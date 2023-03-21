package sg.edu.nus.iss.honeydew.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sg.edu.nus.iss.honeydew.model.Cities;
import sg.edu.nus.iss.honeydew.model.City;
import sg.edu.nus.iss.honeydew.model.Dinner;
import sg.edu.nus.iss.honeydew.model.DinnerMember;
import sg.edu.nus.iss.honeydew.model.Member;
import sg.edu.nus.iss.honeydew.repository.HoneydewRepository;

@Service
public class HoneydewService {
    @Autowired
    private HoneydewRepository honeyRepo;

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
