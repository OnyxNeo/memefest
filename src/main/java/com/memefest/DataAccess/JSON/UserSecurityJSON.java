package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("UserSecurity")
public class UserSecurityJSON {
  @JsonBackReference("userSecurity")
  @JsonProperty("user")
  private UserJSON user;
  
  @JsonProperty("accessTkn")
  private String accessTkn;
  
  @JsonProperty("password")
  private String password;
  
  @JsonProperty("refreshTkn")
  private String refreshTkn;
  
  @JsonProperty("oldPassword")
  private String oldPassword;

  @JsonProperty("cancel")
  private boolean canceled;
  
  public UserSecurityJSON() {}
  
  @JsonCreator
  public UserSecurityJSON(@JsonProperty("accessTkn") String accessTkn,
                            @JsonProperty("password") String password, 
                              @JsonProperty("refreshTkn") String refreshToken,
                                @JsonProperty("userId") Long userId,
                                  @JsonProperty("userName") String username) {
    this.accessTkn = accessTkn;
    this.password = password;
    this.refreshTkn = refreshToken;
    this.user = new UserJSON(userId, null, username, 0, false,
                              null, null, null, null,
                               null, null);
  }
  
  @JsonProperty("user")
  public UserJSON getUser() {
    return this.user;
  }
  
  @JsonProperty("user")
  public void setUser(UserJSON user) {
    this.user = user;
  }
  
  @JsonProperty("accessTkn")
  public String getAccessTkn() {
    return this.accessTkn;
  }
  
  @JsonProperty("accessTkn")
  public void setAccessTkn(String accessTkn) {
    this.accessTkn = accessTkn;
  }
  
  @JsonProperty("cancel")
  public boolean isCancelled() {
    return this.canceled;
  }
  
  @JsonProperty("cancel")
  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }
  
  @JsonProperty("password")
  public String getPassword() {
    return this.password;
  }
  
  @JsonProperty("password")
  public void setPassword(String password) {
    this.password = password;
  }
  
  @JsonProperty("refreshTkn")
  public String getRefreshTkn() {
    return this.refreshTkn;
  }
  
  @JsonProperty("refreshTkn")
  public void setRefreshTkn(String refreshTkn) {
    this.refreshTkn = refreshTkn;
  }

  @JsonProperty("oldPassword")
  public String getOldPassword(){
    return this.oldPassword;
  }

  @JsonProperty("oldPassword")
  public void setOldPassword(String oldPassword){
    this.oldPassword = oldPassword;
  }
}
