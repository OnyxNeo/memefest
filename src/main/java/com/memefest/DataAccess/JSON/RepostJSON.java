package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonRootName("Repost")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class RepostJSON extends PostJSON{

    @JsonProperty("owner")
    private UserJSON owner;

    @JsonProperty("isCanceled")
    private boolean isCanceled;

    @JsonCreator
    public RepostJSON(@JsonProperty("id") Long postId, @JsonProperty("body") String comment, 
                        @JsonProperty("createdAt") LocalDateTime created, 
                            @JsonProperty("upvotes") int upvotes,
                                 @JsonProperty("downvotes") int downvotes,
                                    @JsonProperty("user") UserJSON user,
                                        @JsonProperty("owner") UserJSON owner, 
                                        @JsonProperty("categories") Set<CategoryJSON>  categories,
                      @JsonProperty("canceledCategories")Set<CategoryJSON> canceledCats,
                      @JsonProperty("taggedUsers") Set<UserJSON> taggedUsers) {
        super(postId,comment,created,upvotes, downvotes,user, categories,canceledCats, taggedUsers);
        this.owner = owner;
        this.isCanceled = false;
    }


    public UserJSON getOwner() {
        return this.owner;
    }

    public void setOwner(UserJSON owner) {
        this.owner = owner;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }
}
