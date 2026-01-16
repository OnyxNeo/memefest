package com.memefest.DataAccess;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@NamedQueries(
    {@NamedQuery(
        name = "JokeOfDay.findJokesBetweenDates", 
        query = "SELECT u FROM JokeOfDayEntity u WHERE u.timestamp BETWEEN :startDate AND :endDate"),
    @NamedQuery(
        name = "JokeOfDay.getOnDay",
        query =  "SELECT u FROM JokeOfDayEntity u WHERE FUNCTION('YEAR', u.ForDay) = :year AND" 
                    + " FUNCTION('MONTH', u.ForDay) = :month AND FUNCTION('DAYOFMONTH', u.ForDay) = :day"
    ),
    @NamedQuery(
        name = "JokeOfDay.getComments",
        query = "SELECT u.post FROM JokeOfDayPostEntity u WHERE u.jokeOfDay.jokeId = :jokeId"
    )
})
@Entity(name = "JokeOfDayEntity")
@Table(name = "JOKEOFDAY")
public class JokeOfDay {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "Joke_Id")
    private Long jokeId;

    @Column(name = "UserId", nullable = false, insertable =  false, updatable = false)
    private Long userId;

    @Column(name = "Joke")
    private String punchline;

    @Column(name = "ForDay")
    private Date timestamp;

    @Column(name = "Likes")
    private int likes;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private Sponsor user;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "jokeOfDay")
    @JoinColumn(name = "Joke_Id")
    private Set<JokeOfDayPost> jokeOfDayPosts;

    public Date getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp; 
    }

    public int getLikes(){
        return this.likes;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }

    public String getPunchline(){
        return this.punchline;
    }

    public void setPunchline(String punchline){
        this.punchline = punchline;
    }

    public void setJoke_Id(Long jokeId){
        this.jokeId = jokeId;
    }

    public Long getJoke_Id(){
        return this.jokeId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public Long getUserId(){
        return this.userId;
    }

    public Sponsor getUser(){
        return this.user;
    }

    public Set<JokeOfDayPost> getJokeOfDayPosts(){
        return this.jokeOfDayPosts;
    }
}
