package com.memefest.DataAccess;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
  @NamedQuery(
    name = "TopicFollowNotification.findByTopicId",
    query = "SELECT u FROM TopicFollowNotificationEntity u WHERE u.id.topicId = :topicId AND u.seen = :seen"),
  @NamedQuery(
    name = "TopicFollowNotification.findByUserId",
    query = "SELECT u FROM TopicFollowNotificationEntity u WHERE u.id.userId = :userId AND u.seen = :seen")
})
@Entity(name = "TopicFollowNotificationEntity")
@Table(name = "TOPIC_FOLLOW_NOTIFICATION")
public class TopicFollowNotification {
  
  @EmbeddedId
  private TopicFollowNotificationId id = new TopicFollowNotificationId();

  @Column(name = "Created", nullable = false)
  private Date created;

  @Column(name = "Seen")
  private boolean seen;
  
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
    this.setTopic_Id(topic.getTopic_Id());
  }

  public void setUser(User user){
    this.user = user;
    this.setUserId(user.getUserId());
  }

  public void setTopic_Id(int followerId) {
    this.id.setTopic_Id(followerId);
  }
  
  public void setUserId(int userId) {
    this.id.setUserId(userId);
  }
  
  public int getTopic_Id() {
    return this.id.getTopic_Id();
  }
  
  public int getUserId() {
    return this.id.getUserId();
  }
  
  public void setCreated(Date created){
    this.created = created;
  }

  public Date getCreated(){
    return this.created;
  }

  public boolean getSeen(){
    return this.seen;
  }

  public void setSeen(boolean seen){
    this.seen = seen;
  }
}