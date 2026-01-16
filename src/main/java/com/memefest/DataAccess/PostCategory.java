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
        name = "PostCategory.findByPostId",
        query = "SELECT p FROM PostCategoryEntity p WHERE p.postCatId.postId = :postId"),
    @NamedQuery(
        name = "PostCategory.findByCatId", 
        query = "SELECT p FROM PostCategoryEntity p WHERE p.postCatId.catId = :catId")
})
@Entity(name = "PostCategoryEntity")
@Table(name = "POST_CATEGORY")
public class PostCategory {
  
  @EmbeddedId
  private PostCategoryId postCatId = new PostCategoryId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
  private Post post;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name= "Cat_Id", referencedColumnName ="Cat_Id")
  private Category category;
  
  public Long getPost_Id() {
    return this.postCatId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.postCatId.setPost_Id(postId);
  }
  
  public void setCat_Id(Long catId) {
    this.postCatId.setCat_Id(catId);
  }

  public Long getCat_Id(){
    return this.postCatId.getCat_Id();
  }

  public void setPost(Post post) {
    this.post = post;
    this.setPost_Id(post.getPost_Id());
  }
  
  public Post getPost() {
    return this.post;
  }

  public void setCategory(Category category){
    this.category = category;
    this.postCatId.setCat_Id(category.getCat_Id());
  }

  public Category getCategory(){
    return this.category;
  }
}