package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Shirt extends Item implements Serializable {
    @NotNull(message = "Color cannot be null")
    @NotEmpty(message = "Color cannot be empty")
    private String color;

    @Min(value = 1, message = "Minimum quantity is 1")
    @Max(value = 10, message = "Maximum quantity is 10")
    private int quantity;

    // name parent class properties => Item class
    public Shirt() {
        this.setName("shirt");
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public JsonObjectBuilder toJSONObjectBuilder() {
        return Json.createObjectBuilder()
                .add("color", this.getColor())
                .add("quantity", this.getQuantity());
    }

}
