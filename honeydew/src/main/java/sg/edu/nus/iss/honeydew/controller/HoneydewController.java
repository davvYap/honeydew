package sg.edu.nus.iss.honeydew.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.honeydew.model.Cart;
import sg.edu.nus.iss.honeydew.model.Cities;
import sg.edu.nus.iss.honeydew.model.Dinner;
import sg.edu.nus.iss.honeydew.model.DinnerMember;
import sg.edu.nus.iss.honeydew.model.Member;
import sg.edu.nus.iss.honeydew.model.Shirt;
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
        Cities c = honeySvc.getCitiesFromOptional();
        model.addAttribute("cities", c.getCities());
        model.addAttribute("member", member);
        return "member";
    }

    @PostMapping(path = "/register/nextRegistration")
    public String nextRegistration(Model model, HttpSession session, @Valid Member member, BindingResult binding,
            @ModelAttribute Dinner dinner) throws IOException {
        if (binding.hasErrors()) {
            Cities c = honeySvc.getCitiesFromOptional();
            model.addAttribute("cities", c.getCities());
            return "member";
        }
        // check if user select other as state/city and enter own city at 'other'
        // section
        if (member.getCity().equalsIgnoreCase("Other")) {
            if (member.getOther().isEmpty() || member.getOther().isBlank()) {
                FieldError fe = new FieldError("member", "other", "Cannot leave blank for other section");
                binding.addError(fe);
                Cities c = honeySvc.getCitiesFromOptional();
                model.addAttribute("cities", c.getCities());
                return "member";
            }
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
        model.addAttribute("member", member);
        model.addAttribute("dinner", dinner);
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

    // NOTE shirt ordering
    @GetMapping(path = "/shirt")
    public String purchaseShirt(Model model, @ModelAttribute Shirt shirt, HttpSession session) {
        Cart c = (Cart) session.getAttribute("cart");
        if (c == null) {
            c = new Cart();
            session.setAttribute("cart", c);
        }
        model.addAttribute("cart", c);
        model.addAttribute("shirt", shirt);
        return "shirt";
    }

    @GetMapping(path = "/shirt/cancel")
    public String cancelShirtOrder(Model model, HttpSession session) {
        session.invalidate();
        return "index";
    }

    @PostMapping(path = "/shirt")
    public String purchaseShirt(Model model, @Valid Shirt shirt, BindingResult binding, HttpSession session) {
        Cart c = (Cart) session.getAttribute("cart");
        if (binding.hasErrors()) {
            model.addAttribute("cart", c);
            model.addAttribute("shirt", shirt);
            return "shirt";
        }
        c.addItem(shirt);
        model.addAttribute("cart", c);
        model.addAttribute("shirt", shirt);
        return "shirt";
    }

    @PostMapping(path = "/shirt/checkout")
    public String checkoutCart(Model model, @ModelAttribute Cart cart, HttpSession session) throws IOException {
        Cart c = (Cart) session.getAttribute("cart");
        model.addAttribute("cart", c);

        List<Member> members = honeySvc.getAllMembers();
        model.addAttribute("members", members);
        return "delivery";
    }

}
