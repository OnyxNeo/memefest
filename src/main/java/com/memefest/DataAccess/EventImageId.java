package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EventImageId {

    @Column(name = "Img_Id", nullable = false, updatable = false, insertable = false)
    private Long imgId;
    
    @Column(name = "Event_Id", nullable = false, updatable = false, insertable = false)
    private Long eventId;
    
    public Long getEvent_Id() {
        return this.eventId;
    }
    
    public void setImg_Id(Long imgId) {
        this.imgId = imgId;
    }
    
    public Long getImg_Id() {
        return this.imgId;
    }
    
    public void setEvent_Id(Long eventId) {
        this.eventId = eventId;
    }    
}
