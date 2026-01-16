package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicVideoId {

  @Column(name = "Vid_Id", nullable = false, updatable = false, insertable = false)
  private Long vidId;
  
  @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
  private Long topicId;
  
  public Long getVid_Id() {
    return this.vidId;
  }
  
  public void setVid_Id(Long vidId) {
    this.vidId = vidId;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
}
