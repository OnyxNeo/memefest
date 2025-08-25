package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("UserFollowNotification")
public class UserFollowNotificationJSON extends FollowNotificationJSON{

    @JsonProperty("Follower")
    private UserJSON follower;

    @JsonProperty("User")
    private UserJSON user;

    @JsonCreator
    public UserFollowNotificationJSON(@JsonProperty("NotificationID") int notID, 
                                        @JsonProperty("User") UserJSON user,
                                            @JsonProperty("TimeStamp") LocalDateTime time,
                                                @JsonProperty("Follower") UserJSON follower,
                                                    @JsonProperty("Seen") boolean seen){
        super(notID, time, user, seen);
        this.follower = follower;
    }

    public void setFollower(UserJSON follower) {
        this.follower = follower;
    }

    public UserJSON getFollower(){
        return this.follower;
    }

}
