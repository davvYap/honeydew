package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Member implements Serializable {
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 64, message = "Must be valid name")
    private String name;

    @NotNull(message = "Batch cannot be null")
    @NotEmpty(message = "Batch cannot be empty")
    private String batch;

    @NotNull(message = "Please select a city")
    private City city;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Must be a valid phone number")
    private String phoneNum;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Past(message = "Date of birth must not be future date")
    @NotNull(message = "Date of birth cannot be null")
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate dateOfBirth;

    private int age;

    private String id;

    // constructor with ID
    public Member() {
        this.id = generateId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        int calculateAge = 0;
        if (dateOfBirth != null) {
            calculateAge = Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
        this.age = calculateAge;
    }

    public int getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String generateId() {
        String id = UUID.randomUUID().toString().substring(0, 6);
        return id;
    }

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("batch", this.getBatch())
                .add("city", this.getCity().toJSON())
                .add("phone", this.getPhoneNum())
                .add("email", this.getEmail())
                .add("date of birth", this.getDateOfBirth().toString());
    }

    public JsonObject toJSONObject() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("batch", this.getBatch())
                .add("city", this.getCity().toJSON())
                .add("phone", this.getPhoneNum())
                .add("email", this.getEmail())
                .add("date of birth", this.getDateOfBirth().toString())
                .build();
    }

}
