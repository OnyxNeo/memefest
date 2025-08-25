package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Notification {
    
    FOLLOW, INTERACT, POST, TOPIC , EVENT; 

    @JsonCreator
    public static Notification forValues(@JsonProperty("NotificationType") String Type){
        for(Notification notification : Notification.values()){
            if(notification.name().equalsIgnoreCase(Type)){
                return notification;
            }
        }
        throw new IllegalArgumentException("Invalid EditableType: " + Type);
    }

    public String getValueString(){
        return this.name().toLowerCase();
    }
}
