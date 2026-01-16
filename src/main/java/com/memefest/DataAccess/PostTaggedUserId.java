package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostTaggedUserId {
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;

    @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
    private Long userId;

    public Long getUserId(){
        return this.userId;
    }

    public Long getPost_Id(){
        return this.postId;
    }

    public void setPost_Id(Long postId){
        this.postId = postId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }
}
