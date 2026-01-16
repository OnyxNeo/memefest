package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostReplyId {    

  @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
  private Long postId;
  
  @Column(name = "Post_Info", nullable = false, updatable = false, insertable = false)
  private Long parentId;
  
  public Long getPost_Id() {
    return this.postId;
  }
  
  public void setPost_Id(Long postId) {
    this.postId = postId;
  }
  
  public Long getPost_Info() {
    return this.parentId;
  }
  
  public void setPost_Info(Long parentId) {
    this.parentId = parentId;
  }
}