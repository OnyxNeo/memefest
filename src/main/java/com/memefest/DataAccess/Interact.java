package com.memefest.DataAccess;

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
    @NamedQuery(
        name = "Interact.findByPostId",
        query = "SELECT p FROM InteractEntity p WHERE p.intId.postId = :postId"),
    @NamedQuery(
        name = "Interact.findByUserId", 
        query = "SELECT p FROM InteractEntity p WHERE p.intId.userId = :userId")
})
@Entity(name = "InteractEntity")
@Table(name = "INTERACT")
public class Interact{
  
  @EmbeddedId
  private InteractId intId = new InteractId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
  private Post post;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "UserId", referencedColumnName ="UserId")
  private User user;
  
  @Column(name = "Interaction")
  private boolean interact;

  public Long getPost_Id() {
    return this.intId.getPost_Id();
  } 

  public void setPost_Id(Long postId){
    this.intId.setPost_Id(postId);
  }
  
  public void setUserId(Long userId) {
    this.intId.setUserId(userId);
  }

  public void setPost(Post post) {
    this.post = post;
    this.setPost_Id(post.getPost_Id());
  }
  
  public Post getPost() {
    return this.post;
  }

  public void setUser(User user){
    this.user = user;
    this.intId.setUserId(user.getUserId());
  }

  public User getUser(){
    return this.user;
  }

  public  boolean getInteract(){
    return this.interact;
  }

  public void setInteract(boolean interact){
    this.interact = interact;
  }
}