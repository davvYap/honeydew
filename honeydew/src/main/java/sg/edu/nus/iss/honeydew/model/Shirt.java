package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Shirt extends Item implements Serializable {

    @NotNull(message = "Color cannot be null")
    @NotEmpty(message = "Color cannot be empty")
    private String color = "Green";

    @NotNull(message = "Size cannot be null")
    @NotEmpty(message = "Size cannot be empty")
    private String size;

    @Min(value = 1, message = "Minimum quantity is 1")
    @Max(value = 10, message = "Maximum quantity is 10")
    private int quantity;

    private double price;

    // name parent class properties => Item class
    public Shirt() {
        this.setName("shirt");
    }

    public String getColor() {
        return color.toUpperCase();
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public JsonObjectBuilder toJSONObjectBuilder() {
        return Json.createObjectBuilder()
                .add("color", this.getColor())
                .add("size", this.getSize())
                .add("quantity", this.getQuantity())
                .add("price", this.getPrice());
    }

    public static Shirt createFromJSON(JsonObject jsObj) {
        Shirt shirt = new Shirt();
        shirt.setColor(jsObj.getString("color"));
        shirt.setSize(jsObj.getString("size"));
        shirt.setPrice(jsObj.getJsonNumber("price").doubleValue());
        return shirt;
    }

}
