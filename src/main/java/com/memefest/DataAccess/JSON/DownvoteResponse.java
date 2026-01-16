package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Downvote")
public class DownvoteResponse {


    //@JsonProperty("downvoteCount")
    //private int downvoteCount;

    @JsonProperty("isDownvoted")
    private boolean isDownvoted;

    @JsonCreator
    public DownvoteResponse(@JsonProperty("downvoteCount") int downvoteCount , 
                        @JsonProperty("isDownvoted") boolean isLiked){
        this.isDownvoted = isLiked;
        //this.downvoteCount = downvoteCount;
    }

    public void setIsDownvoted(boolean isDownvoted){
        this.isDownvoted = isDownvoted;
    }
    
    /* 
    public void setDownvoteCount(int count){
        this.downvoteCount = count;
    }

    public int getDownvoteCount(){
        return downvoteCount;
    }
    */

    public boolean getIsDownvoted(){
        return this.isDownvoted;
    }

}
