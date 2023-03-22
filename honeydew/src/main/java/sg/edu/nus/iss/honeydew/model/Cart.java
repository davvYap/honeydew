package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Cart implements Serializable {

    @NotNull(message = "Profile cannot be null")
    private String memberId;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @NotEmpty(message = "Must at least add one item")
    private List<Item> items = new LinkedList<>();

    private double totalCost;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public JsonObject toJSON() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfItems = this.getItems().stream()
                .map(item -> (Shirt) item)
                .map(item -> item.toJSONObjectBuilder())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfItems) {
            arrBuilder.add(jsonObjectBuilder);
        }

        return Json.createObjectBuilder()
                .add("member_id", this.getMemberId())
                .add("address", this.getAddress())
                .add("items", arrBuilder)
                .add("total_cost", this.getTotalCost())
                .build();
    }

    // to pass to honeydew_server as json array
    public JsonArray toJSONArray() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfItems = this.getItems().stream()
                .map(item -> (Shirt) item)
                .map(item -> item.toJSONObjectBuilder())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfItems) {
            arrBuilder.add(jsonObjectBuilder);
        }
        return Json.createArrayBuilder()
                .add(arrBuilder)
                .build();
    }

}
