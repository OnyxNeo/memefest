package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TopicPostNotificationId extends PostNotificationId{
    
    @Column(name = "Topic_Id", nullable = false, updatable = false, insertable = false)
    private Long topicId;
            
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;

    @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
    private Long recipientId;


    public Long getTopic_Id(){
        return this.topicId;
    }

    public void setTopic_Id(Long topicId){
        this.topicId = topicId;
    }

    public Long getPost_Id(){
        return postId;
    }

    public void setPost_Id(Long postId){
        this.postId = postId;
    }

    public Long getUserId(){
        return this.postId;
    }

    public void setUserId(Long recipientId){
        this.recipientId = recipientId;
    }
}
