package com.memefest.DataAccess.JSON;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonRootName("Category")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "CatId")
@JsonFilter("CategoryPublicView")
public class CategoryJSON {
    
    @JsonProperty("CatId")
    private int categoryId;

    @JsonProperty("CatName")
    private String categoryName;
    
    //@JsonManagedReference(value = "categoryTopics")
    @JsonProperty("Topics")
    private Set<TopicJSON> topics;

    //@JsonManagedReference(value = "categoryFollowers")
    @JsonProperty("FollowedBy")
    private Set<UserJSON> followedBy;

    @JsonProperty("CancelFollowedBy")
    private Set<UserJSON> canceledFollowedBy;

    public CategoryJSON(){}

    @JsonCreator
    public CategoryJSON(@JsonProperty("CatId") int categoryId, @JsonProperty("CatName") String categoryName,
                            @JsonProperty("Topics") Set<TopicJSON> topics, 
                                @JsonProperty("FollowedBy") Set<UserJSON> followers,
                                    @JsonProperty("CancelFollowedBy") Set<UserJSON> cancelFollowedBy) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.topics = topics;
    }

    @JsonProperty("CatId")
    public int getCategoryId() {
        return categoryId;
    }

    @JsonProperty("CatId")
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("CatName")
    public String getCategoryName() {   
        return categoryName;
    }

    @JsonProperty("CatName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("Topics")
    public Set<TopicJSON> getTopics() {
        return topics;
    }

    @JsonProperty("Topics")
    public void setTopics(Set<TopicJSON> topics) {
        this.topics = topics;
    }

    @JsonProperty("FollowedBy")
    public Set<UserJSON> getFollowedBy() {
        return followedBy;
    }
    
    @JsonProperty("FollowedBy")
    public void setFollowedBy(Set<UserJSON> followedBy) {
        this.followedBy = followedBy;
    }

    @JsonProperty("CancelFollowedBy")
    public Set<UserJSON> getCancelFollowedBy(){
        return this.canceledFollowedBy;
    } 
    
    @JsonProperty("CancelFollowedBy")
    public void setCancelFollowedBy(Set<UserJSON> cancelFollowedBy){
        this.canceledFollowedBy = cancelFollowedBy;
    }
}
