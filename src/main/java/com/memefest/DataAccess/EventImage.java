package com.memefest.DataAccess;


import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
    {@NamedQuery(
        name = "EventImage.findByPosterId", 
        query = "SELECT u FROM EventImageEntity u WHERE u.eventImgId.eventId = :eventId"), 
    @NamedQuery(
        name = "EventImage.findByEventId", 
        query = "SELECT e FROM EventImageEntity e WHERE e.eventImgId.eventId = :eventId")
})
@Entity(name = "EventImageEntity")
@Table(name = "EVENT_IMAGES")
public class EventImage{
  
  @EmbeddedId
  private EventImageId eventImgId = new EventImageId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Img_Id", referencedColumnName = "Img_Id")
  private Image image;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Event_Id", referencedColumnName ="Event_Id")
  private Event event;
  
  public Long getImg_Id() {
    return this.eventImgId.getImg_Id();
  }

  public void setImg_Id(Long imgId){
    this.eventImgId.setImg_Id(imgId);
  }
  
  public void setEvent_Id(Long eventId) {
    this.eventImgId.setEvent_Id(eventId);
  }

  public void setImage(Image image) {
    this.image = image;
    this.setImg_Id(image.getImg_Id());
  }
  
  public Image getImage() {
    return this.image;
  }

  public Event getEvent(){
    return this.event;
  }

  public void setEvent(Event event){
    this.event = event;
    this.eventImgId.setEvent_Id(event.getEvent_Id());
  }
}