
package com.memefest.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class JokeOfDayPostId {
    
    @Column(name = "Joke_Id", nullable = false, updatable = false, insertable = false)
    private Long jokeId;
    
    @Column(name = "Post_Id", nullable = false, updatable = false, insertable = false)
    private Long postId;
    
    public Long getJoke_Id() {
        return this.jokeId;
    }
    
    public void setJoke_Id(Long jokeId) {
        this.jokeId = jokeId;
    }
    
    public Long getPost_Id() {
        return this.postId;
    }
    
    public void setPost_Id(Long postId) {
        this.postId = postId;
    }    
}
