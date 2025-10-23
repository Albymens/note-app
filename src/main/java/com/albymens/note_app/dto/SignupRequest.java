package com.albymens.note_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @Email
    @NotBlank(message = "Email is required")
    @Size(max = 50)
    private String email;

    @Size(max = 20)
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank
    @Size(min = 6, max = 20, message = "Password must be more than 6 letters")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
