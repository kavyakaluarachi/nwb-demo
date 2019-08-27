package com.parse.starter;

import com.parse.ParseFile;

public class Incident {

    private String type;
    private String when;
    private String status;
    private String address;
    private String message;
    private String user;
    private String reference;
    private ParseFile image;



    public Incident(String type, String date, String address, String message, ParseFile image, String username, String reference, String status) {
        this.type = type;
        this.when = date;
        this.address = address;
        this.message = message;
        this.image = image;
        this.user = username;
        this.reference = reference;
        this.status = status;


    }


    public String getType() {
        return type;
    }

    public String getWhen() {
        return when;
    }

    public String getLocation() {
        return address;
    }

    public String getMessage() { return message; }

    public ParseFile getImage() { return image; }

    public String getUser() { return user; }

    public String getReference() { return reference;}

    public String getStatus() { return status; }



    }

