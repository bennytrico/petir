package com.example.petir;

public class Montir {
    private Double rating;
    private Integer wallet;
    private String name,address,email,role,bank,bank_account_name,bank_account_number,id,image;
    private Double latitude,longitude;

    public Montir() {

    }
    public Montir(String name, String address, String email, String role, String bank, String bank_account_name, String bank_account_number
                   , Double latitude, Double longitude, Integer wallet, Double rating) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.bank = bank;
        this.bank_account_name = bank_account_name;
        this.bank_account_number = bank_account_number;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wallet = wallet;
        this.rating = rating;
    }

    public Integer getWallet() {
        return wallet;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_account_name() {
        return bank_account_name;
    }

    public void setBank_account_name(String bank_account_name) {
        this.bank_account_name = bank_account_name;
    }

    public String getBank_account_number() {
        return bank_account_number;
    }

    public void setBank_account_number(String bank_account_number) {
        this.bank_account_number = bank_account_number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
