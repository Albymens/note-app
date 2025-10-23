package com.albymens.note_app.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;
    @NotBlank(message = "Password is required")
    private String password;

    public @NotBlank(message = "Username or email is required") String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(@NotBlank(message = "Username or email is required") String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public @NotBlank(message = "Password is required") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") String password) {
        this.password = password;
    }
}
