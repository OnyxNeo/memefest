package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicPostJSON extends PostJSON {

    @JsonProperty("topic")
    private TopicJSON topic;

  @JsonCreator
  public TopicPostJSON(@JsonProperty("id") Long postId,
     @JsonProperty("comment") String comment, 
      @JsonProperty("createdAt") LocalDateTime created, @JsonProperty("likes") int upvotes,
         @JsonProperty("downvotes") int downvotes,
          @JsonProperty("user") UserJSON user,
           @JsonProperty("topic") TopicJSON topic,
           @JsonProperty("categories") Set<CategoryJSON> categories,
                      @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats,
                      @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
    super(postId, comment,created,upvotes, downvotes, user, categories, canceledCats, taggedUsers);
    this.topic = topic;
  }

  @JsonProperty("Topic")
  public TopicJSON getTopic() {
    return this.topic;
  }

  @JsonProperty("Topic")
  public void setTopic(TopicJSON topic) {
    this.topic = topic;
  }
    
}
