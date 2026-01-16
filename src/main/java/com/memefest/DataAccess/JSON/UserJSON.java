package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Set;

@JsonRootName("User")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "userId")
@JsonFilter("UserView")
public class UserJSON {
  @JsonProperty("userId")
  private Long userId;
  
  @JsonProperty("email")
  private String email;
  
  @JsonProperty("userName")
  private String username;

  @JsonProperty("avatar")
  private String avatar;
  
  @JsonProperty("contacts")
  private int contacts;
  
  @JsonProperty("verified")
  private boolean verified;
  
  @JsonProperty("firstName")
  private String firstName;
  
  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("displayName")
  private String displayName;
  
  @JsonProperty("userSecurity")
  private UserSecurityJSON userSecurity;
  
  @JsonProperty("posts")
  private Set<PostJSON> posts;
  
  @JsonProperty("cancel")
  private boolean canceled;
  
  @JsonProperty("topicsFollowing")
  private Set<TopicJSON> topicsFollowing;
  
  @JsonProperty("categoriesFollowing")
  private Set<CategoryJSON> categoriesFollowing;

  @JsonProperty("reposts")
  private Set<RepostJSON> reposts;

  @JsonProperty("isFollowed")
  private boolean followed;
  
  public UserJSON() {}
  
  public UserJSON(@JsonProperty("userName") String username, 
                    @JsonProperty("contacts") int contacts,
                     @JsonProperty("userId") Long userId, 
                      @JsonProperty("verified") boolean verified) {
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
    setDisplayName();
    this.followed = false;
  }
  
  @JsonCreator
  public UserJSON(@JsonProperty("userId") Long userId,
                          @JsonProperty("email") String email,
                           @JsonProperty("userName") String username,
                           @JsonProperty("contacts") int contacts,
                            @JsonProperty("verified") boolean verified, 
                            @JsonProperty("firstName") String firstName, 
                            @JsonProperty("lastName") String lastName, 
                            @JsonProperty("userSecurity") UserSecurityJSON userSecurity, 
                            @JsonProperty("posts") Set<PostJSON> posts, 
                            @JsonProperty("categoriesFollowing") Set<CategoryJSON> categoriesFollowing, 
                            @JsonProperty("topicsFollowing") Set<TopicJSON> topicsFollowing) {
    this.email = email;
    this.username = username;
    this.contacts = contacts;
    this.verified = verified;
    this.firstName = firstName;
    this.lastName = lastName;
    if (userSecurity != null)
      this.userSecurity = userSecurity;
    this.userId = userId;
    this.posts = posts;
    this.categoriesFollowing = categoriesFollowing;
    this.topicsFollowing = topicsFollowing;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  public UserJSON(@JsonProperty("email") String email,
                     @JsonProperty("userName") String username, 
                        @JsonProperty("firstName") String firstName,
                          @JsonProperty("lastName") String lastName) {
    this.email = email;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userSecurity = null;
    this.userId = null;
    this.verified = false;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  public UserJSON(@JsonProperty("userName") String username) {
    this.username = username;
    this.userId = null;
    this.email = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.contacts = 0;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  public UserJSON(@JsonProperty("userName") String username, 
                    @JsonProperty("email") String email) {
    this.email = email;
    this.username = username;
    this.userId = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    this.userSecurity = null;
    this.contacts = 0;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  
  public UserJSON(@JsonProperty("userName") String username, 
                    @JsonProperty("userSecurity") UserSecurityJSON userSecurityDetails) {
    this.username = username;
    this.userId = null;
    this.email = null;
    this.verified = false;
    this.firstName = null;
    this.lastName = null;
    if (userSecurityDetails != null)
      this.userSecurity = userSecurityDetails; 
    this.contacts = 0;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  public UserJSON(@JsonProperty("userName") String username, 
                    @JsonProperty("email") String userEmail, 
                      @JsonProperty("contacts") int contacts, 
                        @JsonProperty("firstName") String firstName,
                          @JsonProperty("lastName") String lastname) {
    this.username = username;
    this.userId = null;
    this.email = userEmail;
    this.verified = false;
    this.firstName = firstName;
    this.lastName = lastname;
    this.userSecurity = null;
    this.contacts = contacts;
    this.canceled = false;
    setDisplayName();
    this.followed = false;
  }
  
  @Override
  public boolean equals(Object o){
    if(o instanceof UserJSON && this.hashCode() == o.hashCode())
      return true;
    else
      return false;
  }

  private void setDisplayName(){
    if(firstName!= null && lastName != null){
      displayName = firstName.concat( " " + lastName);
    }
  }

  public boolean getFollowed(){
    return this.followed;
  }

  public void setFollowed(boolean followed){
    this.followed = followed;
  }

  @JsonProperty("userName")
  public String getUsername() {
    return this.username;
  }
  
  @JsonProperty("userName")
  public void setUsername(String username) {
    this.username = username;
  }
  
  @JsonProperty("email")
  public String getEmail() {
    return this.email;
  }
  
  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }
  
  @JsonProperty("contacts")
  public int getContacts() {
    return this.contacts;
  }
  
  @JsonProperty("contacts")
  public void setContacts(int contacts) {
    this.contacts = contacts;
  }
  
  @JsonProperty("verified")
  public boolean isVerified() {
    return this.verified;
  }
  
  @JsonProperty("verified")
  public void setVerified(boolean verified) {
    this.verified = verified;
  }
  


  @JsonProperty("avatar")
  public void setAvatar(String avatar){
    this.avatar = avatar;
  }

  @JsonProperty("avatar")
  public String getAvatar(){
    return avatar;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
    setDisplayName();
  }
  
  @JsonProperty("lastName")
  public String getLastName() {
    return this.lastName;
  }
  
  @JsonProperty("lastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
    setDisplayName();
  }
  
  public UserSecurityJSON getUserSecurity() {
    return this.userSecurity;
  }
  
  public void setUserSecurity(UserSecurityJSON userSecurity) {
    this.userSecurity = userSecurity;
  }
  
  @JsonProperty("userId")
  public Long getUserId() {
    return this.userId;
  }
  
  @JsonProperty("userId")
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  @JsonProperty("cancel")
  public boolean isCancelled() {
    return this.canceled;
  }
  
  @JsonProperty("cancel")
  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  @JsonProperty("reposts")
  public void setReposts(Set<RepostJSON> reposts){
    this.reposts = reposts;
  }

  @JsonProperty("reposts")
  public Set<RepostJSON> getReposts(){
    return this.reposts;
  }
}
