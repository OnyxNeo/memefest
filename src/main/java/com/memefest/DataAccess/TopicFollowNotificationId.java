package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicFollowNotificationId {
  @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
  private Long userId;
  
  @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
  private Long topicId;
  
  public Long getUserId() {
    return this.userId;
  }
  
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
}
