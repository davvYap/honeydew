package sg.edu.nus.iss.honeydew.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
import sg.edu.nus.iss.honeydew.model.Quotations;
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

    @GetMapping(path = "/shirt/cancel")
    public String cancelShirtOrder(Model model, HttpSession session) {
        session.invalidate();
        return "index";
    }

    // NOTE back button from delivery view
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

    @PostMapping(path = "/shirt")
    public String purchaseShirt(Model model, @Valid Shirt shirt, BindingResult binding, HttpSession session) {
        Cart c = (Cart) session.getAttribute("cart");
        if (binding.hasErrors()) {
            model.addAttribute("cart", c);
            model.addAttribute("shirt", shirt);
            return "shirt";
        }

        // validate shirt color
        List<ObjectError> errors = honeySvc.validateShirtOrder(shirt);
        if (!errors.isEmpty()) {
            System.out.println("Color has error!");
            for (ObjectError objectError : errors) {
                binding.addError(objectError);
            }
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
    public String checkoutCart(Model model, HttpSession session, @ModelAttribute Cart cart)
            throws IOException {
        Cart c = (Cart) session.getAttribute("cart");

        // NOTE check if cart is empty or missing
        if (c == null || c.getItems().isEmpty()) {
            c = new Cart();
            session.setAttribute("cart", c);
            model.addAttribute("cart", c);
            model.addAttribute("shirt", new Shirt());
            return "shirt";
        }

        // NOTE cause we need to bind the cart model to thymeleaf, thus we have to give
        // the items list from c to cart
        cart.setItems(c.getItems());
        model.addAttribute("cart", cart);
        List<Member> members = honeySvc.getAllMembers();
        model.addAttribute("members", members);
        return "delivery";
    }

    @PostMapping(path = "/shirt/checkout/complete")
    public String completeCheckout(Model model, HttpSession session, @ModelAttribute @Valid Cart cart,
            BindingResult binding)
            throws IOException {
        Cart c = (Cart) session.getAttribute("cart");

        // NOTE from here we pass back the binded model cart to c
        c.setMemberId(cart.getMemberId());
        c.setAddress(cart.getAddress());
        System.out.println("items >>>>>>>>>>>>>>>" + c.getItems());
        System.out.println("Member >>>>>>>>>>>>>>>> " + c.getMemberId());
        System.out.println("Address >>>>>>>>>>>>>>>> " + c.getAddress());

        if (c.getAddress() == null || c.getAddress().isEmpty() ||
                c.getAddress().isBlank()) {
            System.out.println("Cart has error!");
            FieldError fe = new FieldError("cart", "address", "Address cannot be empty");
            binding.addError(fe);
            model.addAttribute("cart", c);
            List<Member> members = honeySvc.getAllMembers();
            model.addAttribute("members", members);
            return "delivery";
        }

        // handle object error generated from rest api endpoint when giving the invoice
        // create quotation class to retrieve shirt cost based on json string
        Quotations quotations = honeySvc.getQuotations(c).get();
        model.addAttribute("quotations", quotations);
        return "invoice";
    }

}
