package be.vives.ti.orkesthub.domain;

import be.vives.ti.orkesthub.domain.request.MemberRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Members")
public class Member {

    @Id
    private String id;

    private Gender gender;

    private Name name;

    private Address address;

    private String email;

    private String phone;

    private Date birthdate;

    private Date memberSince;

    private String[] instruments;

    private String picture;

    private boolean isManagement;


    public Member(Gender gender, Name name, Address address, String email, String phone, Date birthdate, Date memberSince, String[] instruments, String picture, boolean isManagement) {
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

    public String getId() {
        return id;
    }

    public Gender getGender() {
        return gender;
    }

    public Name getName() {
        return name;
    }

    public Address getAddress() {
        return address;
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

    public boolean isManagement() {
        return isManagement;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public void setMemberSince(Date memberSince) {
        this.memberSince = memberSince;
    }

    public void setInstruments(String[] instruments) {
        this.instruments = instruments;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setManagement(boolean management) {
        this.isManagement = management;
    }
}
