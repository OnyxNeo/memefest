package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
    @NamedQuery(
        name = "PostImage.findByPostId",
        query = "SELECT p FROM PostImageEntity p WHERE p.postImgId.postId = :postId"),
    @NamedQuery(
        name = "PostImage.findByImgId", 
        query = "SELECT p FROM PostImageEntity p WHERE p.postImgId.imgId = :imageId")
})
@Entity(name = "PostImageEntity")
@Table(name = "POST_IMAGES")
public class PostImage {
  
  @EmbeddedId
  private PostImageId postImgId = new PostImageId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
  private Post post;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Img_Id", referencedColumnName ="Img_Id")
  private Image image;
  
  public Long getPost_Id() {
    return this.postImgId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.postImgId.setPost_Id(postId);
  }
  
  public void setImg_Id(Long imgId) {
    this.postImgId.setImg_Id(imgId);
  }

  public void setPost(Post post) {
    this.post = post;
    this.setPost_Id(post.getPost_Id());
  }
  
  public Post getPost() {
    return this.post;
  }

  public void setImage(Image image){
    this.image = image;
    this.postImgId.setImg_Id(image.getImg_Id());
  }

  public Image getImage(){
    return this.image;
  }
}