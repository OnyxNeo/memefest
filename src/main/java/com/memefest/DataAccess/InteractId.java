package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InteractId {
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;

    @Column(name = "UserId", nullable = false, updatable = false, insertable = false)
    private Long userId;

    public Long getPost_Id() {
        return postId;
    }

    public void setPost_Id(Long postId){
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }
}
