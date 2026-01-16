package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventPostJSON extends PostJSON {

    @JsonProperty("event")
    private EventJSON event;

    @JsonCreator
     public EventPostJSON(@JsonProperty("id") Long postId, 
                            @JsonProperty("comment") String comment,
                                @JsonProperty("created") LocalDateTime created, 
                                    @JsonProperty("likes") int upvotes, 
                                        @JsonProperty("downvotes") int downvotes, 
                                            @JsonProperty("user") UserJSON user, 
                                                @JsonProperty("event") EventJSON event,
                                    @JsonProperty("categories") Set<CategoryJSON> categories,
                                    @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats, 
                                    @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
        super(postId, comment,created,upvotes, downvotes, user, categories, canceledCats,taggedUsers);
        this.event = event;
    }

@JsonProperty("event")
public EventJSON getEvent() {
        return this.event;
  }

  @JsonProperty("event")
  public void setEvent(EventJSON event) {
    this.event = event;
  }
  
}
