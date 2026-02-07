package com.memefest.DataAccess.JSON;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateSerializer;

@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
@JsonRootName("JokeOfDay")
@JsonFilter("SponsorView")
public class JokeOfDayJSON implements Serializable{
    
    @JsonProperty("id")
    private Long jokeId;

    @JsonProperty("joke")
    private String punchline;

    @JsonProperty("date")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate timestamp;

    @JsonProperty("likes")
    private int likes;

    @JsonProperty("sponsor")
    private SponsorJSON user;

    @JsonProperty("comments")
    private Set<CommentJSON> comments;

    @JsonCreator
    public JokeOfDayJSON(@JsonProperty("id") Long jokeId,
        @JsonProperty("joke") String punchline, @JsonProperty("date") LocalDate timestamp,
        @JsonProperty("likes") int likes, @JsonProperty("user") SponsorJSON user, @JsonProperty("comments") Set<CommentJSON> comments ){
            this.jokeId = jokeId;
            this.punchline = punchline;
            this.timestamp = timestamp;
            this.comments = comments;
            this.user = user;
            this.likes = likes;
    }

    @JsonProperty("id")
    public Long getJokeId(){
        return this.jokeId;
    }

    @JsonProperty("id")
    public void setJokeId(Long jokeId){
        this.jokeId = jokeId;
    }

    @JsonProperty("joke")
    public String getPunchline(){
        return this.punchline;
    }

    @JsonProperty("joke")
    public void setPunchline(String punchline){
        this.punchline = punchline;
    }

    @JsonProperty("date")
    public LocalDate getDate(){
        return this.timestamp;
    }

    @JsonProperty("date")
    public void setDate(LocalDate timestamp){
        this.timestamp = timestamp;
    }

    @JsonProperty("sponsor")
    public SponsorJSON getUser(){
        return this.user;
    }

    @JsonProperty("sponsor")
    public void setUser(SponsorJSON user){
        this.user = user;
    }

    @JsonProperty("likes")
    public int getLikes(){
        return this.likes;
    }

    @JsonProperty("likes")
    public void setLikes(int likes){
        this.likes = likes;
    }

    @JsonProperty("comments")
    public Set<CommentJSON> getComments(){
        return this.comments;
    }

    @JsonProperty("comments")
    public void setComments(Set<CommentJSON> comments){
        this.comments = comments;
    }
}
