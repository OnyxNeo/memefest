package com.memefest.DataAccess;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
    {
        @NamedQuery(
            name = "FollowNotification.findByFollowerId",
            query = "SELECT f FROM FollowNotificationEntity f WHERE f.id.followerId = :followerId AND f.seen = :seen"
        ),
        @NamedQuery(
            name = "FollowNotification.findByUserId",
            query = "SELECT f FROM FollowNotificationEntity f WHERE f.id.userId = :userId AND f.seen = :seen"
        )
    }
)
@Entity(name = "FollowNotificationEntity")
@Table(name = "FOLLOW_NOTIFICATION")
public class FollowNotification {
    
    @EmbeddedId
    private FollowNotificationId id = new FollowNotificationId();

    @Column(name = "Created", nullable = false)
    private Date created;

    @Column(name = "Seen")
    private boolean seen;

    @ManyToOne
    @JoinColumn(name = "UserId", referencedColumnName = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Follower_Id", referencedColumnName = "UserId")
    private User follower;

    public User getUser(){
        return this.user;
    }

    public User getFollower(){
        return this.follower;
    }

    public void setFollower_Id(int followerId){
        this.id.setFollower_Id(followerId);
    }

    public void setUserId(int userId){
        this.id.setUserId(userId);
    }

    public int getFollower_Id(){
        return this.id.getFollower_Id();
    }

    public int getUserId(){
        return this.id.getUserId();
    }

    public void setCreated(Date created){
        this.created = created;
    }

    public Date getCreated(){
        return this.created;
    }
    
    public boolean getSeen(){
        return this.seen;
    }

    public void setSeen(boolean seen){
        this.seen = seen;
    }
}
