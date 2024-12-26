package be.vives.ti.orkesthub.domain.request;

import be.vives.ti.orkesthub.domain.Address;
import be.vives.ti.orkesthub.domain.Gender;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.Name;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public class MemberUpdateRequest {

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Name is required")
    private NameRequest name;

    @NotNull(message = "Address is required")
    private AddressRequest address;

    @NotNull(message = "Email is required")
    @Email(message = "E-mail must be a valid e-mail")
    private String email;

    @NotNull(message = "Phone number is required")
    @Length(max = 21, message = "Phone number must shorter than 21 characters")
    private String phone;

    @Past(message = "Birthdate is required")
    private Date birthdate;

    @NotNull(message = "\"Member Since\" is required")
    private Date memberSince;

    @NotNull(message = "List of instruments is required")
    private String[] instruments;

    @NotNull(message = "Picture is required")
    private String picture;

    @NotNull(message = "\"Is Mangement\" is required")
    @JsonProperty("isManagement")
    private boolean isManagement;

    public MemberUpdateRequest() {}

    public MemberUpdateRequest(Gender gender, NameRequest name, AddressRequest address, String email, String phone, Date birthdate, Date memberSince, String[] instruments, String picture, boolean isManagement) {
        this.gender = gender;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
        this.memberSince = memberSince;
        this.instruments = instruments;
        this.picture = picture;
        this.isManagement = isManagement;
    }

    public Member makeMember() {
        return new Member(gender, name.makeName(), address.makeAddress(), email, phone, birthdate, memberSince, instruments, picture, isManagement);
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

    public Date getMemberSince() {
        return memberSince;
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
