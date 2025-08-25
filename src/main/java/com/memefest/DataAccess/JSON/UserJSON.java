package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Set;

@JsonRootName("User")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "UserId")
@JsonFilter("UserPublicView")
public class UserJSON {
  @JsonProperty("UserId")
  private int userId;
  
  @JsonProperty("Email")
  private String email;
  
  @JsonProperty("Username")
  private String username;
  
  @JsonProperty("Contacts")
  private int contacts;
  
  @JsonProperty("Verified")
  private boolean verified;
  
  @JsonProperty("FirstName")
  private String firstName;
  
  @JsonProperty("LastName")
  private String lastName;
  
  @JsonProperty("UserSecurity")
  private UserSecurityJSON userSecurity;
  
  @JsonProperty("Posts")
  private Set<PostJSON> posts;
  
  @JsonProperty("Cancel")
  private boolean canceled;
  
  @JsonProperty("TopicsFollowing")
  private Set<TopicJSON> topicsFollowing;
  
  @JsonProperty("CategoriesFollowing")
  private Set<CategoryJSON> categoriesFollowing;

  @JsonProperty("Reposts")
  private Set<RepostJSON> reposts;
  
  public UserJSON() {}
  
  public UserJSON(@JsonProperty("Username") String username, @JsonProperty("Contacts") int contacts, @JsonProperty("UserId") int userId, @JsonProperty("Verified") boolean verified) {
    this.username = username;
    this.userId = userId;
    this.contacts = contacts;
    this.verified = verified;
    this.email = null;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.posts = null;
    this.canceled = false;
  }
  
  @JsonCreator
  public UserJSON(@JsonProperty("UserId") int userId,
                          @JsonProperty("Email") String email,
                           @JsonProperty("Username") String username,
                           @JsonProperty("Contacts") int contacts,
                            @JsonProperty("Verified") boolean verified, 
                            @JsonProperty("FirstName") String firstName, 
                            @JsonProperty("LastName") String lastName, 
                            @JsonProperty("UserSecurity") UserSecurityJSON userSecurity, 
                            @JsonProperty("Posts") Set<PostJSON> posts, 
                            @JsonProperty("CategoriesFollowing") Set<CategoryJSON> categoriesFollowing, 
                            @JsonProperty("TopicsFollowing") Set<TopicJSON> topicsFollowing) {
    this.email = email;
    this.email = email;
    this.username = username;
    this.contacts = contacts;
    this.verified = verified;
    this.firstName = firstName;
    this.lastName = lastName;
    if (userSecurity != null)
      this.userSecurity = new UserSecurityJSON(userSecurity.getAccessTkn(), userSecurity.getPassword(), userSecurity.getRefreshTkn(), new com.memefest.DataAccess.JSON.UserJSON(this.userId, this.username)); 
    this.userId = userId;
    this.posts = posts;
    this.categoriesFollowing = categoriesFollowing;
    this.topicsFollowing = topicsFollowing;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Email") String email, @JsonProperty("Username") String username, @JsonProperty("Contacts") int contacts, @JsonProperty("Verified") boolean verified, @JsonProperty("FirstName") String firstName, @JsonProperty("LastName") String lastName, UserSecurityJSON userSecurity) {
    this.email = email;
    this.username = username;
    this.contacts = contacts;
    this.verified = verified;
    this.firstName = firstName;
    this.lastName = lastName;
    if (userSecurity != null)
      this.userSecurity = new UserSecurityJSON(userSecurity.getAccessTkn(), userSecurity.getPassword(), userSecurity.getRefreshTkn()); 
    this.userId = 0;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Email") String email, @JsonProperty("Username") String username, @JsonProperty("FirstName") String firstName, @JsonProperty("LastName") String lastName) {
    this.email = email;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userSecurity = null;
    this.userId = 0;
    this.verified = false;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Username") String username) {
    this.username = username;
    this.userId = 0;
    this.email = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.contacts = 0;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Username") String username, @JsonProperty("Email") String email) {
    this.email = email;
    this.username = username;
    this.userId = 0;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.contacts = 0;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("UserId") int userId, @JsonProperty("Username") String username) {
    this.username = username;
    this.userId = userId;
    this.email = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.contacts = 0;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Username") String username, @JsonProperty("UserSecurity") UserSecurityJSON userSecurityDetails) {
    this.username = username;
    this.userId = 0;
    this.email = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    if (userSecurityDetails != null)
      this.userSecurity = new UserSecurityJSON(this.userSecurity.getAccessTkn(), this.userSecurity.getPassword(), this.userSecurity.getRefreshTkn()); 
    this.contacts = 0;
    this.canceled = false;
  }
  
  public UserJSON(@JsonProperty("Username") String username, @JsonProperty("Email") String userEmail, @JsonProperty("Contacts") int contacts, @JsonProperty("FirstName") String firstName, @JsonProperty("LastName") String lastname) {
    this.username = username;
    this.userId = 0;
    this.email = userEmail;
    this.verified = false;
    this.firstName = firstName;
    this.lastName = lastname;
    this.userSecurity = null;
    this.contacts = contacts;
    this.canceled = false;
  }
  
  @Override
  public int hashCode(){
    int result = 40;
    if(username != null);
      result = result * this.username.hashCode();
    if(this.email != null)
      result = result * this.email.hashCode();
    if(this.contacts != 0)
      result = result * this.contacts;
    if(this.userId != 0)
      result = result * this.userId;
    if(this.firstName != null && this.lastName != null)
      result = result + (this.firstName.hashCode() + this.lastName.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object o){
    if(o instanceof UserJSON && this.hashCode() == o.hashCode())
      return true;
    else
      return false;
  }


  @JsonProperty("Username")
  public String getUsername() {
    return this.username;
  }
  
  @JsonProperty("Username")
  public void setUsername(String username) {
    this.username = username;
  }
  
  @JsonProperty("Email")
  public String getEmail() {
    return this.email;
  }
  
  @JsonProperty("Email")
  public void setEmail(String email) {
    this.email = email;
  }
  
  @JsonProperty("Contacts")
  public int getContacts() {
    return this.contacts;
  }
  
  @JsonProperty("Contacts")
  public void setContacts(int contacts) {
    this.contacts = contacts;
  }
  
  @JsonProperty("Verified")
  public boolean isVerified() {
    return this.verified;
  }
  
  @JsonProperty("Verified")
  public void setVerified(boolean verified) {
    this.verified = verified;
  }
  
  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  @JsonProperty("LastName")
  public String getLastName() {
    return this.lastName;
  }
  
  @JsonProperty("LastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public UserSecurityJSON getUserSecurity() {
    return this.userSecurity;
  }
  
  public void setUserSecurity(UserSecurityJSON userSecurity) {
    this.userSecurity = new UserSecurityJSON(userSecurity.getAccessTkn(), userSecurity.getPassword(), userSecurity.getRefreshTkn(), new com.memefest.DataAccess.JSON.UserJSON(this.userId, this.username));
  }
  
  @JsonProperty("UserId")
  public int getUserId() {
    return this.userId;
  }
  
  @JsonProperty("UserId")
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  @JsonProperty("Cancel")
  public boolean isCancelled() {
    return this.canceled;
  }
  
  @JsonProperty("Cancel")
  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  @JsonProperty("Reposts")
  public void setReposts(Set<RepostJSON> reposts){
    this.reposts = reposts;
  }

  @JsonProperty("Reposts")
  public Set<RepostJSON> getReposts(){
    return this.reposts;
  }
}
