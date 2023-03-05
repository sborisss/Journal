package com.example.journal;

public class UserPOJO {
    public String username;
    public String userclass;
    public String password;

    public String getUsername() {
        return username;
    }
    public String getUserclass() {
        return userclass;
    }
    public String getPassword() { return password; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserclass(String userclass) {
        this.userclass = userclass;
    }

    public void setPassword(String password) { this.password = password; }
}
