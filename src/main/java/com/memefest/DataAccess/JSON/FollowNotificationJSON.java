package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("FollowNotification")
public abstract class FollowNotificationJSON extends NotificationJSON{
    


    @JsonCreator
    public FollowNotificationJSON(@JsonProperty("NotificationID") int notId,
                                    @JsonProperty("Timestamp") LocalDateTime timestamp,
                                            @JsonProperty("User") UserJSON user,
                                                @JsonProperty("Seen") boolean seen) {
        super(notId, timestamp, Notification.FOLLOW, user, seen);
    }
}
