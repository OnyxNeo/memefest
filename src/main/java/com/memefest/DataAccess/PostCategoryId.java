package com.memefest.DataAccess;

import jakarta.persistence.Column;
    import jakarta.persistence.Embeddable;

    @Embeddable
    public class PostCategoryId {
    @Column(name = "Cat_Id", nullable = false, updatable = false, insertable = false)
    private Long catId;
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;
    
    public Long getCat_Id() {
        return this.catId;
    }
    
    public void setCat_Id(Long catId) {
        this.catId = catId;
    }
    
    public Long getPost_Id() {
        return this.postId;
    }
    
    public void setPost_Id(Long postId) {
        this.postId = postId;
    }
    }

