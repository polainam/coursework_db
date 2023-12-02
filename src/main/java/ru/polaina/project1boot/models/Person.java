package ru.polaina.project1boot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
@Table(name = "clients")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personId;
    @NotEmpty(message = "User name should not be empty")
    @Column(name = "user_name")
    private String userName;

    /////////////////////////////////////
    @OneToMany(mappedBy = "person")
    private List<Journal> journalEntries;
    /////////////////////////////////////

    @Pattern(regexp = "[A-Z][a-z]+",
            message = "The name must begin with a capital letter")
    @NotEmpty(message = "First name should not be empty")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Last name should not be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Last name must begin with a capital letter")
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty(message = "Parther name should not be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Parther name must begin with a capital letter")
    @Column(name = "parther_name")
    private String partherName;

    @Pattern(regexp = "\\d{4}",
            message = "The passport series must contain 4 digits")
    @Column(name = "passport_seria")
    private String passportSeria;

    @Pattern(regexp = "\\d{6}",
            message = "Passport number must contain 6 digits")
    @Column(name = "passport_num")
    private String passportNum;

    @NotEmpty(message = "Password number should not be empty")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Journal> getJournalEntries() {
        return journalEntries;
    }

    public void setJournalEntries(List<Journal> journalEntries) {
        this.journalEntries = journalEntries;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPartherName() {
        return partherName;
    }

    public void setPartherName(String partherName) {
        this.partherName = partherName;
    }

    public String getPassportSeria() {
        return passportSeria;
    }

    public void setPassportSeria(String passportSeria) {
        this.passportSeria = passportSeria;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
