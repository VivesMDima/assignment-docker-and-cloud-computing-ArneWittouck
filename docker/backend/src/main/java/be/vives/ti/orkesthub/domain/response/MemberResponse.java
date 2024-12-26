package be.vives.ti.orkesthub.domain.response;

import be.vives.ti.orkesthub.domain.Address;
import be.vives.ti.orkesthub.domain.Gender;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.Name;

import java.util.Date;

public class MemberResponse {

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

    public MemberResponse() {}

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.gender = member.getGender();
        this.name = member.getName();
        this.address = member.getAddress();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.birthdate = member.getBirthdate();
        this.memberSince = member.getMemberSince();
        this.instruments = member.getInstruments();
        this.picture = member.getPicture();
        this.isManagement = member.isManagement();
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
}
