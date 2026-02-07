package com.memefest.DataAccess;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name =  "SponsorEntity")
@Table(name = "SPONSOR")
public class Sponsor {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long userId;

    @Column(name = "Username")
    private String name;

    @Column(name = "Email")
    private String email;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<JokeOfDay> jokes;

    public Long getUserId(){
        return this.userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public Set<JokeOfDay> getJokes(){
        return jokes;
    }


}
