package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
   
@NamedQueries({@NamedQuery(name = "PostReplyEntity.getRepliesOfPostId", 
  query = "SELECT pr FROM PostReplyEntity pr WHERE pr.postReplyId.parentId = :postId"),
@NamedQuery(name =  "PostReplyEntity.getRepliesByUserId",
  query = "SELECT pr FROM PostReplyEntity pr WHERE pr.post.userId = :userId"),
@NamedQuery(name = "PostReplyEntity.getCommentCount",
  query = "SELECT COUNT(pr) FROM PostReplyEntity pr WHERE pr.postReplyId.parentId = :postId"
)
})
@Entity(name = "PostReplyEntity")
@Table(name = "REPLY")
public class PostReply{

  @EmbeddedId
  private PostReplyId postReplyId = new PostReplyId();
  
  @OneToOne(cascade = {CascadeType.MERGE}, optional = false)
  @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
  private Post post;

  @ManyToOne
  @JoinColumn(name = "Post_Info", referencedColumnName = "Post_Id")
  private Post parent;

  
  public Post getPost() {
    return this.post;
  }

  public void setParent(Post parent){
    this.postReplyId.setPost_Info(parent.getPost_Id());
    this.parent = parent;
  }

  public Post getParent(){
    return this.parent;
  }
  
  public void setPost(Post post) {
    this.postReplyId.setPost_Id(post.getPost_Id());
    this.post = post;
  }

  public Long getPost_Id(){
    return this.postReplyId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.postReplyId.setPost_Id(postId);
  }

  public Long getPost_Info() {
    return this.postReplyId.getPost_Info();
  }
  
  public void setPost_Info(Long parentId) {
    this.postReplyId.setPost_Info(parentId);
  }
}
