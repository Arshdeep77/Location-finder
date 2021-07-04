package com.example.trackme.model;

public class User {
String id;
    String Phn;

    public User(String id, String phn) {
        this.id = id;
        Phn = phn;
    }
    public User(){
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhn() {
        return Phn;
    }

    public void setPhn(String phn) {
        Phn = phn;
    }
}
