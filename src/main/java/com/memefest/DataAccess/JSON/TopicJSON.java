package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName("Topic")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "TopicId")
@JsonFilter("TopicView")
public class TopicJSON implements Serializable{
  
  @JsonProperty("topicId")
  private Long topicId;
  
  @JsonProperty("title")
  private String title;
  
  @JsonProperty("created")
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  private LocalDateTime created;
  
  @JsonProperty("categories")
  private Set<CategoryJSON> categories;
  
  @JsonProperty("posts")
  private Set<TopicPostJSON> posts;
  
  @JsonProperty("followedBy")
  private Set<UserJSON> followedBy;
  
  @JsonProperty("cancelFollowedBy")
  private Set<UserJSON> cancelFollowedBy;
  
  @JsonProperty("cancelCategories")
  private Set<CategoryJSON> cancelCategories;
  
  @JsonProperty("cancel")
  private boolean canceled;
  
  @JsonCreator
  public TopicJSON(@JsonProperty("topicId") Long topicId,
                    @JsonProperty("title") String title, 
                      @JsonProperty("created") LocalDateTime created, 
                        @JsonProperty("categories") Set<CategoryJSON> categories, 
                          @JsonProperty("posts") Set<TopicPostJSON> posts, 
                            @JsonProperty("followedBy") Set<UserJSON> followedBy) {
    this.topicId = topicId;
    this.title = title;
    this.created = created;
    this.categories = categories;
    this.posts = posts;
    this.followedBy = followedBy;
    this.canceled = false;
  }
  
  @JsonProperty("topicId")
  public Long getTopicId() {
    return this.topicId;
  }
  
  @JsonProperty("title")
  public String getTitle() {
    return this.title;
  }
  
  @JsonProperty("created")
  public LocalDateTime getCreated() {
    return this.created;
  }
  
  @JsonProperty("created")
  public void setCreated(LocalDateTime created) {
    this.created = created;
  }
  
  @JsonProperty("costs")
  public Set<TopicPostJSON> getPosts() {
    return this.posts;
  }
  
  @JsonProperty("followedBy")
  public Set<UserJSON> getFollowedBy() {
    return this.followedBy;
  }
  
  @JsonProperty("topicId")
  public void setTopicId(Long topicId) {
    this.topicId = topicId;
  }
  
  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }
  
  @JsonProperty("cancel")
  public boolean isCancelled() {
    return this.canceled;
  }
  
  @JsonProperty("cancel")
  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }
  
  @JsonProperty("categories")
  public void setCategories(Set<CategoryJSON> categories) {
    this.categories = categories;
  }
  
  @JsonProperty("categories")
  public Set<CategoryJSON> getCategories() {
    return this.categories;
  }
  
  @JsonProperty("posts")
  public void setPosts(Set<TopicPostJSON> posts) {
    this.posts = posts;
  }
  
  @JsonProperty("followedBy")
  public void setFollowedBy(Set<UserJSON> followedBy) {
    this.followedBy = followedBy;
  }
  
  @JsonProperty("cancelFollowedBy")
  public Set<UserJSON> getCancelFollowedBy() {
    return this.cancelFollowedBy;
  }
  
  @JsonProperty("cancelFollowedBy")
  public void setCancelFollowedBy(Set<UserJSON> cancelFollowedBy) {
    this.cancelFollowedBy = cancelFollowedBy;
  }
  
  @JsonProperty("cancelCategories")
  public Set<CategoryJSON> getCancelCategories() {
    return this.cancelCategories;
  }
  
  @JsonProperty("cancelCategories")
  public void setCancelCategories(Set<CategoryJSON> cancelCategories) {
    this.cancelCategories = cancelCategories;
  }
}
