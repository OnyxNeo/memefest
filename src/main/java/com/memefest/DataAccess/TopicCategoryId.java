package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicCategoryId {
    
  @Column(name = "Cat_Id", nullable = false, updatable = false, insertable = false)
  private Long catId;
  
  @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
  private Long topicId;
  
  public Long getCat_Id() {
    return this.catId;
  }
  
  public void setCat_Id(Long catId) {
    this.catId = catId;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
}
