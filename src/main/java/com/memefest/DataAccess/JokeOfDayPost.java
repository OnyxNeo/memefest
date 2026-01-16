package com.memefest.DataAccess;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
    {@NamedQuery(
        name = "JokeOfDayPost.findByJokeId", 
        query = "SELECT u FROM JokeOfDayPostEntity u WHERE u.jokeOfDayPostId.jokeId = :jokeId")
})
@Entity(name = "JokeOfDayPostEntity")
@Table(name = "JOKEOFDAY_POST")
public class JokeOfDayPost {
  @EmbeddedId
  private JokeOfDayPostId jokeOfDayPostId = new JokeOfDayPostId();

  @ManyToOne
  @JoinColumn(name = "Post_Id")
  private Post post;

  @ManyToOne
  @JoinColumn(name ="Joke_Id")
  private JokeOfDay jokeOfDay;
  
  public Long getPost_Id() {
    return this.jokeOfDayPostId.getPost_Id();
  }

  public void setPost_Id(Long postId){
    this.jokeOfDayPostId.setPost_Id(postId);
  }
  
  public void setJoke_Id(Long jokeId) {
    this.jokeOfDayPostId.setJoke_Id(jokeId);
  }

  public void setPost(Post post) {
    this.setPost_Id(post.getPost_Id());
    this.post = post;
  }
  
  public Post getPost() {
    return this.post;
  }

  public JokeOfDay getJokeOfDay(){
    return this.jokeOfDay;
  }

  public void setJokeOfDay(JokeOfDay jokeOfDay){
    this.setJoke_Id(jokeOfDay.getJoke_Id());
    this.jokeOfDay = jokeOfDay;
  }
}
    