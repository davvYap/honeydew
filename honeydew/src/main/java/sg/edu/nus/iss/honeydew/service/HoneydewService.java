package sg.edu.nus.iss.honeydew.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

import jakarta.json.JsonArray;
import sg.edu.nus.iss.honeydew.model.Cart;
import sg.edu.nus.iss.honeydew.model.Cities;
import sg.edu.nus.iss.honeydew.model.City;
import sg.edu.nus.iss.honeydew.model.DinnerMember;
import sg.edu.nus.iss.honeydew.model.Item;
import sg.edu.nus.iss.honeydew.model.Login;
import sg.edu.nus.iss.honeydew.model.Member;
import sg.edu.nus.iss.honeydew.model.Quotations;
import sg.edu.nus.iss.honeydew.model.Shirt;
import sg.edu.nus.iss.honeydew.repository.HoneydewRepository;

@Service
public class HoneydewService {
    @Autowired
    private HoneydewRepository honeyRepo;

    private static final String[] SHIRT_COLORS = { "yellow", "green", "black", "red", "blue", "orange" };

    private static final String[] SHIRT_SIZES = { "XS", "S", "M", "L", "XL", "XXL" };

    public void saveMember(Member member) {
        honeyRepo.saveMember(member);
    }

    public void saveDinnerDetails(DinnerMember dm) {
        honeyRepo.saveDinnerDetails(dm);
    }

    // to retrieve malaysia states information
    public Optional<Cities> getCities() throws IOException {
        String url = "https://jianliew.me/malaysia-api/state/v1/all.json";

        RequestEntity req = RequestEntity
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(req, String.class);
        Cities cities = Cities.createFromJSON(response.getBody());

        if (cities != null) {
            // for (City city : cities.getCities()) {
            // System.out.println("CITY >>>>>>>>>>>>>>>>>>>>>>>>" + city.getState());
            // }
            return Optional.of(cities);
        }
        return Optional.empty();
    }

    // convert to cities object and pass to controller
    public Cities getCitiesFromOptional() throws IOException {
        Cities c = new Cities();
        Optional<Cities> oc = getCities();
        if (oc != null) {
            c = oc.get();
        }
        return c;
    }

    public List<Member> getAllMembers() throws IOException {
        return honeyRepo.getAllMembers();
    }

    public Optional<Member> getMemberByEmail(String email) throws IOException {
        return honeyRepo.getMemberByEmail(email);
    }

    public boolean authenticateLogin(Login login) throws IOException {
        return honeyRepo.authenticateLogin(login);
    }

    // validate shirt colors to ensure can safely call honeydew_server quotation
    public List<ObjectError> validateShirtOrder(Shirt shirt) {
        List<ObjectError> errors = new LinkedList<>();
        FieldError error;
        int count = 0;
        for (String color : SHIRT_COLORS) {
            if (shirt.getColor().toLowerCase().equalsIgnoreCase(color)) {
                continue;
            } else {
                count++;
            }
        }
        if (count >= 6) {
            error = new FieldError("shirt", "color",
                    "We do not have %s color shirt".formatted(shirt.getColor().toLowerCase()));
            errors.add(error);
        }
        return errors;
    }

    // aggregate similar items with similar color and size
    public List<Item> addItems(Cart c, Item item) {
        List<Item> items = c.getItems();

        if (items.size() == 0) {
            items.add(item);
            return items;
        }

        Shirt newShirt = (Shirt) item;
        for (Item i : items) {
            Shirt existingShirt = (Shirt) i;
            if (existingShirt.getColor().equalsIgnoreCase(newShirt.getColor())
                    && existingShirt.getSize().equalsIgnoreCase(newShirt.getSize())) {
                int newQuantity = existingShirt.getQuantity() + newShirt.getQuantity();
                newShirt.setQuantity(newQuantity);
                items.remove(existingShirt);
                items.add(newShirt);
                return items;
            }
        }

        items.add(item);
        return items;
    }

    // call honeydew_server get quotations
    public Optional<Quotations> getQuotationsFromAPI(Cart cart) throws IOException {

        String url = "http://localhost:3000/api/quotation";
        // String url =
        // "https://honeydewserver-production.up.railway.app/api/quotation";

        // determine what to post -> JsonArray
        JsonArray cartJSON = cart.toJSONarray();
        RequestEntity req = RequestEntity
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cartJSON.toString(), String.class);

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> res;
        // in case http server issue
        try {
            res = template.exchange(req, String.class);

        } catch (Exception e) {
            throw e;
        }

        Quotations quotations = Quotations.createFromJSON(res.getBody());
        if (quotations != null) {
            return Optional.of(quotations);
        }
        return Optional.empty();
    }

    // generate shirt quotations without honeydew_server
    public double calculateShirtCost(Shirt shirt) {
        double cost = 0;
        String color = shirt.getColor().toLowerCase();
        switch (color) {
            case "yellow", "green":
                cost += 30.5;
                break;
            case "orange", "red":
                cost += 35;
                break;
            case "black", "blue":
                cost += 40.5;
                break;
        }
        switch (shirt.getSize()) {
            case "XS", "S":
                cost *= 1;
                break;
            case "M", "L":
                cost *= 1.2;
                break;
            case "XL", "XXL":
                cost *= 1.5;
                break;
            default:
                cost *= 1;
        }
        double roundedCost = Math.round(cost * 100.0) / 100.0;
        shirt.setPrice(roundedCost);
        return roundedCost;
    }

    // generate Quotation without honeydew_server
    public Quotations getQuotations(Cart cart) {
        Quotations q = new Quotations();
        q.setQuoteId(Quotations.generateQuotationId());
        q.setQuotations(cart.getItems().stream().map(s -> (Shirt) s).toList());
        for (Shirt s : q.getQuotations()) {
            s.setPrice(calculateShirtCost(s));
        }
        return q;
    }

    // calculate total cost
    public double getTotalCost(Quotations q, Cart c) {

        c.setInvoiceId(q.getQuoteId());
        double total = 0;
        for (Item item : c.getItems()) {
            Shirt shirt = (Shirt) item;
            int quantity = shirt.getQuantity();
            for (Shirt qShirt : q.getQuotations()) {
                if (shirt.getColor().equalsIgnoreCase(qShirt.getColor())
                        && shirt.getSize().equalsIgnoreCase(qShirt.getSize())) {
                    qShirt.setQuantity(quantity);
                    total += qShirt.getQuantity() * qShirt.getPrice();
                }
            }
        }
        return total;
    }

    // calculate total shirt quantity
    public int getTotalQuantity(Quotations q) {
        int total = 0;
        for (Shirt shirt : q.getQuotations()) {
            total += shirt.getQuantity();
        }
        return total;
    }

    public void saveShirtOrder(Cart c) {
        honeyRepo.saveShirtOrder(c);
    }

    // get hour of the day
    public int getTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

}
