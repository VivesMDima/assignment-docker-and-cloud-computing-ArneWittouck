package be.vives.ti.orkesthub.domain.request;

import be.vives.ti.orkesthub.domain.Address;
import be.vives.ti.orkesthub.domain.Gender;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.Name;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public class MemberRequest {

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Name is required")
    @Valid
    private NameRequest name;

    @NotNull(message = "Address is required")
    @Valid
    private AddressRequest address;

    @Email(message = "Must be a valid e-mail")
    private String email;

    @Length(min = 4, max = 21, message = "Phone number must be longer than 4 characters and shorter than 21 characters")
    private String phone;

    @Past(message = "Birthdate cannot be a future date")
    private Date birthdate;

    private String[] instruments;

    private String picture;

    @JsonProperty("isManagement")
    private boolean isManagement;

    public MemberRequest() {}

    public MemberRequest(Gender gender, NameRequest name, AddressRequest address, String email, String phone, Date birthdate, String[] instruments, String picture, boolean isManagement) {
        this.gender = gender;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
        this.instruments = instruments;
        this.picture = picture;
        this.isManagement = isManagement;
    }

    public Member makeMember() {
        return new Member(gender, name.makeName(), address.makeAddress(), email, phone, birthdate, new Date(), instruments, picture, isManagement);
    }

    public Gender getGender() {
        return gender;
    }

    public Name getName() {
        return name.makeName();
    }

    public Address getAddress() {
        return address.makeAddress();
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public String[] getInstruments() {
        return instruments;
    }

    public String getPicture() {
        return picture;
    }

    @JsonProperty("isManagement")
    public boolean isManagement() {
        return isManagement;
    }
}
