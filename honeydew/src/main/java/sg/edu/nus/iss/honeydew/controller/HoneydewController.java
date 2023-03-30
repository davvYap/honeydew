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
import sg.edu.nus.iss.honeydew.model.Login;
import sg.edu.nus.iss.honeydew.model.Member;
import sg.edu.nus.iss.honeydew.model.PO;
import sg.edu.nus.iss.honeydew.model.Quotations;
import sg.edu.nus.iss.honeydew.model.Shirt;
import sg.edu.nus.iss.honeydew.service.HoneydewService;

@Controller
@RequestMapping
public class HoneydewController {
    @Autowired
    private HoneydewService honeySvc;

    @GetMapping(path = { "/home", "/" })
    public String getHome(Model model, HttpSession session, @ModelAttribute Login login) {
        // check if current status is login or not
        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            System.out.println("Member is login!");
            int currTime = honeySvc.getTime();
            model.addAttribute("currTime", currTime);
            model.addAttribute("islogin", true);
            model.addAttribute("name", member.getName());
            return "index";
        }
        System.out.println("Member not login!");
        model.addAttribute("login", login);
        model.addAttribute("islogin", false);
        return "index";
    }

    @GetMapping(path = "/signUp")
    public String signUpAccount(Model model, @ModelAttribute Member member, HttpSession session) throws IOException {
        session.invalidate();
        Cities c = honeySvc.getCitiesFromOptional();
        model.addAttribute("cities", c.getCities());
        model.addAttribute("member", member);
        return "member";
    }

    @PostMapping(path = "/signUp")
    public String completeSignUpAccount(Model model, HttpSession session, @Valid Member member, BindingResult binding,
            @ModelAttribute Login login)
            throws IOException {
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

        // check if password is consistent
        if (!member.getPassword().equals(member.getConfirmPassword())) {
            FieldError fe = new FieldError("member", "password", "Password not consistent");
            binding.addError(fe);
            Cities c = honeySvc.getCitiesFromOptional();
            model.addAttribute("cities", c.getCities());
            return "member";
        }
        honeySvc.saveMember(member);
        session.setAttribute("member", member);
        model.addAttribute("name", member.getName());
        model.addAttribute("islogin", true);
        model.addAttribute("login", login);
        return "index";
    }

    @PostMapping(path = "/login")
    public String login(Model model, HttpSession session, @ModelAttribute Login login, BindingResult binding)
            throws IOException {
        if (honeySvc.authenticateLogin(login)) {
            System.out.println("Member authenticated!");
            Member m = honeySvc.getMemberByEmail(login.getEmail()).get();
            session.setAttribute("member", m);
            int currTime = honeySvc.getTime();
            model.addAttribute("currTime", currTime);
            model.addAttribute("islogin", true);
            model.addAttribute("name", m.getName());
            return "index";
        }
        System.out.println("Member not authenticated!");
        model.addAttribute("login", login);
        model.addAttribute("islogin", false);
        model.addAttribute("loginAlert", true);
        return "index";
    }

    @GetMapping(path = "/logout")
    public String logout(Model model, HttpSession session, @ModelAttribute Login login) {
        session.invalidate();
        model.addAttribute("login", login);
        return "index";
    }

    // register dinner
    @GetMapping(path = "/register")
    public String registerDinner(Model model, @ModelAttribute Dinner dinner, HttpSession session) throws IOException {
        Member m = (Member) session.getAttribute("member");
        model.addAttribute("name", m.getName());
        model.addAttribute("dinner", dinner);
        return "dinner";
    }

    @PostMapping(path = "/register/confirmDetails")
    public String confirmDetails(Model model, @Valid Dinner dinner, BindingResult binding, HttpSession session) {
        Member m = (Member) session.getAttribute("member");
        if (binding.hasErrors()) {
            model.addAttribute("name", m.getName());
            return "dinner";
        }

        // check if attending dinner but number is 0
        if (dinner.getIsAttending() && dinner.getNumberOfAttendance() == 0) {
            FieldError fe = new FieldError("dinner", "numberOfAttendance", "Cannot have 0 attendees");
            binding.addError(fe);
            model.addAttribute("name", m.getName());
            return "dinner";
        }

        session.setAttribute("dinner", dinner);
        model.addAttribute("member", m);
        model.addAttribute("name", m.getName());
        model.addAttribute("dinner", dinner);
        return "details";
    }

    @PostMapping(path = "/confirmed")
    public String completedRegistration(Model model, HttpSession session, @ModelAttribute Login login) {
        Member member = (Member) session.getAttribute("member");
        Dinner dinner = (Dinner) session.getAttribute("dinner");
        DinnerMember dm = new DinnerMember(member, dinner);
        honeySvc.saveDinnerDetails(dm);
        model.addAttribute("name", member.getName());
        int currTime = honeySvc.getTime();
        model.addAttribute("currTime", currTime);
        model.addAttribute("islogin", true);
        return "index";
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

        // c.addItem(shirt);
        c.setItems(honeySvc.addItems(c, shirt));
        model.addAttribute("cart", c);
        model.addAttribute("shirt", shirt);
        return "shirt";
    }

    @PostMapping(path = "/shirt/checkout")
    public String checkoutCart(Model model, HttpSession session, @ModelAttribute PO po)
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

        model.addAttribute("cart", c);
        model.addAttribute("po", po);
        List<Member> members = honeySvc.getAllMembers();
        model.addAttribute("members", members);
        return "delivery";
    }

    @PostMapping(path = "/shirt/checkout/complete")
    public String completeCheckout(Model model, HttpSession session, @ModelAttribute @Valid PO po,
            BindingResult binding)
            throws IOException {
        Cart c = (Cart) session.getAttribute("cart");

        if (binding.hasErrors()) {
            System.out.println("PO has error!");
            model.addAttribute("cart", c);
            model.addAttribute("po", po);
            List<Member> members = honeySvc.getAllMembers();
            model.addAttribute("members", members);
            return "delivery";
        }

        // semantic check
        if (po.getAddress().isBlank() || po.getAddress().isEmpty()) {
            ObjectError err = new ObjectError("poError", "Address cannot be empty");
            binding.addError(err);
            model.addAttribute("cart", c);
            model.addAttribute("po", po);
            List<Member> members = honeySvc.getAllMembers();
            model.addAttribute("members", members);
            return "delivery";
        }

        c.setPo(po);

        // create quotation class to retrieve shirt cost based on json string
        Quotations quotations = honeySvc.getQuotations(c).get();
        Double totalCost = honeySvc.getTotalCost(quotations, c);
        int numberOfShirts = honeySvc.getTotalQuantity(quotations);
        // IMPORTANT issue below
        // String memberName = honeySvc.getMemberByEmail(po.getMemberId()).getName();
        String deliveryAddress = po.getAddress();
        model.addAttribute("quotations", quotations);
        model.addAttribute("numberOfShirts", numberOfShirts);
        model.addAttribute("total", totalCost);
        // model.addAttribute("memberName", memberName);
        model.addAttribute("deliveryAddress", deliveryAddress);

        honeySvc.saveShirtOrder(c);

        return "invoice";
    }

}
