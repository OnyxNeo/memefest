package com.memefest.Services;

import java.util.Set;

import com.memefest.DataAccess.EventPost;
import com.memefest.DataAccess.Repost;
import com.memefest.DataAccess.TopicPost;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.PostReply;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventPostJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.RepostJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import jakarta.ejb.Local;
import jakarta.persistence.NoResultException;

@Local
public interface PostOperations {

    public void editPostReplies(PostWithReplyJSON post);

    public void removePostReplies(PostWithReplyJSON post);

    public void editPostWithReply(PostWithReplyJSON post);

    public void removePostWithReply(PostWithReplyJSON post);

    public TopicPost getTopicPostEntity(TopicPostJSON topicPost) throws NoResultException;

    public void editTopicPost(TopicPostJSON topicPost);

    public void removeTopicPost(TopicPostJSON topicPost);

    public Post getPostEntity(PostJSON post);

    public void editPost(PostJSON post);

    public void removePost(PostJSON post);

    public Set<PostJSON> getPostWithReplyInfo(PostWithReplyJSON postWithReply);
    
    public PostJSON getPostInfo(PostJSON post);

    public void removeRepost(RepostJSON repost);

    public Set<RepostJSON> getRepostsByUser(UserJSON user);

    public void editEventPost(EventPostJSON eventPost);

    public void removeEventPost(EventPostJSON eventPost);

    public EventPost getEventPostEntity(EventPostJSON eventPost);

    public void editRepost(RepostJSON eventRepost);

    public Repost getRepostEntity(Post post, User owner) throws NoResultException;

    public Set<PostReply> getPostReplyEntities(PostWithReplyJSON post);
    
    public RepostJSON getRepostInfo(RepostJSON repost);

    public TopicPostJSON getTopicPostInfo(TopicPostJSON topicPost);

    public EventPostJSON getEventPostInfo(EventPostJSON eventPost);

    public Set<EventPostJSON> getEventPostsByEvent(EventJSON event);

    public Set<TopicPostJSON> getTopicPostsByTopic(TopicJSON topic);

    public Set<PostJSON> searchPost(PostJSON post);

    public Set<PostJSON> getAllPosts();

    public void togglePostUpvote(PostJSON post, UserJSON user);

    public void togglePostDownvote(PostJSON post, UserJSON user);

    public boolean isLikedByUser(PostJSON post, UserJSON user);

    public boolean isDownvotedByUser(PostJSON post, UserJSON user);

    //public Set<RepostJSON> getReposts(RepostJSON repost);
}
