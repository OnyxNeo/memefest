package com.memefest.Jaxrs;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;

public class UserInput {
  @FormParam("firstName")
  String firstname;
  
  @FormParam("lastName")
  String lastname;
  
  @FormParam("contacts")
  int contacts;
  
  @FormParam("email")
  String email;
  
  @FormParam("userName")
  String username;
  
  @FormParam("password")
  String password;
  
  @HeaderParam("Content-Type")
  String contentType;
  
  public String getFirstname() {
    return this.firstname;
  }
  
  public String getLastname() {
    return this.lastname;
  }
  
  public int getContacts() {
    return this.contacts;
  }
  
  public String getEmail() {
    return this.email;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public String getContentType() {
    return this.contentType;
  }
}