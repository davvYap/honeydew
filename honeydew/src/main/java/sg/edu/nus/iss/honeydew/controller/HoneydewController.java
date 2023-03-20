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
import sg.edu.nus.iss.honeydew.model.DinnerMember;
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

    @PostMapping(path = "/register/nextRegistration")
    public String nextRegistration(Model model, HttpSession session, @Valid Member member, BindingResult binding,
            @ModelAttribute Dinner dinner) throws IOException {
        if (binding.hasErrors()) {
            Cities c = new Cities();
            Optional<Cities> oc = honeySvc.getCities();
            if (oc != null) {
                c = oc.get();
            }
            model.addAttribute("cities", c.getCities());
            model.addAttribute("Singapore", new City("Singapore"));
            model.addAttribute("Other", new City("Other"));
            return "member";
        }
        session.setAttribute("member", member);
        model.addAttribute("dinner", dinner);
        return "dinner";
    }

    @PostMapping(path = "/register/nextRegistration/confirmDetails")
    public String confirmDetails(Model model, @Valid Dinner dinner, BindingResult binding, HttpSession session) {
        if (binding.hasErrors()) {
            return "dinner";
        }
        Member member = (Member) session.getAttribute("member");
        session.setAttribute("dinner", dinner);
        System.out.println("Member >>>>>>>>>>>>>>>>>>>>>>>>" + member);
        System.out.println("Member ID >>>>>>>>>>>>>>>>>>>>>>>>" + member.getId());
        System.out.println("Member name >>>>>>>>>>>>>>>>>>>>>>>>" + member.getName());
        System.out.println("Member state >>>>>>>>>>>>>>>>>>>>>>>>>>" + member.getCity().getState());
        System.out.println("Member dob >>>>>>>>>>>>>>>>>>>>>>>>" + member.getDateOfBirth());
        System.out.println("Member batch >>>>>>>>>>>>>>>>>>>>>>>>" + member.getBatch());
        System.out.println("Member age >>>>>>>>>>>>>>>>>>>>>>>>" + member.getAge());
        System.out.println("Member email >>>>>>>>>>>>>>>>>>>>>>>>" + member.getEmail());
        System.out.println("Dinner >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + dinner.toJSONObject().toString());
        model.addAttribute("member", member);
        model.addAttribute("dinner", dinner);
        model.addAttribute("state", member.getCity().getState());
        return "details";
    }

    @PostMapping(path = "/confirmed")
    public String completedRegistration(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        Dinner dinner = (Dinner) session.getAttribute("dinner");
        DinnerMember dm = new DinnerMember(member, dinner);
        honeySvc.saveMember(member);
        honeySvc.saveDinnerDetails(dm);
        model.addAttribute("name", member.getName());
        return "welcome";
    }
}
