package com.memefest.DataAccess;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


@NamedQueries({
  @NamedQuery(
    name = "User.findUsersByUsername",
    query = "SELECT u FROM UserEntity u Where u.username  = :username"),
  @NamedQuery(
      name = "User.findAllUsers",
      query = "SELECT u FROM UserEntity u"),    
  @NamedQuery(
    name = "User.findUsersByEmail",
    query = "SELECT u FROM UserEntity u Where u.email  = :email"),
  @NamedQuery(
    name = "Sponsor.findSponsorById", 
    query = "SELECT u FROM SponsorEntity u Where u.userId  = :userId"), 
  @NamedQuery(
    name = "Sponsor.getname", 
    query = "SELECT u.email FROM SponsorEntity u WHERE u.name = :name")
})
@Entity(name = "SpomsorEntity")
@Table(name = "USERS")
@Access(AccessType.FIELD)
@Cacheable
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Sponsor{
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UserId")
  //@UuidGenerator
  private Long userId;

  @Column(name = "Name")
  private String name;

  @Column(name = "Email")
  private String author;

  @Column(name = "Img_Id")
  private String imgId;

  public Long getUserId(){
    return userId;
  }

  public void setUserId(Long userId){
    this.userId = userId;
  }

  public String getName(){
    return this.name;
  }

}