package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
  @NamedQuery(
    name = "TopicCategory.getByTopicId", 
    query = "SELECT tc FROM TopicCategoryEntity tc WHERE tc.topicCategoryId.topicId = :topicId"),
  @NamedQuery(
    name = "TopicCategory.getByCategoryId",
    query = "SELECT tc FROM TopicCategoryEntity tc WHERE tc.topicCategoryId.catId = :categoryId"),
})
@Entity(name = "TopicCategoryEntity")
@Table(name = "TOPIC_CATEGORY")
public class TopicCategory{

  @EmbeddedId
  private TopicCategoryId topicCategoryId = new TopicCategoryId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Cat_Id", referencedColumnName = "Cat_Id")
  private Category category;
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Topic_Id", referencedColumnName = "Topic_Id")
  private Topic topic; 

  public void setCategory(Category category) {
    this.topicCategoryId.setCat_Id(category.getCat_Id());
    this.category = category;
  }
  
  public Category getCategory() {
    return this.category;
  }

  public void setTopic(Topic topic){
    this.topicCategoryId.setTopic_Id(topic.getTopic_Id());
    this.topic = topic;
  }

  public Topic getTopic(){
    return this.topic;
  }
  
  public Long getCat_Id(){
    return topicCategoryId.getCat_Id();
  }

  public void setCat_Id(Long catId){
    this.topicCategoryId.setCat_Id(catId);
  }

  public Long getTopic_Id(){
    return topicCategoryId.getTopic_Id();
  }

  public void setTopic_Id(Long topicId){
    this.topicCategoryId.setTopic_Id(topicId);
  }
}
