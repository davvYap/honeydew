package sg.edu.nus.iss.honeydew.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;

import jakarta.json.JsonArray;
import sg.edu.nus.iss.honeydew.model.Cart;
import sg.edu.nus.iss.honeydew.model.Cities;
import sg.edu.nus.iss.honeydew.model.City;
import sg.edu.nus.iss.honeydew.model.Dinner;
import sg.edu.nus.iss.honeydew.model.DinnerMember;
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
            for (City city : cities.getCities()) {
                System.out.println("CITY >>>>>>>>>>>>>>>>>>>>>>>>" + city.getState());
            }
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

    public Member getMemberById(String id) {
        return honeyRepo.getMemberById(id);
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

    // call honeydew_server get quotations
    public Optional<Quotations> getQuotations(Cart cart) throws IOException {

        String url = "http://localhost:3000/api/quotation";

        // determine what to post -> JsonArray
        JsonArray jsArr = cart.toJSONArray();
        RequestEntity req = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsArr.toString(), String.class);

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> res = template.exchange(req, String.class);

        Quotations quotations = Quotations.createFromJSON(res.getBody());
        if (quotations != null) {
            return Optional.of(quotations);
        }
        return Optional.empty();
    }

    public void debugService(Member member, Dinner dinner) {
        System.out.println("Member >>>>>>>>>>>>>>>>>>>>>>>>" + member);
        System.out.println("Member ID >>>>>>>>>>>>>>>>>>>>>>>>" + member.getId());
        System.out.println("Member name >>>>>>>>>>>>>>>>>>>>>>>>" + member.getName());
        System.out.println("Member state >>>>>>>>>>>>>>>>>>>>>>>>>>" + member.getCity());
        System.out.println("Member dob >>>>>>>>>>>>>>>>>>>>>>>>" + member.getDateOfBirth());
        System.out.println("Member batch >>>>>>>>>>>>>>>>>>>>>>>>" + member.getBatch());
        System.out.println("Member age >>>>>>>>>>>>>>>>>>>>>>>>" + member.getAge());
        System.out.println("Member email >>>>>>>>>>>>>>>>>>>>>>>>" + member.getEmail());
        System.out.println("Member JSON >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + member.toJSON().toString());
        System.out.println("Dinner JSON >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + dinner.toJSON().toString());
    }

}
