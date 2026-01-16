package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@NamedQueries({
    @NamedQuery(
        name = "Admin.isAdminByUsername", 
        query = "SELECT COUNT(u.userId) FROM AdminEntity u INNER JOIN UserEntity e WHERE e.username = :username"),
    @NamedQuery(
        name = "Admin.isAdminByUserId",
        query = "SELECT count(u.userId) FROM AdminEntity u WHERE u.userId = :userId")
})
@Entity(name = "AdminEntity")
@Table(name = "ADMIN_USER")
public class UserAdmin{

  @Id
  @Column(name = "UserId")
  private Long userId;

  @OneToOne(cascade = {CascadeType.ALL})
  @PrimaryKeyJoinColumn(name = "UserId")
  private User user;


  public Long getUserId(){
    return this.userId;
  }

  public User getUser(){
    return this.user;
  }
    
}
