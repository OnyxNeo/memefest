package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EventPostId {
    @Column(name = "Event_Id", nullable = false, updatable = false, insertable = false)
    private Long eventId;
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;
    
    public Long getEvent_Id() {
        return this.eventId;
    }
    
    public void setEvent_Id(Long eventId) {
        this.eventId = eventId;
    }
    
    public Long getPost_Id() {
        return this.postId;
    }
    
    public void setPost_Id(Long postId) {
        this.postId = postId;
    }    
}
