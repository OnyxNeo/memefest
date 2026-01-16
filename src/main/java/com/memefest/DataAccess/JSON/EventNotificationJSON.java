package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("eventNotification")
public class EventNotificationJSON extends NotificationJSON {

    @JsonProperty("event")
    private EventJSON event;

    @JsonProperty("canceled")
    private boolean canceled;
    
    @JsonCreator
    public EventNotificationJSON(@JsonProperty("notificationID") Long notId, 
                                    @JsonProperty("timestamp") LocalDateTime date, 
                                        @JsonProperty("event")EventJSON event, 
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
