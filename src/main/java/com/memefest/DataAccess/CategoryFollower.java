package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
  {@NamedQuery(name = "CategoryFollower.findByCategoryId",
        query = "SELECT u FROM CategoryFollowerEntity u WHERE u.id.categoryId = :categoryId"), 
  
    @NamedQuery(name = "CategoryFollower.findByUserId",
        query = "SELECT u FROM CategoryFollowerEntity u WHERE u.id.userId = :userId")})
@Entity(name = "CategoryFollowerEntity")
@Table(name = "CATEGORY_FOLLOWS")
public class CategoryFollower {
  @EmbeddedId
  private CategoryFollowerId id = new CategoryFollowerId();
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "UserId", referencedColumnName = "UserId")
  private User user;
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Cat_Id", referencedColumnName = "Cat_Id")
  private Category category;
  
  public User getUser() {
    return this.user;
  }
  
  public void setCategory(Category category) {
    this.category = category;
  }

  public Category getCategory(){
    return this.category;
  }
  
  public void setFollower(User follower) {
    this.user = follower;
  }
  
  public User getFollower() {
    return this.user;
  }
  
  public void setCat_Id(Long followerId) {
    this.id.setCat_Id(followerId);
  }
  
  public void setUserId(Long userId) {
    this.id.setUserId(userId);
  }
  
  public Long getCat_Id() {
    return this.id.getCat_Id();
  }
  
  public Long getUserId() {
    return this.id.getUserId();
  }
}
