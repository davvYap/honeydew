package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PO implements Serializable {
    @NotNull(message = "Profile cannot be null")
    private String memberId;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    private double totalCost;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
                .add("member_id", this.memberId)
                .add("address", this.address)
                .add("total_cost", this.totalCost);
    }

}
