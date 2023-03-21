package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Cart implements Serializable {

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private Member member;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    private List<Item> items = new LinkedList<>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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
                .add("name", this.getMember().getName())
                .add("address", this.getAddress())
                .add("phone_number", this.getMember().getPhoneNum())
                .add("email", this.getMember().getEmail())
                .add("items", arrBuilder)
                .build();
    }
}
