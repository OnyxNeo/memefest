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
        name = "EventCategory.findByEventId",
        query = "SELECT p FROM EventCategoryEntity p WHERE p.eventCatId.eventId = :eventId"),
    @NamedQuery(
        name = "EventCategory.findByCatId", 
        query = "SELECT p FROM EventCategoryEntity p WHERE p.eventCatId.catId = :catId")
})
@Entity(name = "EventCategoryEntity")
@Table(name = "EVENT_CATEGORY")
public class EventCategory {
  
  @EmbeddedId
  private EventCategoryId eventCatId = new EventCategoryId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Event_Id", referencedColumnName = "Event_Id")
  private Event event;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Cat_Id", referencedColumnName ="Cat_Id")
  private Category category;
  
  public Long getEvent_Id() {
    return this.eventCatId.getEvent_Id();
  }

  public void setEvent_Id(Long eventId){
    this.eventCatId.setEvent_Id(eventId);
  }
  
  public void setCat_Id(Long catId) {
    this.eventCatId.setCat_Id(catId);
  }

  public Long getCat_Id(){
    return this.eventCatId.getCat_Id();
  }

  public void setPost(Event event) {
    this.event = event;
    this.setEvent_Id(event.getEvent_Id());
  }
  
  public Event getEvent() {
    return this.event;
  }

  public void setCategory(Category category){
    this.category = category;
    this.eventCatId.setCat_Id(category.getCat_Id());
  }

  public Category getCategory(){
    return this.category;
  }
}