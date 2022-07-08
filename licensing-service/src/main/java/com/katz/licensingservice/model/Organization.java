package com.katz.licensingservice.model;

public class Organization {
    private String name;
    private String contactName;
    private String contactEmail;
    private String contactPhone;

    public String getName() {
        return name;
    }

    public void setName(String organizationName) {
        this.name = organizationName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String organizationEmail) {
        this.contactEmail = organizationEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String organizationPhone) {
        this.contactPhone = organizationPhone;
    }
}
