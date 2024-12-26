package be.vives.ti.orkesthub.domain;

import jakarta.validation.constraints.NotNull;

public class Address {

    private String street;
    private String number;
    private String city;
    private int  postalcode;

    public Address() {
    }

    public Address(String street, String number, String city, int postalcode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.postalcode = postalcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(int postalcode) {
        this.postalcode = postalcode;
    }
}
