package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Set;

@NamedNativeQueries({
  @NamedNativeQuery(
    name = "Topic.getTopicByTitle",
    query = "SELECT TOP(1) T.Topic_Id as topicId, T.Title as topicName, T.Created as created, T.Likes as likes FROM TOPIC T" 
      + " WHERE T.Title LIKE CONCAT('%',CONCAT(?, '%'))",
    resultSetMapping = "TopicEntityMapping"),
    @NamedNativeQuery(
      name = "Topic.searchByTitle",
      query = "SELECT T.Topic_Id as topicId, T.Title as topicName, T.Created as created, T.Likes as likes FROM TOPIC T" 
        + " WHERE T.title LIKE CONCAT('%',CONCAT(?, '%'))",
      resultSetMapping = "TopicEntityMapping")
})
@SqlResultSetMappings({
  @SqlResultSetMapping(
    name = "TopicEntityMapping",
    entities = {
      @EntityResult(
        entityClass = Topic.class, 
        fields = {
          @FieldResult(name = "topicId", column = "topicId"), 
          @FieldResult(name = "topicName", column = "topicName"),
          @FieldResult(name = "likes", column = "likes"), 
          @FieldResult(name = "created", column = "created")
        }
      )
    }
  )
})
@NamedQueries({
  @NamedQuery(
    name = "Topic.findByTopicId", 
    query = "SELECT p FROM TopicEntity p WHERE p.topicId = :topicId"),
    @NamedQuery(
      name = "Topic.getAll",
      query ="SELECT p FROM TopicEntity p" 
    )
})
@Entity(name = "TopicEntity")
@Table(name = "TOPIC")
public class Topic {
  
  @Id
  @Column(name = "Topic_Id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //@UuidGenerator
  private Long topicId;
  
  @Column(name = "Title")
  private String topicName;
  
  @Column(name = "Created")
  private Date created;

  @Column(name = "Likes")
  private int likes;
  
  @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "topic")
  private Set<TopicPost> topicPost;
  
  @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "topic")
  public Set<TopicFollower> followedBy;

  @OneToMany(cascade ={CascadeType.PERSIST}, mappedBy = "topic")
  @JoinColumn(referencedColumnName = "Topic_Id")
  private Set<TopicPostNotification> notifications;

  @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "topic")
  @JoinColumn(referencedColumnName = "Topic_Id")
  private Set<TopicImage> topicImages;
  
  @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "topic")
  private Set<TopicVideo> topicVideos;

  @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "topic")
  private Set<TopicCategory> topicCategories;

  /*@ManyToOne(cascade = CascadeType.ALL)
  private MainCategory mainCategory;

  @ManyToMany(cascade = CascadeType.ALL)
  private Set<SubCategory> subCategories;
  */

  public Set<TopicFollower> getFollowedBy() {
    return this.followedBy;
  }
  
  public void setFollowedBy(Set<TopicFollower> followedBy) {
    this.followedBy = followedBy;
  }
  
  public Long getTopic_Id() {
    return this.topicId;
  }
  
  public void setTopic_Id(Long topicId) {
    this.topicId = topicId;
  }
  
  public Date getCreated() {
    return this.created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public void setTopicPosts(Set<TopicPost> post) {
    this.topicPost = post;
  }
  
  public Set<TopicPost> getTopicPosts() {
    return this.topicPost;
  }
  public String getTitle() {
    return this.topicName;
  }
  
  public void setTitle(String topicName) {
    this.topicName = topicName;
  }

  public Set<TopicPostNotification> geTopictNotifications() {
    return notifications;
  }

  public void setTopicNotifications(Set<TopicPostNotification> notifications) {
    this.notifications = notifications;
  }

  public Set<TopicImage> getImages(){
    return this.topicImages;
  }

  public void setImages(Set<TopicImage> topicImages){
    this.topicImages = topicImages;
  }

  public Set<TopicVideo> getVideos(){
    return this.topicVideos;
  }

  public void setVideos(Set<TopicVideo> topicVideos){
    this.topicVideos = topicVideos;
  }

  public Set<TopicCategory> getCategories(){
    return this.topicCategories;
  }

  public void setCategories(Set<TopicCategory> categories){
    this.topicCategories = categories;
  }

  public int getLikes(){
    return this.likes;
  }  

  public void setLikes(int likes){
    this.likes = likes;
  }

  public void addLikes(){
        this.setLikes((getLikes() + 1));
    }

/* 
  public Set<SubCategory> getSubCategories(){
    return this.subCategories;
  }

  public void setSubCategories(Set<SubCategory> subCategories){
    this.subCategories = subCategories;
  } 
  public MainCategory getMainCategory(){
    return mainCategory;
  }
  
  public void setMainCategory(MainCategory mainCategory){
    this.mainCategory = mainCategory;
  }
  */
}

