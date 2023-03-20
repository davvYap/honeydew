package sg.edu.nus.iss.honeydew.controller;

import java.io.IOException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.honeydew.model.Cities;
import sg.edu.nus.iss.honeydew.model.City;
import sg.edu.nus.iss.honeydew.model.Dinner;
import sg.edu.nus.iss.honeydew.model.Member;
import sg.edu.nus.iss.honeydew.service.HoneydewService;

@Controller
@RequestMapping
public class HoneydewController {
    @Autowired
    private HoneydewService honeySvc;

    @GetMapping(path = "/register")
    public String registerDinner(Model model, @ModelAttribute Member member, HttpSession session) throws IOException {
        session.invalidate();
        // retrieve cities from API
        Cities c = new Cities();
        Optional<Cities> oc = honeySvc.getCities();
        if (oc != null) {
            c = oc.get();
        }
        model.addAttribute("cities", c.getCities());
        model.addAttribute("member", member);
        model.addAttribute("Singapore", new City("Singapore"));
        model.addAttribute("Other", new City("Other"));
        return "member";
    }

    @PostMapping(path = "/nextRegistration")
    public String nextRegistration(Model model, HttpSession session, @Valid Member member, BindingResult binding,
            @ModelAttribute Dinner dinner) throws IOException {
        if (binding.hasErrors()) {
            Cities c = new Cities();
            Optional<Cities> oc = honeySvc.getCities();
            if (oc != null) {
                c = oc.get();
            }
            model.addAttribute("cities", c.getCities());
            return "member";
        }
        session.setAttribute("member", member);
        model.addAttribute("dinner", dinner);
        return "dinner";
    }
}
