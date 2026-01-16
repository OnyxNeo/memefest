package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SubCategoryId {    
  @Column(name = "Cat_Id", nullable = false, updatable = false, insertable = false)
  private Long catId;
  
  @Column(name = "Parent_Id", nullable = false, updatable = false, insertable = false)
  private Long parentId;
  
  public Long getCat_Id() {
    return this.catId;
  }
  
  public void setCat_Id(Long catId) {
    this.catId = catId;
  }
  
  public Long getParent_Id() {
    return this.parentId;
  }
  
  public void setParent_Id(Long parentId) {
    this.parentId = parentId;
  }
}