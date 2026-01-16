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
        name = "EventVideo.findByEventId",
        query = "SELECT e FROM EventVideoEntity e WHERE e.eventVidId.eventId = :eventId"),
    @NamedQuery(
        name = "EventVideo.findByVidId", 
        query = "SELECT e FROM EventVideoEntity e WHERE e.eventVidId.vidId = :vidId")
})
@Entity(name = "EventVideoEntity")
@Table(name = "EVENT_VIDEOS")
public class EventVideo{
  @EmbeddedId
  private EventVideoId eventVidId = new EventVideoId();

  @OneToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Vid_Id", referencedColumnName = "Vid_Id")
  private Video video;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Event_Id", referencedColumnName ="Event_Id")
  private Event event;
  
  public Long getVid_Id() {
    return this.eventVidId.getVid_Id();
  }

  public void setVid_Id(Long postId){
    this.eventVidId.setVid_Id(postId);
  }
  
  public void setEvent_Id(Long imgId) {
    this.eventVidId.setEvent_Id(imgId);
  }

  public void setVideo(Video video) {
    this.video = video;
    this.setVid_Id(video.getVid_Id());
  }
  
  public Video getVideo() {
    return this.video;
  }

  public Event getEvent(){
    return this.event;
  }

  public void setEvent(Event event){
    this.event = event;
    this.eventVidId.setVid_Id(event.getEvent_Id());
  }
}
    