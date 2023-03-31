package sg.edu.nus.iss.honeydew.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Quotations implements Serializable {
    private String quoteId;
    private List<Shirt> quotations = new LinkedList<>();

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public List<Shirt> getQuotations() {
        return quotations;
    }

    public void setQuotations(List<Shirt> quotations) {
        this.quotations = quotations;
    }

    public static String generateQuotationId() {
        String id = UUID.randomUUID().toString().substring(0, 6);
        return id;
    }

    public static Quotations createFromJSON(String json) throws IOException {
        Quotations q = new Quotations();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader r = Json.createReader(is);
            JsonObject jsObj = r.readObject();
            q.setQuoteId(jsObj.getString("quoteId"));

            List<Shirt> itemCost = jsObj.getJsonArray("quotations").stream()
                    .map(item -> (JsonObject) item)
                    .map(item -> Shirt.createFromJSON(item))
                    .toList();
            q.setQuotations(itemCost);
        }
        return q;
    }
}
