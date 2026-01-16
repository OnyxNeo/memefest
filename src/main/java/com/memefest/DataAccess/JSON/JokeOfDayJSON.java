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

@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
@JsonRootName("JokeOfDay")
@JsonFilter("SponsorView")
public class JokeOfDayJSON implements Serializable{
    
    @JsonProperty("id")
    private Long jokeId;

    @JsonProperty("joke")
    private String punchline;

    @JsonProperty("date")
    private LocalDate timestamp;

    @JsonProperty("likes")
    private int likes;

    @JsonProperty("author")
    private SponsorJSON user;

    @JsonProperty("comments")
    private Set<PostJSON> comments;

    @JsonCreator
    public JokeOfDayJSON(@JsonProperty("id") Long jokeId,
        @JsonProperty("joke") String punchline, @JsonProperty("date") LocalDate timestamp,
        @JsonProperty("likes") int likes, @JsonProperty("user") SponsorJSON user, @JsonProperty("comments") Set<PostJSON> comments ){
            this.jokeId = jokeId;
            this.punchline = punchline;
            this.timestamp = timestamp;
            this.comments = comments;
            this.user = user;
            this.likes = likes;
    }

    public Long getJokeId(){
        return this.jokeId;
    }


    public void setJokeId(Long jokeId){
        this.jokeId = jokeId;
    }

    public String getPunchline(){
        return this.punchline;
    }

    public void setPunchline(String punchline){
        this.punchline = punchline;
    }

    public LocalDate getDate(){
        return this.timestamp;
    }

    public void setDate(LocalDate timestamp){
        this.timestamp = timestamp;
    }

    public SponsorJSON getUser(){
        return this.user;
    }

    public void setUser(SponsorJSON user){
        this.user = user;
    }

    public int getLikes(){
        return this.likes;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }

    public Set<PostJSON> getComments(){
        return this.comments;
    }

    public void setComments(Set<PostJSON> comments){
        this.comments = comments;
    }
}
