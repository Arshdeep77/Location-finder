package com.example.trackme.model;

public class ContactUser {
   public String name;
        String PhNo;
   String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhNo() {
        return PhNo;
    }

    public void setPhNo(String phNo) {
        PhNo = phNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContactUser(String name, String phNo, String id) {
        this.name = name;
        PhNo = phNo;
        this.id = id;
    }

    public ContactUser() {

    }
}
