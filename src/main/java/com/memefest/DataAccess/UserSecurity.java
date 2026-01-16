package com.memefest.DataAccess;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@NamedQueries({
  @NamedQuery(
    name = "UserSecurity.findByUsername", 
    query = "SELECT s.userId, s.username, s.email, se.accessTkn, se.refreshTkn FROM SecurityEntity se INNER JOIN se.user s"
      + " WHERE s.username = :username"),
  @NamedQuery(
    name = "UserSecurity.findByEmail",
    query = "SELECT s.userId, s.username, s.email, se.accessTkn, se.refreshTkn FROM SecurityEntity se INNER JOIN se.user s"
      + " WHERE s.email = :email"),
  @NamedQuery(
    name = "UserSecurity.findByUserId", 
    query = "SELECT s.userId, s.username, s.email, se.accessTkn, se.refreshTkn FROM SecurityEntity se INNER JOIN se.user s"
      + " WHERE s.userId = :userId"), 
  @NamedQuery(
    name = "UserSecurity.updateJsonTkns",
    query = "UPDATE SecurityEntity se SET se.accessTkn = :accessTkn, se.refreshTkn = :refreshTkn" 
      + "  WHERE  se.user.username = :username"), 
  @NamedQuery(
    name = "UserSecurity.updatePasswordFromUsername", 
    query = "UPDATE SecurityEntity se SET se.password = :password WHERE"
      + " se.user.username = :username"), 
  @NamedQuery(
    name = "UserSecurity.updatePasswordFromUserId",
    query = "UPDATE SecurityEntity se SET se.password = :password WHERE EXISTS (SELECT se FROM SecurityEntity se"
      + " INNER JOIN se.user s WHERE s.userId = :userId)"), 
  @NamedQuery(
    name = "UserSecurity.getUserPasswordFromUserId", 
    query = "SELECT se.password , s.userId, s.username FROM SecurityEntity se INNER JOIN se.user s"
      + " WHERE s.userId = :userId"),
  @NamedQuery(
    name = "UserSecurity.getUserPasswordFromUsername",
    query = "SELECT se.password, s.userId, s.username FROM SecurityEntity se INNER JOIN se.user s"
    + " WHERE s.username = :username")
})
@Entity(name = "SecurityEntity")
@Table(name = "USER_SECURITY")
@Access(AccessType.FIELD)
public class UserSecurity {
  
  @Id
  //@GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UserId", nullable = false, insertable = true, updatable = false)
  private Long userId;
  
  @Column(name = "Refresh_Token")
  private String refreshTkn;
  
  @Column(name = "Password_field")
  private String password;
  
  @Column(name = "Access_Token")
  private String accessTkn;
  
  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST})
  @PrimaryKeyJoinColumn(name = "UserId",referencedColumnName = "UserId")
  private User user;
  
  public void setAccessTkn(String accessTkn) {
    this.accessTkn = accessTkn;
  }
  
  public String getAccessTkn() {
    return this.accessTkn;
  }
  
  public String getRefreshTkn() {
    return this.refreshTkn;
  }
  
  public void setRefreshTkn(String refreshTkn) {
    this.refreshTkn = refreshTkn;
  }
  
  public User getUser() {
    return this.user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public Long getUserId() {
    return this.userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }
}