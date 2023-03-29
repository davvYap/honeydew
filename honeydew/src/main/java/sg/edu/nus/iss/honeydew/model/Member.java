package sg.edu.nus.iss.honeydew.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
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
    private String city;

    private String other;

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

    public String password;

    private String confirmPassword;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    public String getFinalCity() {
        if (this.getCity().equalsIgnoreCase("Other")) {
            return this.getOther();
        }
        return this.getCity();
    }

    public static Member createFromMember(Member m) {
        Member member = new Member();
        member.setName(m.getName());
        member.setBatch(m.getBatch());
        member.setCity(m.getCity());
        member.setDateOfBirth(m.getDateOfBirth());
        member.setId(m.getId());
        member.setOther(m.getOther());
        member.setPhoneNum(m.getPhoneNum());
        member.setEmail(m.getEmail());
        member.setPassword(m.getPassword());
        return member;
    }

    public JsonObjectBuilder toJSONObjectBuilder() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("name", this.getName().toUpperCase())
                .add("batch", this.getBatch())
                .add("city", this.getFinalCity())
                .add("phone", this.getPhoneNum())
                .add("email", this.getEmail())
                .add("date of birth", this.getDateOfBirth().toString())
                .add("password", this.getPassword());
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("name", this.getName().toUpperCase())
                .add("batch", this.getBatch())
                .add("city", this.getFinalCity())
                .add("phone", this.getPhoneNum())
                .add("email", this.getEmail())
                .add("date_of_birth", this.getDateOfBirth().toString())
                .add("password", this.getPassword())
                .build();
    }

    public static Member createFromJSON(String json) throws IOException {
        Member member = new Member();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject jsObj = reader.readObject();
            member.setId(jsObj.getString("id"));
            member.setName(jsObj.getString("name"));
            member.setBatch(jsObj.getString("batch"));
            member.setCity(jsObj.getString("city"));
            member.setPhoneNum(jsObj.getString("phone"));
            member.setEmail(jsObj.getString("email"));
            member.setPassword(jsObj.getString("password"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dob = LocalDate.parse(jsObj.getString("date_of_birth"), formatter);
            member.setDateOfBirth(dob);
        }
        return member;
    }

}
