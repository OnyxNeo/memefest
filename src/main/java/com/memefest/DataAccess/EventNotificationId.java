package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EventNotificationId {
    
    @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
    private Long userId;

    @Column(name = "Event_Id", nullable = false, updatable = false, insertable = false)
    private Long eventId;

    public Long getEvent_Id(){
        return eventId;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setEvent_Id(Long eventId){
        this.eventId = eventId;
    }
}
