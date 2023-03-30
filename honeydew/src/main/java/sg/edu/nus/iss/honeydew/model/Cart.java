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

    private String invoiceId;

    private PO po;

    @NotEmpty(message = "Must at least add one item")
    private List<Item> items = new LinkedList<>();

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public PO getPo() {
        return po;
    }

    public void setPo(PO po) {
        this.po = po;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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
                .add("invoiceId", this.invoiceId)
                .add("email", this.po.getEmail())
                .add("address", this.po.getAddress())
                .add("items", arrBuilder)
                .add("total_cost", this.po.getTotalCost())
                .build();
    }

    // to pass to honeydew_server as json array
    public JsonArray toJSONarray() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfItems = this.getItems().stream()
                .map(item -> (Shirt) item)
                .map(item -> item.toJSONObjectBuilder())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfItems) {
            arrBuilder.add(jsonObjectBuilder);
        }
        // to pass to only array
        return arrBuilder.build();
    }

}
