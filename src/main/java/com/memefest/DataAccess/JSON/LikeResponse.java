package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Like")
public class LikeResponse {

    @JsonProperty("likeCount")
    private int likeCount;

    @JsonProperty("isLiked")
    private boolean isLiked;

    @JsonCreator
    public LikeResponse(@JsonProperty("likeCount") int likeCount , 
                        @JsonProperty("isLiked") boolean isLiked){
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }

    public void setIsLiked(boolean isLiked){
        this.isLiked = isLiked;
    }

    public void setLikeCount(int count){
        this.likeCount = count;
    }

    public int getLikeCount(){
        return likeCount;
    }

    public boolean getIsLiked(){
        return this.isLiked;
    }

}
