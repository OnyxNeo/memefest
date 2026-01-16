package com.memefest.DataAccess;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({@NamedQuery(name = "RepostTaggedUser.getTaggedUserIdOfPostId", 
  query = "SELECT pr FROM RepostTaggedUserEntity pr WHERE pr.postTaggedUserId.postId = :postId")})
@Entity(name = "RepostTaggedUserEntity")
@Table(name = "REPOST_TAGUSER")
public class RepostTaggedUser { 

    @EmbeddedId
    private PostTaggedUserId postTaggedUserId = new PostTaggedUserId();

    @ManyToOne
    //@JoinColumn(name="Post_Id")
    private Repost post;

    @ManyToOne
    @JoinColumn(name="UserId",insertable= false, updatable = false)
    private User taggedUser;

    public void setTaggedUser(User user){
        this.taggedUser = user;
        setUserId(user.getUserId());
    }

    public void setPost(Repost post){
        this.post = post;
        setPost_Id(post.getPost_Id());
    }

    public User getUser(){
        return this.taggedUser;
    }

    public Repost getPost(){
        return this.post;
    }

    public void setUserId(Long userId){
        this.postTaggedUserId.setUserId(userId);
    }

    public void setPost_Id(Long postId){
        this.postTaggedUserId.setPost_Id(postId);
    }
}
