package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Dinner implements Serializable {
    @NotNull(message = "Please select an option")
    private Boolean isAttending = true;

    @Min(value = 0, message = "Minimum attendee is 0")
    @Max(value = 10, message = "Maximum attendees is 10")
    private int numberOfAttendance;

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

    public int getNumberOfAttendance() {
        return numberOfAttendance;
    }

    public void setNumberOfAttendance(int numberOfAttendance) {
        this.numberOfAttendance = numberOfAttendance;
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
                .add("number_of_attendance", this.getNumberOfAttendance())
                .add("is_alergic", this.getIsAllergic() ? "Yes" : "No")
                .add("is_vegetarian", this.getIsVegetarian() ? "Yes" : "No")
                .add("allergic_food", this.getAllergicFood());
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("is_attending", this.getIsAttending() ? "Yes" : "No")
                .add("number_of_attendance", this.getNumberOfAttendance())
                .add("is_alergic", this.getIsAllergic() ? "Yes" : "No")
                .add("is_vegetarian", this.getIsVegetarian() ? "Yes" : "No")
                .add("allergic_food", this.getIsAllergic() ? this.getAllergicFood() : "No allergy")
                .build();
    }

}
