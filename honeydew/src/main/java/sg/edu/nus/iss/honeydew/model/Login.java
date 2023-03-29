package sg.edu.nus.iss.honeydew.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

public class Login implements Serializable {

    @NotEmpty(message = "Login email cannot be empty")
    private String email;

    @NotEmpty(message = "Login password cannot be empty")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
