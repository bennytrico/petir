package com.example.petir;

public class Order {
    private String customer_id,address,type_order,transmition,brand,type_motor,date,time,status_order,name_customer,
            no_handphone_customer,number_plate,type_checkup,id;
    private Boolean oli_mesin,oli_gardan,flag_customer_agree,flag_montir_agree;
    private Integer amount;

    private String check_up_list;
    private Montir montir;
    public Order(){

    }
    public void OrderCheckup (String customer_id,String address, String type_order, String transmition,
                  String brand, String type_motor, String date, String time,String status_order,
                  Boolean flag_customer_agree,Boolean flag_montir_agree,Integer amount,Montir montir,String check_up_list,
                  String name_customer, String no_handphone_customer,String number_plate) {
        this.customer_id = customer_id;
        this.address = address;
        this.type_order = type_order;
        this.transmition = transmition;
        this.brand = brand;
        this.type_motor = type_motor;
        this.date = date;
        this.time = time;
        this.status_order = status_order;
        this.flag_customer_agree = flag_customer_agree;
        this.flag_montir_agree = flag_montir_agree;
        this.amount = amount;
        this.montir = montir;
        this.check_up_list = check_up_list;
        this.name_customer = name_customer;
        this.no_handphone_customer = no_handphone_customer;
        this.number_plate = number_plate;
    }

    public Order(String customer_id, String address, String type_order, String transmition,
                 String brand, String type_motor, String date, String time, String status_order, Boolean oli_gardan,
                 Boolean oli_mesin, Boolean flag_customer_agree, Boolean flag_montir_agree, Integer amount, Montir montir,
                 String name_customer, String no_handphone_customer, String number_plate) {
        this.customer_id = customer_id;
        this.address = address;
        this.type_order = type_order;
        this.transmition = transmition;
        this.brand = brand;
        this.type_motor = type_motor;
        this.date = date;
        this.time = time;
        this.status_order = status_order;
        this.oli_gardan = oli_gardan;
        this.oli_mesin = oli_mesin;
        this.flag_customer_agree = flag_customer_agree;
        this.flag_montir_agree = flag_montir_agree;
        this.amount = amount;
        this.montir = montir;
        this.name_customer = name_customer;
        this.no_handphone_customer = no_handphone_customer;
        this.number_plate = number_plate;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType_order() {
        return type_order;
    }

    public void setType_order(String type_order) {
        this.type_order = type_order;
    }

    public String getTransmition() {
        return transmition;
    }

    public void setTransmition(String transmition) {
        this.transmition = transmition;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType_motor() {
        return type_motor;
    }

    public void setType_motor(String type_motor) {
        this.type_motor = type_motor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus_order() {
        return status_order;
    }

    public void setStatus_order(String status_order) {
        this.status_order = status_order;
    }

    public Boolean getOli_mesin() {
        return oli_mesin;
    }

    public void setOli_mesin(Boolean oli_mesin) {
        this.oli_mesin = oli_mesin;
    }

    public Boolean getOli_ganda() {
        return oli_gardan;
    }

    public void setOli_ganda(Boolean oli_gardan) {
        this.oli_gardan = oli_gardan;
    }

    public Boolean getFlag_customer_agree() {
        return flag_customer_agree;
    }

    public void setFlag_customer_agree(Boolean flag_customer_agree) {
        this.flag_customer_agree = flag_customer_agree;
    }

    public Boolean getFlag_montir_agree() {
        return flag_montir_agree;
    }

    public void setFlag_montir_agree(Boolean flag_montir_agree) {
        this.flag_montir_agree = flag_montir_agree;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Montir getMontir() {
        return montir;
    }

    public void setMontir(Montir montir) {
        this.montir = montir;
    }

    public String getName_customer() {
        return name_customer;
    }

    public void setName_customer(String name_customer) {
        this.name_customer = name_customer;
    }

    public String getNo_handphone_customer() {
        return no_handphone_customer;
    }

    public void setNo_handphone_customer(String no_handphone_customer) {
        this.no_handphone_customer = no_handphone_customer;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public String getType_checkup() {
        return type_checkup;
    }

    public void setType_checkup(String type_checkup) {
        this.type_checkup = type_checkup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheck_up_list() {
        return check_up_list;
    }

    public void setCheck_up_list(String check_up_list) {
        this.check_up_list = check_up_list;
    }
}
