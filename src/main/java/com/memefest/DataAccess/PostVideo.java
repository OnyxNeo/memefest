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

@NamedQueries({
    @NamedQuery(
        name = "PostVideo.findByPostId",
        query = "SELECT p FROM PostVideoEntity p WHERE p.postVidId.postId = :postId"),
    @NamedQuery(
        name = "PostVideo.findByVidId", 
        query = "SELECT p FROM PostVideoEntity p WHERE p.postVidId.vidId = :vidId")
})
@Entity(name = "PostVideoEntity")
@Table(name = "POST_VIDEOS")
public class PostVideo{
  @EmbeddedId
  private PostVideoId postVidId = new PostVideoId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
  private Post post;

  @OneToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Vid_Id", referencedColumnName ="Vid_Id")
  private Video video;
  
  public Long getPost_Id()   {
    return this.postVidId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.postVidId.setPost_Id(postId);
  }
  
  public void setVid_Id(Long vidId) {
    this.postVidId.setVid_Id(vidId);
  }

  public void setPost(Post post) {
    this.post = post;
    this.setPost_Id(post.getPost_Id());
  }
  
  public Post getPost() {
    return this.post;
  }

  public Video getVideo(){
    return this.video;
  }

  public void setVideo(Video video){
    this.video = video;
    this.postVidId.setVid_Id(video.getVid_Id());
  }
}