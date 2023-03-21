package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.NotNull;

public class Dinner implements Serializable {
    @NotNull(message = "Please select an option")
    private Boolean isAttending = true;

    @NotNull(message = "Please select an option")
    private Boolean isVegetarian = false;

    @NotNull(message = "Please select an option")
    private Boolean isAllergic = false;

    private String allergicFood;

    public Boolean getIsAttending() {
        return isAttending;
    }

    public void setIsAttending(Boolean isAttending) {
        this.isAttending = isAttending;
    }

    public Boolean getIsAllergic() {
        return isAllergic;
    }

    public void setIsAllergic(Boolean isAllergic) {
        this.isAllergic = isAllergic;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getAllergicFood() {
        if (this.getIsAllergic()) {
            return allergicFood;
        }
        return "No allergy";
    }

    public void setAllergicFood(String allergicFood) {
        this.allergicFood = allergicFood;
    }

    public JsonObjectBuilder toJSONoObjectBuilder() {
        return Json.createObjectBuilder()
                .add("is_attending", this.getIsAttending() ? "Yes" : "No")
                .add("is_alergic", this.getIsAllergic() ? "Yes" : "No")
                .add("is_vegetarian", this.getIsVegetarian() ? "Yes" : "No")
                .add("allergic_food", this.getAllergicFood());
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("is_attending", this.getIsAttending() ? "Yes" : "No")
                .add("is_alergic", this.getIsAllergic() ? "Yes" : "No")
                .add("is_vegetarian", this.getIsVegetarian() ? "Yes" : "No")
                .add("allergic_food", this.getIsAllergic() ? this.getAllergicFood() : "No allergy")
                .build();
    }

}
