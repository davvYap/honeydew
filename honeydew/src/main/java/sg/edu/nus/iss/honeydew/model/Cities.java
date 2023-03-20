package sg.edu.nus.iss.honeydew.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Cities implements Serializable {
    private List<City> cities = new LinkedList<>();

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public static Cities createFromJSON(String json) throws IOException {
        Cities cities = new Cities();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader jsReader = Json.createReader(is);
            JsonArray jsArray = jsReader.readArray();
            List<City> allCities = jsArray.stream()
                    .map(city -> (JsonObject) city)
                    .map(city -> City.createFromJSON(city))
                    .toList();
            cities.setCities(allCities);
        }
        return cities;
    }

}
