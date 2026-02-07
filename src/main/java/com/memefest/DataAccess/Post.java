package com.memefest.DataAccess;

import java.util.Date;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FetchType;
import jakarta.persistence.FieldResult;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;

@NamedNativeQueries({
    @NamedNativeQuery(name = "Post.getPostByComment",
        query = "SELECT TOP(1) P.Post_Id as postId, P.Comment  as comment, P.Created as created," 
                   + " P.UserId as userId FROM POST P "
                + "WHERE CONTAINS(P.Comment,?)"
        , resultSetMapping = "PostEntityMapping"
    ),
    /*  
    */
    @NamedNativeQuery(name = "Post.searchByComment",
        query = "SELECT P.Post_Id as postId, P.Comment as comment, P.Created as created," 
                   + " P.UserId as userId FROM POST P "
                + "WHERE P.Comment LIKE CONCAT(CONCAT( '%',?),'%') AND P.Post_Id NOT IN(SELECT REPLY.Post_Id FROM REPLY) AND "
                + "P.Post_Id NOT IN (SELECT EVENT_POST.Post_Id FROM EVENT_POST) AND P.Post_Id NOT IN "
                + "(SELECT TOPIC_POST.Post_Id FROM TOPIC_POST) AND P.Post_Id NOT IN (SELECT JOKEOFDAY_POST.Post_Id FROM JOKEOFDAY_POST)"
        , resultSetMapping = "PostEntityMapping"
    ),
    @NamedNativeQuery(name = "Post.getByUserId",
        query = "SELECT P.Post_Id as postId, P.Comment as comment, P.Created as created, "
                                   +"P.UserId as userId FROM POST P "
                + "WHERE P.UserId = ? AND P.Post_Id NOT IN(SELECT REPLY.Post_Id FROM REPLY) AND "
                + "P.Post_Id NOT IN (SELECT EVENT_POST.Post_Id FROM EVENT_POST) AND P.Post_Id NOT IN "  
                + "(SELECT TOPIC_POST.Post_Id FROM TOPIC_POST) AND P.Post_Id NOT IN (SELECT JOKEOFDAY_POST.Post_Id FROM JOKEOFDAY_POST)",
        resultSetMapping = "PostEntityMapping"
    )
})
@SqlResultSetMappings(
    @SqlResultSetMapping(
        name = "PostEntityMapping",
        entities = {
            @EntityResult(
                entityClass = Post.class,
                fields = {
                    @FieldResult(name = "postId", column = "postId"),
                    @FieldResult(name = "comment", column = "comment"),
                    @FieldResult(name = "created", column = "created"),
                    @FieldResult(name = "userId", column = "userId"),
                }
            )
        }
    )   
)
@NamedQueries({
    @NamedQuery(
        name = "Post.getAll",
        query = "SELECT po FROM PostEntity po WHERE po.postId NOT IN (SELECT pr.postReplyId.postId FROM PostReplyEntity pr)"
    )
})
@Entity(name = "PostEntity")
@Table(name = "POST")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Post_Id")
    //@UuidGenerator
    private Long postId;

    @Column(name = "Comment")
    private String comment;
    
    @Column(name = "UserId", nullable = false, insertable =  false, updatable = false)
    private Long userId;

    @Column(name = "Created")
    private Date created;
    
    /* 
    @Column(name = "Video_Id",nullable = true, insertable = true, updatable = true)
    private int videoId;

    @Column(name = "Img_Id", nullable = true, insertable = true, updatable = true)
    private int imageId;
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;   

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    @JoinColumn(referencedColumnName =  "Post_Id")
    private Set<PostTaggedUser> taggedUsers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<PostVideo> videos;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    @JoinColumn(referencedColumnName = "Post_Id")
    private Set<EventPost> eventPosts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<TopicPost> topicPosts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<JokeOfDayPost> jokeOfDays;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<PostImage> images;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "parent")
    private Set<PostReply> postWithReplys;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post", optional = true )
    private PostReply replyTo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<PostNotification> notifications;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "Post_Id")
    private Set<TopicPostNotification> topicPostNotifications;

    @OneToMany(fetch =  FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    @JoinColumn(referencedColumnName = "Post_Id")    
    private Set<Repost> reposts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "post")
    private Set<PostCategory> categories;


    @OneToMany(fetch =  FetchType.LAZY, cascade =  CascadeType.PERSIST, mappedBy = "post")
    @JoinColumn(referencedColumnName = "Post_Id")
    private Set<Interact> interactions;

    public Long getPost_Id(){
        return postId;
    }

    public void setTaggedUsers(Set<PostTaggedUser> taggedUsers){
        this.taggedUsers = taggedUsers;
    }

    public Set<PostTaggedUser> getTaggedUsers(){
        return this.taggedUsers;
    }

    public Set<PostCategory> getCategories(){
        return this.categories; 
    }

    public void setPost_Id(Long postId){
        this.postId = postId;
    }
    
    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }    
    
    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }   

    public Set<PostReply> postReplys(){
        return postWithReplys;
    }

    public void setPostReplys(Set<PostReply> postReplys){
        this.postWithReplys = postReplys;
    }

    public Set<PostNotification> getNotifications(){
        return this.notifications;
    }

    public Set<Interact> getInteractions(){
        return this.interactions;
    }

    public void setNotifications(Set<PostNotification> notifications) {
        this.notifications = notifications;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void addReply(PostReply postReply) {
        postWithReplys.add(postReply);
    }

    public void removeReply(PostReply postReply) {
        postWithReplys.remove(postReply);
    }

    public int getReplyCount() {
        return postWithReplys.size();
    } 

    public Set<PostImage> getImages() {
        return images;    
    }

    public Set<PostVideo> getVideos() {
        return videos;
    }

    public void setVideos(Set<PostVideo> videos) {
        this.videos = videos;
    }

    public void setReposts(Set<Repost> reposts){
        this.reposts = reposts;
    }

    public Set<Repost> getReposts(){
        return this.reposts;
    }

    public Set<EventPost> getEventPosts(){
        return this.eventPosts;
    }
    
}
