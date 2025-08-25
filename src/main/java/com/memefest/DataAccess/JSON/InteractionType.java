package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum InteractionType {
    UPVOTE, DOWNVOTE;

    @JsonCreator
    public static InteractionType forValues(@JsonProperty("InteractionType") String Type){
        for(InteractionType interaction : InteractionType.values()){
            if(interaction.name().equalsIgnoreCase(Type)){
                return interaction;
            }
        }
        throw new IllegalArgumentException("Invalid InteractionType: " + Type);
    }

    public String getValueString(){
        return this.name().toLowerCase();
    }
}
