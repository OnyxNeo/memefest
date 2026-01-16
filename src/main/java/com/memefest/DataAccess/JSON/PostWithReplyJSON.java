package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName("PostWithReplys")
@JsonFilter("PostWithReplyView")
public class PostWithReplyJSON extends PostJSON {
  @JsonProperty("comments") 
  private Set<PostJSON> posts;
  
  @JsonCreator
  public PostWithReplyJSON(@JsonProperty("id") Long postId,
                      @JsonProperty("body") String comment, @JsonProperty("createdAt") 
                        LocalDateTime created, @JsonProperty("likes") int upvotes, 
                        @JsonProperty("downvotes") int downvotes,
                         @JsonProperty("user") UserJSON user,
                           @JsonProperty("comments") Set<PostJSON> posts,
                           @JsonProperty("categories") Set<CategoryJSON> categories,
                      @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats, 
                      @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
    super(postId, comment, created, upvotes, downvotes, user, categories, canceledCats, taggedUsers);
    this.posts = posts;
  }
  
  @JsonProperty("comments")
  public Set<PostJSON> getPosts() {
    return this.posts;
  }
  
  @JsonProperty("comments")
  public void setPosts(Set<PostJSON> posts) {
    this.posts = posts;
  }
}
