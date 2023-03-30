package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PO implements Serializable {

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    private double totalCost;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                .add("email", this.email)
                .add("address", this.address)
                .add("total_cost", this.totalCost);
    }

}
