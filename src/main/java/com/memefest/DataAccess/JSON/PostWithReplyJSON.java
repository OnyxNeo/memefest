package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName("PostWithReplys")
//@JsonFilter("PostWithReplyPublicView")
public class PostWithReplyJSON extends PostJSON {
  @JsonProperty("Replys")
  private Set<PostJSON> posts;
  
  @JsonCreator
  public PostWithReplyJSON(@JsonProperty("PostId") int postId,
                      @JsonProperty("Comment") String comment, @JsonProperty("Created") 
                        LocalDateTime created, @JsonProperty("Upvotes") int upvotes, 
                        @JsonProperty("Downvotes") int downvotes,
                         @JsonProperty("User") UserJSON user,
                           @JsonProperty("Replys") Set<PostJSON> posts,
                           @JsonProperty("Categories") Set<CategoryJSON> categories,
                      @JsonProperty("CanceledCategories")Set<CategoryJSON> canceledCats) {
    super(postId, comment, created, upvotes, downvotes, user, categories, canceledCats);
    this.posts = posts;
  }
  
  @JsonProperty("Replys")
  public Set<PostJSON> getPosts() {
    return this.posts;
  }
  
  @JsonProperty("Replys")
  public void setPosts(Set<PostJSON> posts) {
    this.posts = posts;
  }
}
