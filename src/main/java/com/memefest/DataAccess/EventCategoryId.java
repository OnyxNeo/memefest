package com.memefest.DataAccess;

import jakarta.persistence.Column;
    import jakarta.persistence.Embeddable;

    @Embeddable
    public class EventCategoryId {
    @Column(name = "Cat_Id", nullable = false, updatable = false, insertable = false)
    private Long catId;
    
    @Column(name = "Event_Id", nullable = false, updatable = false, insertable = false)
    private Long eventId;
    
    public Long getCat_Id() {
        return this.catId;
    }
    
    public void setCat_Id(Long catId) {
        this.catId = catId;
    }
    
    public Long getEvent_Id() {
        return this.eventId;
    }
    
    public void setEvent_Id(Long postId) {
        this.eventId = postId;
    }

    }

