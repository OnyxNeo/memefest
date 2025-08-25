package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("EventNotification")
public class EventNotificationJSON extends NotificationJSON {

    @JsonProperty("Event")
    private EventJSON event;

    @JsonProperty("Canceled")
    private boolean canceled;
    
    @JsonCreator
    public EventNotificationJSON(@JsonProperty("NotificationID") int notId, 
                                    @JsonProperty("Timestamp") LocalDateTime date, 
                                        @JsonProperty("Event")EventJSON event, 
                                            @JsonProperty("User")UserJSON user,
                                                @JsonProperty("Seen") boolean seen) {
        super(notId, date, Notification.EVENT, user, seen);
        this.event = event;
        this.canceled = false;
    }

    public EventJSON getEvent() {
        return event;
    }

    public void setEvent(EventJSON event) {
        this.event = event;
    }


    public boolean isCanceled(){
        return canceled;
    }

    public void setCanceled(boolean canceled){
        this.canceled = canceled;
    }
}
