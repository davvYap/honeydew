package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class City implements Serializable {
    private String state;
    private String capital;
    private String abbreviation;

    public City() {
    }

    public City(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static City createFromJSON(JsonObject jsObj) {
        City city = new City();
        city.setState(jsObj.getString("state"));
        city.setAbbreviation(jsObj.getString("abbreviation"));
        city.setCapital(jsObj.getString("capital"));
        return city;
    }

    public JsonObjectBuilder toJSONObjectBuilder() {
        return Json.createObjectBuilder()
                .add("state", this.getState())
                .add("capital", this.getCapital())
                .add("abbreviation", this.getAbbreviation());
    }

}
