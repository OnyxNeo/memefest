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
    name = "TopicFollower.findByTopicId",
    query = "SELECT u FROM TopicFollowerEntity u WHERE u.id.topicId = :topicId"),
  @NamedQuery(
    name = "TopicFollower.findByUserId",
    query = "SELECT u FROM TopicFollowerEntity u WHERE u.id.userId = :userId")
})
@Entity(name = "TopicFollowerEntity")
@Table(name = "TOPIC_FOLLOWS")
public class TopicFollower {
  @EmbeddedId
  private TopicFollowerId id = new TopicFollowerId();
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "UserId", referencedColumnName = "UserId")
  private User user;
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Topic_Id", referencedColumnName = "Topic_Id")
  private Topic topic;
  
  public User getUser() {
    return this.user;
  }
  
  public void setTopic(Topic topic) {
    this.topic = topic;
  }
  
  public void setFollower(User follower) {
    this.user = follower;
  }
  
  public User getFollower() {
    return this.user;
  }
  
  public void setTopic_Id(Long followerId) {
    this.id.setTopic_Id(followerId);
  }
  
  public void setUserId(Long userId) {
    this.id.setUserId(userId);
  }
  
  public Long getTopic_Id() {
    return this.id.getTopic_Id();
  }
  
  public Long getUserId() {
    return this.id.getUserId();
  }
}