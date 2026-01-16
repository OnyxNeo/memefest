package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicImageId {

  @Column(name = "Poster_Id", nullable = false, updatable = false, insertable = false)
  private Long imgId;
  
  @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
  private Long topicId;
  
  public Long getPoster_Id() {
    return this.imgId;
  }
  
  public void setPoster_Id(Long imgId) {
    this.imgId = imgId;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
}
