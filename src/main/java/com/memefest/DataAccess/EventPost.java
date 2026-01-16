package com.memefest.DataAccess;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
    {@NamedQuery(
        name = "EventPost.findByEventId", 
        query = "SELECT u FROM EventPostEntity u WHERE u.eventPostId.eventId = :eventId")
})
@Entity(name = "EventPostEntity")
@Table(name = "Event_POST")
public class EventPost{
  @EmbeddedId
  private EventPostId eventPostId = new EventPostId();

  @ManyToOne
  @JoinColumn(name = "Post_Id")
  private Post post;

  @ManyToOne
  @JoinColumn(name ="Event_Id")
  private Event event;
  
  public Long getPost_Id() {
    return this.eventPostId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.eventPostId.setPost_Id(postId);
  }
  
  public void setEvent_Id(Long imgId) {
    this.eventPostId.setEvent_Id(imgId);
  }

  public void setPost(Post post) {
    this.setPost_Id(post.getPost_Id());
    this.post = post;
  }
  
  public Post getPost() {
    return this.post;
  }

  public Event getEvent(){
    return this.event;
  }

  public void setEvent(Event event){
    this.setEvent_Id(event.getEvent_Id());
    this.event = event;
  }
}
    