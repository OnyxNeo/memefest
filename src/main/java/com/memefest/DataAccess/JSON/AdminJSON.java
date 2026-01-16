package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("admin")
public class AdminJSON extends UserJSON {
  @JsonCreator
  public AdminJSON(@JsonProperty("UserId") Long userId, @JsonProperty("Email") String email, @JsonProperty("Username") String username, @JsonProperty("Contacts") int contacts, @JsonProperty("Verified") boolean verified, @JsonProperty("FirstName") String firstName, @JsonProperty("LastName") String lastName, @JsonProperty("UserSecurity") UserSecurityJSON userSecurity) {
    super(userId, email, username, contacts, verified, firstName, lastName, userSecurity, null, null, null);
  }
}
