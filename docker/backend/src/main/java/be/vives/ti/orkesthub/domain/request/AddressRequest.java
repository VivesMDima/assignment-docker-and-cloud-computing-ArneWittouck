package be.vives.ti.orkesthub.domain.request;

import be.vives.ti.orkesthub.domain.Address;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddressRequest {
    @NotBlank(message = "Street must not be blank")
    private String street;
    @NotBlank(message = "Number must not be blank")
    private String number;
    @NotBlank(message = "City must not be blank")
    private String city;
    @Min(value = 0, message = "Postal code must be a positive number")
    @Max(value = 99950, message = "Postal code must be lower than 99950")
    private int  postalcode;

    public AddressRequest() {}

    public AddressRequest(String street, String number, String city, int postalcode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.postalcode = postalcode;
    }

    public Address makeAddress() {
        return new Address(street, number, city, postalcode);
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getCity() {
        return city;
    }

    public int getPostalcode() {
        return postalcode;
    }
}
