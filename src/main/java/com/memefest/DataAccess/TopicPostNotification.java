package com.memefest.DataAccess;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
    @NamedQuery(name = "TopicPostNotification.getTopicNotificationByTopicId",
        query = "SELECT tPN FROM TopicPostNotificationEntity tPN WHERE tPN.topicPostNot.topicId = :topicId AND tPN.seen = :seen"),
    @NamedQuery(name = "TopicPostNotification.getTopicNotificationByPostId",
        query = "SELECT tPN FROM TopicPostNotificationEntity tPN WHERE tPN.topicPostNot.postId = :postId AND tPN.seen = :seen"),
    @NamedQuery(name = "TopicPostNotification.getTopicNotificationByUserId",
        query =  "SELECT tPN FROM TopicPostNotificationEntity tPN WHERE tPN.topicPostNot.recipientId = :userId AND tPN.seen = :seen"),
    @NamedQuery(name = "TopicPostNotification.getTopicPostNotificationByPostId&UserId", 
        query =  "SELECT ePN FROM TopicPostNotificationEntity ePN WHERE ePN.topicPostNot.recipientId = :userId AND"
                    + " (ePN.topicPostNot.postId = :postId) AND ePN.seen = :seen"),
    @NamedQuery(name = "TopicPostNotification.getTopicPostNotificationByTopicId&PostId",
        query =  "SELECT ePN FROM TopicPostNotificationEntity ePN WHERE ePN.topicPostNot.topicId = :topicId AND"
                    + " (ePN.topicPostNot.postId = :postId) AND ePN.seen = :seen"),
    @NamedQuery(name = "TopicPostNotification.getTopicPostNotificationByUserId&TopicId",
        query =  "SELECT ePN FROM TopicPostNotificationEntity ePN WHERE ePN.topicPostNot.topicId = :topicId AND"
                    + " (ePN.topicPostNot.recipientId = :userId) AND ePN.seen = :seen")
})
@Entity(name = "TopicPostNotificationEntity")
@Table(name = "TOPIC_POST_NOTIFICATION")
public class TopicPostNotification {
    
    @EmbeddedId
    private TopicPostNotificationId topicPostNot = new TopicPostNotificationId();

    @Column(name = "Created", nullable = false)
    private Date created;

    @Column(name = "Seen")
    private boolean seen;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "Topic_Id", referencedColumnName = "Topic_Id")
    private Topic topic;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
    private Post post;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "UserId", referencedColumnName = "UserId")
    private User user;


    public Topic getTopic(){
        return this.topic;
    }

    public void setTopic(Topic topic){
        this.topic = topic;
        this.topicPostNot.setTopic_Id(topic.getTopic_Id());
    }

    public void setTopic_Id(int topicId){
        this.topicPostNot.setTopic_Id(topicId);
    }

    public int getTopic_Id(){
        return this.topicPostNot.getTopic_Id();
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User user){
        this.user = user;
        this.topicPostNot.setUserId(user.getUserId());
    }

    public int getUserId(){
        return this.topicPostNot.getUserId();
    }

    public void setUserId(int userId){
        this.topicPostNot.setUserId(userId);
    }

    public Post getPost(){
        return post;
    }

    public void setPost(Post post){
        this.post = post;
        this.topicPostNot.setPost_Id(post.getPost_Id());
    }

    public int getPost_Id(){
        return this.topicPostNot.getPost_Id();
    }

    public void setPost_Id(int postId){
        this.topicPostNot.setPost_Id(postId);
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
