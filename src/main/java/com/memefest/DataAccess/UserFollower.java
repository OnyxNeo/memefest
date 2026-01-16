package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
  @NamedQuery(
    name = "UserFollower.findByFollowerId",
    query = "SELECT u FROM UserFollowerEntity u WHERE u.id.followerId = :followerId"), 
  @NamedQuery(
    name = "UserFollower.findByUserId", 
    query = "SELECT u FROM UserFollowerEntity u WHERE u.id.userId = :userId")
})
@Entity(name = "UserFollowerEntity")
@Table(name = "USER_FOLLOWS")
public class UserFollower {
  @EmbeddedId
  private UserFollowerId id = new UserFollowerId();
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "UserId", referencedColumnName = "UserId")
  private User user;
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Follower_Id", referencedColumnName= "UserId")
  private User follower;
  
  public User getUser() {
    return this.user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public void setFollower(User follower) {
    this.follower = follower;
  }
  
  public User getFollower() {
    return this.follower;
  }
  
  public void setFollower_Id(Long followerId) {
    this.id.setFollowerId(followerId);
  }
  
  public void setUserId(Long userId) {
    this.id.setUserId(userId);
  }
  
  public Long getFollower_Id() {
    return this.id.getFollowerId();
  }
  
  public Long getUserId() {
    return this.id.getUserId();
  }
}