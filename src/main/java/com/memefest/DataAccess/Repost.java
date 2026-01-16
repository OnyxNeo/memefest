package com.memefest.DataAccess;


import java.util.Set;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "RepostEntity")
@NamedQueries({
    @NamedQuery(
        name = "Repost.findByPostId",
        query = "SELECT p FROM RepostEntity p WHERE p.repostId.postId = :postId"),
    @NamedQuery(
        name = "Repost.findByUserId", 
        query = "SELECT p FROM RepostEntity p WHERE p.repostId.userId = :userId")
})
@Table(name = "REPOST")
public class Repost {
    
    @EmbeddedId
    private RepostId repostId = new RepostId();

    @ManyToOne
    //@JoinColumn(referencedColumnName ="UserId", name = "UserId")
    //@MapsId("userId")
    @JoinColumn(name= "UserId")
    private User user;

    @ManyToOne
    //@MapsId("postId")
    @JoinColumn(name ="Post_Id")
    //@JoinColumn(referencedColumnName = "Post_Id", name ="Post_Id")
    private Post post;
    
    @OneToMany(mappedBy = "post")
    @JoinColumn(name="Post_Id", referencedColumnName  = "Post_Id")
    private Set<RepostTaggedUser> taggedUsers;


    public void setTaggedUsers(Set<RepostTaggedUser> taggedUsers){
        this.taggedUsers = taggedUsers;
    }

    public Set<RepostTaggedUser> getTaggedUsers(){
        return taggedUsers;
    }

    public void setUser(User user) {
        this.setUserId(user.getUserId());
        this.user = user;
    }

    public void setPost_Id(Long postId) {
        this.repostId.setPost_Id(postId);
    }

    public Long getPost_Id(){
        return this.repostId.getPost_Id();
    }

    public Long getUserId() {
        return this.repostId.getUserId();
    }

    public void setUserId(Long userId){
        this.repostId.setUserId(userId);
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }
    public void setPost(Post post){
        this.setPost_Id(post.getPost_Id());
        this.post = post;
    }

}
