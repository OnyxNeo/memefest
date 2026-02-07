package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName("Post")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "postId")
@JsonFilter("PostView")
public class PostJSON {
  @JsonProperty("id")
  private Long postId;
  
  @JsonProperty("createdAt")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime created;
  
  @JsonProperty("likes")
  private int upvotes;
  
  @JsonProperty("body")
  private String comment;

  @JsonProperty("downvotes")
  private int downvotes;

  @JsonProperty("user")
  private UserJSON user;

  @JsonProperty("categories")
  private Set<CategoryJSON> categories;

  @JsonProperty("taggedUsers")
  private Set<UserJSON> taggedUsers;

  @JsonProperty("canceledCategories")
  private Set<CategoryJSON> canceledCats;

  @JsonProperty("cancel")
  private boolean canceled;

  @JsonProperty("isLiked")
  private boolean liked;

  @JsonProperty("isDownvoted")
  private boolean downvoted;

  @JsonProperty("Images")
  private Set<ImageJSON> images;

  @JsonProperty("Videos")
  private Set<VideoJSON> videos;

  @JsonProperty("commentCount")
  private int commentCount;
  
  @JsonCreator
  public PostJSON(@JsonProperty("id") Long postId, @JsonProperty("body") String comment, 
                      @JsonProperty("createdAt") LocalDateTime created, 
                      @JsonProperty("likes") int upvotes, @JsonProperty("downvotes") int downvotes,
                      @JsonProperty("user") UserJSON user,
                      @JsonProperty("categories") Set<CategoryJSON> categories,
                      @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats,
                      @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
    this.postId = postId;
    this.comment = comment;
    this.created = created;
    this.upvotes = upvotes;
    this.downvotes = downvotes;
    this.user = user;
    this.categories = categories;
    this.canceledCats = canceledCats;
    this.taggedUsers = taggedUsers;
    this.liked = false;
    this.downvoted = false;
  }
  
  @JsonProperty("taggedUsers")
  public Set<UserJSON> getTaggedUsers(){
    return this.taggedUsers;
  }

  public void setLiked(boolean liked){
    this.liked = liked;
  }

  public boolean getLiked(){
    return this.liked;
  }

  public void setDownvoted(boolean downvoted){
    this.downvoted = downvoted;
  }

  public boolean getDownvoted(){
    return this.downvoted;
  }

  @JsonProperty("taggedUsers")
  public void setTaggedUsers(Set<UserJSON> users){
    this.taggedUsers = users;
  }

  @JsonProperty("id")
  public Long getPostId() {
    return this.postId;
  }
  
  @JsonProperty("body")
  public String getComment() {
    return this.comment;
  }
  
  @JsonProperty("createdAt")
  public LocalDateTime getCreated() {
    return this.created;
  }
  
  @JsonProperty("likes")
  public int getUpvotes() {
    return this.upvotes;
  }
  
  @JsonProperty("downvotes")
  public int getDownvotes() {
    return this.downvotes;
  }
  
  @JsonProperty("user")
  public UserJSON getUser() {
    return this.user;
  }
  
  @JsonProperty("id")
  public void setPostId(Long postId) {
    this.postId = postId;
  }
  
  @JsonProperty("body")
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  @JsonProperty("createdAt")
  public void setCreated(LocalDateTime created) {
    this.created = created;
  }
  
  @JsonProperty("likes")
  public void setUpvotes(int upvotes) {
    this.upvotes = upvotes;
  }
  
  @JsonProperty("downvotes")
  public void setDownvotes(int downvotes) {
    this.downvotes = downvotes;
  }
  
  @JsonProperty("cancel")
  public boolean isCancelled() {
    return this.canceled;
  }
  
  @JsonProperty("cancel")
  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }
  
  @JsonProperty("user")
  public void setUser(UserJSON user) {
    this.user = user;
  }

  @JsonProperty("categories")
  public void setCategories(Set<CategoryJSON> categories){
    this.categories = categories;
  }
  
  @JsonProperty("categories")
  public Set<CategoryJSON> getCategories(){
    return this.categories;
  }

  @JsonProperty("canceledCategories")
  public void setCanceledCategories(Set<CategoryJSON> canceledCats){
    this.canceledCats = canceledCats;
  }

  @JsonProperty("canceledCategories")
  public Set<CategoryJSON> getCanceledCategories(){
    return this.canceledCats;
  }

  @JsonProperty("videos")
  public Set<VideoJSON> getVideos(){
    return this.videos;
  }

  @JsonProperty("videos")
  public void setVideos(Set<VideoJSON> videos){
    this.videos = videos;
  }

  @JsonProperty("images")
  public Set<ImageJSON> getImages(){
    return this.images;
  }

  @JsonProperty("images")
  public void setImages(Set<ImageJSON> images){
    this.images = images;
  }

  @JsonProperty("commentCount")
  public int getCommentCount(){
    return this.commentCount;    
  }

  @JsonProperty("commentCount")
  public void setCommentCount(int commentCount){
    this.commentCount = commentCount;
  }

}
