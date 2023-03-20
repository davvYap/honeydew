package sg.edu.nus.iss.honeydew.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sg.edu.nus.iss.honeydew.model.Cities;
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
            return Optional.of(cities);
        }
        return Optional.empty();
    }

}
