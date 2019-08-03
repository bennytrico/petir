package com.example.petir;

public class Customer {

    private String username,email,role,password,number_handphone, fcm_token;
    private Integer wallet;

    public Customer() {

    }

    public Customer(String name, String email, String role, String password, String number_handphone, Integer wallet) {
        this.username = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.number_handphone = number_handphone;
        this.wallet = wallet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNumber_handphone() {
        return number_handphone;
    }

    public void setNumber_handphone(String number_handphone) {
        this.number_handphone = number_handphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
