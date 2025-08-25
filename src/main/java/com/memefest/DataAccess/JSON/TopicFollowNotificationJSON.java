package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("TopicFollowNotification")
public class TopicFollowNotificationJSON extends FollowNotificationJSON{
    
    @JsonProperty("Topic")
    private TopicJSON topic;

    @JsonCreator
    public TopicFollowNotificationJSON(@JsonProperty("NotificationID") int notId,
                                            @JsonProperty("Topic") TopicJSON topic,
                                                @JsonProperty("Timestamp") LocalDateTime timestamp,
                                                    @JsonProperty("Follower") UserJSON user,
                                                        @JsonProperty("Seen") boolean seen) {
        super(notId,timestamp, user, seen);
        this.topic = topic;         
    }
    
    public TopicJSON getTopic() {
        return topic;
    }

    public void setTopic(TopicJSON topic) {
        this.topic = topic;
    }
}
