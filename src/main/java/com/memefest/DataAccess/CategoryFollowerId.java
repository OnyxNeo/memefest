package com.memefest.DataAccess;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CategoryFollowerId {
  @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
  private Long userId;
  
  @Column(name = "Cat_Id", nullable = false, updatable = false, insertable = false)
  private Long categoryId;
  
  public Long getUserId() {
    return this.userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public Long getCat_Id() {
    return this.categoryId;
  }
  
  public void setCat_Id(Long categoryId) {
    this.categoryId = categoryId;
  }
}
