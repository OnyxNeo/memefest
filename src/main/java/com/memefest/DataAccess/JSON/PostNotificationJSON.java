package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("PostNotification")
public class PostNotificationJSON extends NotificationJSON{
  
    @JsonProperty("Post")
    private PostJSON post;

    @JsonCreator
    public PostNotificationJSON(@JsonProperty("NotificationID") int notId,
                                    @JsonProperty("Post") PostJSON postJSON,
                                        @JsonProperty("TimeStamp") LocalDateTime time,
                                            @JsonProperty("User") UserJSON user,
                                                @JsonProperty("Seen") boolean seen){
        super(notId, time, Notification.POST,user, seen);
        this.post = postJSON;
    }
    public void setPost(PostJSON post){
        this.post = post;
    }

    public PostJSON getPost(){
        return this.post;
    }
}