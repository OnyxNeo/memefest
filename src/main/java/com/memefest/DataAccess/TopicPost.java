package com.memefest.DataAccess;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries(
    {@NamedQuery(
        name = "TopicPost.findByTopicId", 
        query = "SELECT u FROM TopicPostEntity u WHERE u.topicPostId.topicId = :topicId"), 
  @NamedQuery(
        name = "TopicPost.findByPostId", 
        query = "SELECT u FROM TopicPostEntity u WHERE u.topicPostId.postId = :postId") 
})
@Entity(name = "TopicPostEntity")
@Table(name = "TOPIC_POST")
public class TopicPost{

    @EmbeddedId
    TopicPostId topicPostId = new TopicPostId();

    @ManyToOne(cascade = CascadeType.ALL )
    //PrimaryKeyJoinColumn(name ="Topic_Id", referencedColumnName= "Topic_Id")
    @JoinColumn(name = "Topic_Id", referencedColumnName = "Topic_Id")
    private Topic topic;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "Post_Id", referencedColumnName = "Post_Id")
    private Post post;
        
    public Topic getTopic() {
        return this.topic;
    }
        
    public void setTopic(Topic topic) {
        this.topic = topic;
        this.topicPostId.setTopic_Id(topic.getTopic_Id());
    }

    public Long getTopic_Id(){
        return this.topicPostId.getTopic_Id();
    }

    public void setTopic_Id(Long topicId){
        this.topicPostId.setTopic_Id(topicId);
    }

    public Post getPost(){
        return this.post;
    }

    public void setPost(Post post){
        this.post = post;
    }

    public Long getPost_Id(){
        return topicPostId.getPost_Id();
    }

    public void setPost_Id(Long postId){
        this.topicPostId.setPost_Id(postId);
    }
}
    