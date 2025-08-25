package com.memefest.DataAccess;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Set;

@NamedQueries({
  @NamedQuery(
    name = "User.findUsersByUsername",
    query = "SELECT u FROM UserEntity u Where u.username  = :username"),
  @NamedQuery(
      name = "User.findAllUsers",
      query = "SELECT u FROM UserEntity u"),    
  @NamedQuery(
    name = "User.findUsersByEmail",
    query = "SELECT u FROM UserEntity u Where u.email  = :email"),
  @NamedQuery(
    name = "User.findUsersById", 
    query = "SELECT u FROM UserEntity u Where u.userId  = :userId"), 
  @NamedQuery(
    name = "User.getUserSecurityDetails",
    query = "SELECT uSec FROM UserEntity u JOIN FETCH u.securityDetails uSec WHERE u.userId = :userId"),
  @NamedQuery(
    name = "User.emailExists", 
    query = "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email"), 
  @NamedQuery(
    name = "User.getEmailFromUsername", 
    query = "SELECT u.email FROM UserEntity u WHERE u.username = :username")
})
@Entity(name = "UserEntity")
@Table(name = "USERS")
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UserId")
  private int userId;
  
  @Column(name = "F_name")
  private String firstName;
  
  @Column(name = "L_name")
  private String lastName;
  
  @Column(name = "Username", unique = true)
  private String username;
  
  @Column(name = "Phone_No")
  private int contacts;
  
  @Column(name = "Email", unique = true)
  private String email;
  
  @Column(name = "Verified")
  private boolean verified;
  
  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "user", optional = false)
  private UserSecurity securityDetails;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "user")
  //@JoinColumn(name = "UserId")
  private Set<Post> posts;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "user")
  private Set<UserFollower> userFollowedBy;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "follower")
  private Set<UserFollower> userFollowing;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "user")
  private Set<TopicFollower> topicFollowing;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "user")
  private Set<CategoryFollower> categoriesFollowing;

  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "user")
  private Set<Video> videos;

  @OneToMany(mappedBy = "user")
  @JoinColumn(referencedColumnName = "UserId")
  //@JoinColumn(referencedColumnName = "UserId")
  private Set<Repost> reposts;

  @OneToMany(fetch = FetchType.LAZY, cascade ={CascadeType.PERSIST}, mappedBy = "user")
  @JoinColumn(referencedColumnName = "UserId")
  private Set<Event> events;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "follower")
  private Set<FollowNotification> followNotifications;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "user")
  private Set<PostNotification> postNotifications;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy= "user")
  private Set<EventNotification> eventNotifications;
  
  public Set<EventNotification> getEventNotifications(){
    return this.eventNotifications;
  }

  public void setEventNotifications(Set<EventNotification> eventNotifications){
    this.eventNotifications = eventNotifications;
  }
  
  public Set<TopicFollower> getTopicFollowing() {
    return this.topicFollowing;
  }
  
  public void setTopicFollowing(Set<TopicFollower> topicFollowers) {
    this.topicFollowing = topicFollowers;
  }
  
  public Set<UserFollower> getUserFollowedBy() {
    return this.userFollowedBy;
  }
  
  public void seUserFollowedBy(Set<UserFollower> userFollowers) {
    this.userFollowedBy = userFollowers;
  }
  
  public Set<UserFollower> getUserFollowing() {
    return this.userFollowing;
  }
  
  public void setUserFollowing(Set<UserFollower> userFollowers) {
    this.userFollowing = userFollowers;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public String getF_name() {
    return this.firstName;
  }
  
  public String getL_Name() {
    return this.lastName;
  }
  
  public void setF_name(String firstName) {
    this.firstName = firstName;
  }
  
  public void setL_Name(String lastName) {
    this.lastName = lastName;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public int getPhone_No() {
    return this.contacts;
  }
  
  public void setPhone_No(int contacts) {
    this.contacts = contacts;
  }
  
  public String getEmail() {
    return this.email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public boolean isVerified() {
    return this.verified;
  }
  
  public void setVerified(boolean verified) {
    this.verified = verified;
  }
  
  public UserSecurity getSecurityDetails() {
    return this.securityDetails;
  }
  
  public void setSecurityDetails(UserSecurity securityDetails) {
    this.securityDetails = securityDetails;
  }
  
  public int getUserId() {
    return this.userId;
  }
  
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  public Set<Post> getPosts() {
    return this.posts;
  }
  
  public void setPosts(Set<Post> posts) {
    this.posts = posts;
  }
  
  public Set<CategoryFollower> getCategoriesFollowing() {
    return this.categoriesFollowing;
  }
  
  public void setCategoriesFollowing(Set<CategoryFollower> categoriesFollowers) {
    this.categoriesFollowing = categoriesFollowers;
  }

  public Set<Video> getVideos() {
    return this.videos;
  }

  public void setVideos(Set<Video> videos) {
    this.videos = videos;
  }

  public Set<Repost> getReposts() {
    return this.reposts;
  }

  public void setReposts(Set<Repost> reposts) {
    this.reposts = reposts;
  }

  public Set<Event> getEvents(){
    return this.events;
  }

    public void setEvents(Set<Event> events) {
    this.events = events;
  }


  public Set<PostNotification> getPostNotifications(){
    return this.postNotifications;
  }

  public void setPostNotifications(Set<PostNotification> postNotifications){
    this.postNotifications = postNotifications;
  }

  public void setFollowNotifications(Set<FollowNotification> followNotifications){
    this.followNotifications = followNotifications;
  }

  public Set<FollowNotification> getFollowNotifications(){
    return this.followNotifications;
  }
}