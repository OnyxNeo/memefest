package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("TopicPostNotification")
public class TopicPostNotificationJSON extends PostNotificationJSON{
    
    @JsonProperty("TopicPost")
    private TopicPostJSON topicPost;

    @JsonCreator
    public TopicPostNotificationJSON(@JsonProperty("NotificationID") int notId, 
                                        @JsonProperty("TopicPost") TopicPostJSON topicPost,
                                            @JsonProperty("TimeStamp") LocalDateTime time,
                                                @JsonProperty("User") UserJSON user,
                                                    @JsonProperty("Seen") boolean seen){
        super(notId, (PostJSON)topicPost,time, user, seen);
        this.topicPost = topicPost;
    }

    public TopicPostJSON getTopicPost() {
        return topicPost;
    }

    public void setTopicPost(TopicPostJSON topicPost) {
        this.topicPost = topicPost;
    }

    
}
