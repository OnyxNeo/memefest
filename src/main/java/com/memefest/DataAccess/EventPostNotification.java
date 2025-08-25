package com.memefest.DataAccess;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByEventId", 
        query = "SELECT ePN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.eventId = :eventId AND ePN.seen = :seen"),
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByPostId",
        query = "SELECT epN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.postId = :postId AND ePN.seen = :seen"),
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByUserId",
        query = "SELECT epN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.recipientId = :userId "
                    + "AND epN.seen = :seen"),
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByPostId&UserId",
        query =  "SELECT ePN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.recipientId = :userId AND"
                    + " ePN.eventPostNot.postId = :postId AND epN.seen = :seen"),
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByPostId&EventId",
        query =  "SELECT ePN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.eventId = :eventId AND"
                    + " ePN.eventPostNot.postId = :postId AND ePN.seen = :seen"),
    @NamedQuery(name = "EventPostNotification.getEventPostNotificationByUserId&EventId",
        query =  "SELECT ePN FROM EventPostNotificationEntity ePN WHERE ePN.eventPostNot.eventId = :eventId AND"
                    + " ePN.eventPostNot.recipientId = :userId AND ePN.seen = :seen")
})
@Entity(name = "EventPostNotificationEntity")
@Table(name = "EVENT_POST_NOTIFICATION")
public class EventPostNotification{
    
    @EmbeddedId
    private EventPostNotificationId eventPostNot = new EventPostNotificationId();

    @Column(name = "Created", nullable = false)
    private Date created;

    @Column(name = "Seen")
    private boolean seen;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "Event_Id", referencedColumnName = "Event_Id")
    private Event event;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
    private Post post;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "UserId", referencedColumnName = "UserId")
    private User user;


    public Event getEvent(){
        return this.event;
    }

    public void setEvent(Event event){
        this.event = event;
        this.eventPostNot.setPost_Id(event.getEvent_Id());
    }

    public void setEvent_Id(int eventId){
        this.eventPostNot.setEvent_Id(eventId);
    }

    public int getEvent_Id(){
        return this.eventPostNot.getEvent_Id();
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User user){
        this.user = user;
        this.eventPostNot.setUserId(user.getUserId());
    }

    public int getUserId(){
        return this.eventPostNot.getUserId();
    }

    public void setUserId(int userId){
        this.eventPostNot.setUserId(userId);
    }

    public Post getPost(){
        return post;
    }

    public void setPost(Post post){
        this.post = post;
        this.eventPostNot.setPost_Id(post.getPost_Id());
    }

    public int getPost_Id(){
        return this.eventPostNot.getPost_Id();
    }

    public void setPost_Id(int postId){
        this.eventPostNot.setPost_Id(postId);
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
