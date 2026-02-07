package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFilter("CommentView")
public class CommentJSON extends PostJSON{

@JsonCreator
  public CommentJSON(@JsonProperty("commentId") Long postId, @JsonProperty("content") String comment, 
                      @JsonProperty("createdAt") LocalDateTime created, 
                      @JsonProperty("likes") int upvotes, @JsonProperty("downvotes") int downvotes,
                      @JsonProperty("user") UserJSON user,
                      @JsonProperty("categories") Set<CategoryJSON> categories,
                      @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats,
                      @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
    super(postId, comment, created, upvotes, downvotes, user, categories, canceledCats, taggedUsers);
    super.setPostId(postId);
  }

  @JsonProperty("commentId")
  @Override
  public void setPostId(Long postId){
    super.setPostId(postId);
  }

  @JsonProperty("commentId")
  @Override
  public Long getPostId(){
    return super.getPostId();
  }
  
  @JsonProperty("content")
  @Override
  public String getComment() {
    return super.getComment();
  }
  
  @JsonProperty("content")
  @Override
  public void setComment(String comment) {
    super.setComment(comment);
  }
  
}
