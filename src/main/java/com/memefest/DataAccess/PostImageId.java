package com.memefest.DataAccess;

import jakarta.persistence.Column;
    import jakarta.persistence.Embeddable;

    @Embeddable
    public class PostImageId {
    @Column(name = "Img_Id", nullable = false, updatable = false, insertable = false)
    private Long imgId;
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;
    
    public Long getImg_Id() {
        return this.imgId;
    }
    
    public void setImg_Id(Long imgId) {
        this.imgId = imgId;
    }
    
    public Long getPost_Id() {
        return this.postId;
    }
    
    public void setPost_Id(Long postId) {
        this.postId = postId;
    }
    }

