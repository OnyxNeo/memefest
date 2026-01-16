package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicPostId {


  @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
  private Long postId;
  
  @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
  private Long topicId;
  
  public Long getPost_Id() {
    return this.postId;
  }
  
  public void setPost_Id(Long postId) {
    this.postId = postId;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
}
