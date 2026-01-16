package com.memefest.DataAccess.JSON;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;

@JsonRootName("scheduledTopic")
public class ScheduledTopicsJSON {
    
    @JsonProperty("schedule")
    @JsonSerialize(keyAs = TopicJSON.class, contentAs = LocalDateTime.class, contentUsing = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(keyAs = TopicJSON.class,keyUsing = KeyDeserializer.class, contentAs =  LocalDateTime.class, contentUsing = CustomLocalDateTimeDeserializer.class)
    private Map<TopicJSON,LocalDateTime> schedules;

    @JsonCreator
    public ScheduledTopicsJSON(@JsonProperty("schedule") Map<TopicJSON,LocalDateTime> schedules){
        this.schedules = schedules;
    }

    public Map<TopicJSON, LocalDateTime> getSchedules(){
        return this.schedules;
    }

    public void setSchedules(Map<TopicJSON, LocalDateTime> schedules){
        this.schedules = schedules;
    }
}