package com.example.highfive.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactObject implements Serializable {
    String userID, firstName, lastName, email, phone, website, company, picture, notes, address, jobTitle;
    ArrayList<String> listOfUserIds;


    public ContactObject(String userID, String firstName, String lastName, String email, String phone,
                         String website, String company, String address, String picture, String notes, String jobTitle) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.company = company;
        this.picture = picture;
        this.notes = notes;
        this.address = address;
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<String> getListOfUserIds() {
        return listOfUserIds;
    }

    public void setListOfUserIds(ArrayList<String> listOfUserIds) {
        this.listOfUserIds = listOfUserIds;
    }
}
