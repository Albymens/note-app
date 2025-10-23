package com.albymens.note_app.dto;

public class AuthRequest {
    private String usernameOrEmail;
    private String password;
    private String email;
    private String username;

    public AuthRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public AuthRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.usernameOrEmail = username;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

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
