package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FollowNotificationId {
    
    @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
    private Long userId;

    @Column(name = "Follower_Id", nullable = false, updatable = false, insertable = false)
    private Long followerId;

    public Long getUserId(){
        return userId;
    }

    public Long getFollower_Id(){
        return followerId;
    }
    
    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setFollower_Id(Long followerId){
        this.followerId = followerId;
    }
}
