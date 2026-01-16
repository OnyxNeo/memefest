package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserFollowerId {
  @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
  private Long userId;
  
  @Column(name = "Follower_Id", nullable = false, updatable = false, insertable = false)
  private Long followerId;
  
  public Long getUserId() {
    return this.userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public Long getFollowerId() {
    return this.followerId;
  }
  
  public void setFollowerId(Long followerId) {
    this.followerId = followerId;
  }
}
