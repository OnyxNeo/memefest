package com.memefest.DataAccess.JSON;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonRootName("category")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "catId")
@JsonFilter("CategoryView")
public class CategoryJSON {
    
    @JsonProperty("catId")
    private Long categoryId;

    @JsonProperty("catName")
    private String categoryName;
    
    //@JsonManagedReference(value = "categoryTopics")
    @JsonProperty("topics")
    private Set<TopicJSON> topics;

    //@JsonManagedReference(value = "categoryFollowers")
    @JsonProperty("followedBy")
    private Set<UserJSON> followedBy;

    @JsonProperty("cancelFollowedBy")
    private Set<UserJSON> canceledFollowedBy;

    public CategoryJSON(){}

    @JsonCreator
    public CategoryJSON(@JsonProperty("catId") Long categoryId, @JsonProperty("catName") String categoryName,
                            @JsonProperty("topics") Set<TopicJSON> topics, 
                                @JsonProperty("followedBy") Set<UserJSON> followers,
                                    @JsonProperty("cancelFollowedBy") Set<UserJSON> cancelFollowedBy) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.topics = topics;
    }

    @JsonProperty("catId")
    public Long getCategoryId() {
        return categoryId;
    }

    @JsonProperty("catId")
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("catName")
    public String getCategoryName() {   
        return categoryName;
    }

    @JsonProperty("catName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("topics")
    public Set<TopicJSON> getTopics() {
        return topics;
    }

    @JsonProperty("topics")
    public void setTopics(Set<TopicJSON> topics) {
        this.topics = topics;
    }

    @JsonProperty("followedBy")
    public Set<UserJSON> getFollowedBy() {
        return followedBy;
    }
    
    @JsonProperty("followedBy")
    public void setFollowedBy(Set<UserJSON> followedBy) {
        this.followedBy = followedBy;
    }

    @JsonProperty("cancelFollowedBy")
    public Set<UserJSON> getCancelFollowedBy(){
        return this.canceledFollowedBy;
    } 
    
    @JsonProperty("cancelFollowedBy")
    public void setCancelFollowedBy(Set<UserJSON> cancelFollowedBy){
        this.canceledFollowedBy = cancelFollowedBy;
    }
}
