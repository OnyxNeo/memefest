package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EventVideoId {
    
    @Column(name = "Vid_Id", nullable = false, updatable = false, insertable = false)
    private Long vidId;
    
    @Column(name = "Event_Id", nullable = false, updatable = false, insertable = false)
    private Long eventId;
    
    public Long getEvent_Id() {
        return this.eventId;
    }
    
    public void setVid_Id(Long vidId) {
        this.vidId = vidId;
    }
    
    public Long getVid_Id() {
        return this.vidId;
    }
    
    public void setEvent_Id(Long eventId) {
        this.eventId = eventId;
    }    
}
