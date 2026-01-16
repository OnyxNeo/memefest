package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("EventPostNotification")
public class EventPostNotificationJSON extends PostNotificationJSON{
    
    @JsonProperty("EventPost")
    private EventPostJSON eventPost;

    @JsonCreator
    public EventPostNotificationJSON(@JsonProperty("NotificationID") Long notId,
                                    @JsonProperty("EventPost") EventPostJSON eventPost,
                                        @JsonProperty("TimeStamp") LocalDateTime time, 
                                            @JsonProperty("User") UserJSON user,
                                                @JsonProperty("Seen") boolean seen) {
        super(notId, (PostJSON) eventPost, time, user, seen);
    }   

    public EventPostJSON getEventPost() {
        return eventPost;
    }

    public void setEventPost(EventPostJSON eventPost) {
        this.eventPost = eventPost;
    }

}
