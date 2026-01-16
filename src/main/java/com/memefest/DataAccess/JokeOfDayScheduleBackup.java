package com.memefest.DataAccess;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity(name = "JokeOfDayBackupEntity")
@Table(name = "JOKEOFDAYSCHEDULEBACKUP")
public class JokeOfDayScheduleBackup {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "Joke_Id")
    private Long jokeId;

    @Column(name = "Author")
    private String author;

    @Column(name = "Setup")
    private String setup;

    @Column(name = "Punchline")
    private String punchline;

    @Column(name = "ForDay")
    private Date timestamp;

    public Date getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp; 
    }
    
    public String getSetup(){
        return this.setup;
    }

    public void setSetup(String setup){
        this.setup = setup;
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

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return this.author;

    }


}

