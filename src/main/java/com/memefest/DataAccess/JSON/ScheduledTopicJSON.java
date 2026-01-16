package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;

@JsonRootName("ScheduledTopic")
public class ScheduledTopicJSON{

    @JsonProperty("timestamp")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using =  CustomLocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
    
    @JsonProperty("topic")
    private TopicJSON topic;

    public ScheduledTopicJSON(@JsonProperty("timestamp") LocalDateTime timestamp, @JsonProperty("topic") TopicJSON topic){
        this.timestamp = timestamp;
        this.topic = topic;
    }

    public TopicJSON getTopic(){
        return this.topic;
    }
    
    public void setTopic(TopicJSON topic){
        this.topic = topic;
    }

    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp(){
        return this.timestamp;
    }

}
