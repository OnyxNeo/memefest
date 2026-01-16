package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostVideoId {
  @Column(name = "Vid_Id", nullable = false, updatable = false, insertable = false)
  private Long vidId;
  
  @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
  private Long postId;
  
  public Long getVid_Id() {
    return this.vidId;
  }
  
  public void setVid_Id(Long imgId) {
    this.vidId = imgId;
  }
  
  public Long getPost_Id() {
    return this.postId;
  }
  
  public void setPost_Id(Long postId) {
    this.postId = postId;
  }
}

