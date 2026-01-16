package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@NamedQueries({
  @NamedQuery(
    name = "TopicVideo.getByTopicId", 
    query = "SELECT tc FROM TopicVideoEntity tc WHERE tc.topicVidId.topicId = :topicId"),
  @NamedQuery(
    name = "TopicVideo.getByVideoId",
    query = "SELECT tc FROM TopicVideoEntity tc WHERE tc.topicVidId.vidId = :videoId")
})
@Entity(name = "TopicVideoEntity")
@Table(name = "TOPIC_VIDEO")
public class TopicVideo{    
  
  @EmbeddedId
  private TopicVideoId topicVidId = new TopicVideoId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Topic_Id", referencedColumnName = "Topic_Id")
  private Topic topic;

  @OneToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name= "Vid_Id", referencedColumnName ="Vid_Id")
  private Video video;
  
  public Long getTopic_Id() {
    return this.topicVidId.getTopic_Id();
  }

  public void setTopic_Id(Long topicId){
    this.topicVidId.setTopic_Id(topicId);
  }
  
  public void setVid_Id(Long vidId) {
    this.topicVidId.setVid_Id(vidId);
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
    this.topicVidId.setTopic_Id(topic.getTopic_Id());
  }
  
  public Topic getTopic() {
    return this.topic;
  }
  
  public Video getVideo(){
    return this.video;
  }
  public void setVideo(Video video){
    this.video = video;
    this.topicVidId.setVid_Id(video.getVid_Id());
  }
}


