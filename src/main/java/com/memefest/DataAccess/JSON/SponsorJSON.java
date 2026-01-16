package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SponsorJSON {

    @JsonProperty("image")
    private ImageJSON image;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;
    
    @JsonCreator
    public SponsorJSON(@JsonProperty("image") ImageJSON image, @JsonProperty("name") String name){
        this.image = image;
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setImage(ImageJSON image){
        this.image = image;
    }

    public ImageJSON getImage(){
        return this.image;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
