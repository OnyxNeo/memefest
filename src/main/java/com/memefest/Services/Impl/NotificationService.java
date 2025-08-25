package com.memefest.Services.Impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.memefest.DataAccess.Event;
import com.memefest.DataAccess.EventNotification;
import com.memefest.DataAccess.EventNotificationId;
import com.memefest.DataAccess.EventPost;
import com.memefest.DataAccess.EventPostNotification;
import com.memefest.DataAccess.EventPostNotificationId;
import com.memefest.DataAccess.FollowNotification;
import com.memefest.DataAccess.FollowNotificationId;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.PostNotification;
import com.memefest.DataAccess.PostNotificationId;
import com.memefest.DataAccess.Topic;
import com.memefest.DataAccess.TopicFollowNotification;
import com.memefest.DataAccess.TopicFollowNotificationId;
import com.memefest.DataAccess.TopicPost;
import com.memefest.DataAccess.TopicPostNotification;
import com.memefest.DataAccess.TopicPostNotificationId;
import com.memefest.DataAccess.User;import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventNotificationJSON;
import com.memefest.DataAccess.JSON.EventPostJSON;
import com.memefest.DataAccess.JSON.EventPostNotificationJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostNotificationJSON;
import com.memefest.DataAccess.JSON.TopicFollowNotificationJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.TopicPostNotificationJSON;
import com.memefest.DataAccess.JSON.UserFollowNotificationJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.EventOperations;
import com.memefest.Services.FeedsOperations;
import com.memefest.Services.NotificationOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.TopicOperations;
import com.memefest.Services.UserOperations;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;

@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "NotificationService")
public class NotificationService implements NotificationOperations{
    
    @PersistenceContext(unitName = "memeFest", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @EJB
    private PostOperations postOps;
    
    @EJB
    private UserOperations userOps;

    @EJB 
    private TopicOperations topicOps;

    @EJB
    private EventOperations eventOps;

    @EJB 
    private FeedsOperations feedsOps;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //throw a custom exception to show object was not created
    private void createTopicPostNotification(TopicPost topicPost, User user)throws NoResultException{
        if(topicPost == null || user == null)
            return;
            TopicPostNotification topicPostNot = new TopicPostNotification();
            topicPostNot.setPost(topicPost.getPost());
            topicPostNot.setUser(user);
            topicPostNot.setTopic(topicPost.getTopic());
            topicPostNot.setSeen(false);
            entityManager.persist(topicPost);

            feedsOps.sendToUsers(topicPostNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //throw a custom exception to show object was not created
    public void createUserFollowNotification(UserFollowNotificationJSON userFollowNot){
        if(userFollowNot == null)
            return;
        User user = userOps.getUserEntity(userFollowNot.getFollower());
        User follower = userOps.getUserEntity(userFollowNot.getUser());
        FollowNotification followNot = new FollowNotification();
        followNot.setFollower_Id(follower.getUserId());
        followNot.setUserId(user.getUserId());
        followNot.setSeen(false);
        entityManager.persist(followNot);
        
        feedsOps.sendToAll(userFollowNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createTopicFollowNotification(TopicFollowNotificationJSON topicFollowNot){
        if(topicFollowNot == null)
            return;
        User user = userOps.getUserEntity(topicFollowNot.getUser());
        Topic topic = topicOps.getTopicEntity(topicFollowNot.getTopic());
        TopicFollowNotification followNot = new TopicFollowNotification();
        followNot.setTopic_Id(topic.getTopic_Id());
        followNot.setUserId(user.getUserId());
        followNot.setSeen(false);
        entityManager.persist(followNot);
        feedsOps.sendToAll(topicFollowNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //throw a custom exception to show object was not created
    public void editTopicPostNotification(TopicPostNotificationJSON topicPostNot){
        if(topicPostNot == null)
            throw new NoResultException("No Notification");
        
        try{
            TopicPostNotification topicPostNotEntity = getTopicPostNotificationEntity(topicPostNot);
            topicPostNotEntity.setSeen(true);
            entityManager.merge(topicPostNotEntity);   
        }
        catch(NoResultException ex){
            User user = userOps.getUserEntity(topicPostNot.getUser());
            TopicPost topicPost = postOps.getTopicPostEntity(topicPostNot.getTopicPost());
            createTopicPostNotification(topicPost, user);
            return;     
        }
    }

    public void removeTopicPostNotification(TopicPostNotificationJSON postNot){
        try{
            TopicPostNotification postNotification = getTopicPostNotificationEntity(postNot);
            this.entityManager.remove(postNotification);
        }
        catch(NoResultException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void editUserFollowNotification(UserFollowNotificationJSON userFollowNotification){
        if(userFollowNotification == null)
            throw new NoResultException("No Notification found");
        try{
            FollowNotification userFollowEntity = getUserFollowNotificationEntity(userFollowNotification);
            userFollowEntity.setSeen(true);
            entityManager.merge(userFollowEntity);
        }
        catch(NoResultException ex){
            createUserFollowNotification(userFollowNotification);
            return;
        }
    }   

    public void removeUserFollowNotification(UserFollowNotificationJSON followNot){
        try{
            FollowNotification followNotification = getUserFollowNotificationEntity(followNot);
            this.entityManager.remove(followNotification);
        }catch(NoResultException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void editTopicFollowNotification(TopicFollowNotificationJSON topicFollowNot){
        if(topicFollowNot == null)
            throw new NoResultException("No Notification found");
        try {
            TopicFollowNotification userFollowEntity = getTopicFollowNotificationEntity(topicFollowNot);
            userFollowEntity.setSeen(true);
            entityManager.merge(userFollowEntity);
        } catch (NoResultException e) {
            createTopicFollowNotification(topicFollowNot);
            return;
        }    
    }

    public void removeTopicFollowNotification(TopicFollowNotificationJSON followNot){
        try{
            TopicFollowNotification followNotification = getTopicFollowNotificationEntity(followNot);
            this.entityManager.remove(followNotification);
        }
        catch(NoResultException ex){
            return;
        }
    }



    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TopicFollowNotification getTopicFollowNotificationEntity(TopicFollowNotificationJSON topicFollowNot) throws NoResultException{
        if(topicFollowNot != null){
            if(topicFollowNot.getUser() != null && topicFollowNot.getTopic() != null){
                User user = userOps.getUserEntity(topicFollowNot.getUser());
                Topic topic = topicOps.getTopicEntity(topicFollowNot.getTopic());
                TopicFollowNotificationId followNotId = new TopicFollowNotificationId();
                followNotId.setUserId(user.getUserId());
                followNotId.setTopic_Id(topic.getTopic_Id());
                TopicFollowNotification topicFollowNotEntity = entityManager.find(TopicFollowNotification.class, followNotId);
                if(topicFollowNotEntity != null)
                    return topicFollowNotEntity;
                else throw new NoResultException();
            }
            else
                throw new NoResultException("No Post Notification found for Topic");
        }
        else
            throw new NoResultException("No Post Notification found for Topic");        
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public FollowNotification getUserFollowNotificationEntity(UserFollowNotificationJSON userFollow) throws NoResultException{
        if(userFollow != null){
            if(userFollow.getFollower() != null && userFollow.getUser() != null){
                User user = userOps.getUserEntity(userFollow.getUser());
                User follower = userOps.getUserEntity(userFollow.getFollower());
                FollowNotificationId followNotId = new FollowNotificationId();
                followNotId.setUserId(user.getUserId());
                followNotId.setFollower_Id(follower.getUserId());
                FollowNotification followNotEntity = entityManager.find(FollowNotification.class, followNotId);
                if(followNotEntity != null)
                    return followNotEntity;
                else throw new NoResultException();
            }
            else
                throw new NoResultException("No Post Notification found for Topic");
        }
        else
            throw new NoResultException("No Post Notification found for Topic");
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TopicPostNotification getTopicPostNotificationEntity(TopicPostNotificationJSON topicPostNotification) throws NoResultException{
        if(topicPostNotification != null){
            if(topicPostNotification.getPost() != null && topicPostNotification.getUser() != null){
                User user = userOps.getUserEntity(topicPostNotification.getUser());

                TopicPost topicPost = postOps.getTopicPostEntity(topicPostNotification.getTopicPost());
                TopicPostNotificationId postNotId = new TopicPostNotificationId();
                postNotId.setPost_Id(topicPost.getPost_Id());
                postNotId.setUserId(user.getUserId());
                postNotId.setTopic_Id(topicPost.getTopic_Id());
                TopicPostNotification topicNotEntity = entityManager.find(TopicPostNotification.class, postNotId);
                if(topicNotEntity != null)
                    return topicNotEntity;
                else throw new NoResultException();
            }
            else
                throw new NoResultException("No Post Notification found for Topic");
        }
        else
            throw new NoResultException("No Post Notification found for Topic");  
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PostNotification getPostNotificationEntity(PostNotificationJSON postNotification) throws NoResultException{
        if(postNotification != null){
            if(postNotification.getPost() != null && postNotification.getUser() != null){
                PostJSON post = postOps.getPostInfo(postNotification.getPost());
                PostNotificationId postNotId = new PostNotificationId();
                postNotId.setPost_Id(post.getPostId());
                postNotId.setUserId(post.getUser().getUserId());
                PostNotification postEntity = entityManager.find(PostNotification.class, postNotId);
                if(postEntity != null)
                    return postEntity;
                else throw new NoResultException();
            }
            else
                throw new NoResultException("No Post Notification found");
        }
        else
            throw new NoResultException("No Post Notification found");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void createPostNotification(PostNotificationJSON postNotification){
        if(postNotification == null)
            return;
        PostNotification postNot = new PostNotification();
        User user = userOps.getUserEntity(postNotification.getPost().getUser());
        Post post = postOps.getPostEntity(postNotification.getPost());
        postNot.setPost(post);
        postNot.setUser(user);
        postNot.setSeen(false);
        entityManager.persist(postNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void editPostNotification(PostNotificationJSON postNot){
        if(postNot == null)
            throw new NoResultException("No Notification found");
            
        try{
            PostNotification postNotEntity = getPostNotificationEntity(postNot);
            postNotEntity.setSeen(postNot.getSeen());
        }
        catch(NoResultException ex){    
            createPostNotification(postNot);
            return;
        }
    }

    public void removePostNotification(PostNotificationJSON postNot){
        try{
            PostNotification postNotification = getPostNotificationEntity(postNot);
            this.entityManager.remove(postNotification);
        }
        catch(NoResultException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //throw a custom exception to show object was not created
    public void createEventNotification(EventNotificationJSON eventNotification){ 
        User user = userOps.getUserEntity(eventNotification.getUser());
        Event event = eventOps.getEventEntity(eventNotification.getEvent());
        EventNotification eventNot = new EventNotification();
        eventNot.setEvent(event);
        eventNot.setUser(user);
        entityManager.persist(eventNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //throw a custom exception to show object was not created
    public void editEventNotification(EventNotificationJSON eventNot){
        if(eventNot == null)
            throw new NoResultException("No Event Notification found");
        try{
            EventNotification eventNotEntity = getEventNotificationEntity(eventNot);
            eventNotEntity.setSeen(eventNot.getSeen());
            entityManager.merge(eventNotEntity);
        }
        catch(NoResultException ex){
            createEventNotification(eventNot);
        }
    }

    public void removeEventNotification(EventNotificationJSON eventNot){
        try{
            EventNotification eventNotification = getEventNotificationEntity(eventNot);
            this.entityManager.remove(eventNotification);
        }
        catch(NoResultException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EventNotification getEventNotificationEntity(EventNotificationJSON eventNotification) throws NoResultException{
        if(eventNotification != null && eventNotification.getEvent() != null){
            Event event = eventOps.getEventEntity(eventNotification.getEvent());
            User user = userOps.getUserEntity(eventNotification.getUser());
            EventNotificationId eventNotId = new EventNotificationId();
            eventNotId.setEvent_Id(event.getEvent_Id());
            eventNotId.setUserId(user.getUserId());
            EventNotification eventNotEntity = entityManager.find(EventNotification.class, eventNotId);
             if(eventNotEntity == null)
                throw new NoResultException();
            return eventNotEntity;
            
        }
        else
            throw new NoResultException("No Event Notification found");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //throw a custom exception to show object was not created
    private void createEventPostNotification(EventPost eventPost, User user){
        if(eventPost  == null || user == null)
            return;
        EventPostNotification eventPostNot = new EventPostNotification();
        eventPostNot.setEvent(eventPost.getEvent());
        eventPostNot.setPost(eventPost.getPost());
        eventPostNot.setUser(user);
        entityManager.persist(eventPostNot);
        feedsOps.sendToAll(eventPostNot);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //throw a custom exception to show object was not created
    public void editEventPostNotification(EventPostNotificationJSON eventPostNot){
        if(eventPostNot == null)
            throw new NoResultException("No Notification");
        try{
            EventPostNotification eventPostNotEntity = getEventPostNotificationEntity(eventPostNot); 
            eventPostNotEntity.setSeen(eventPostNot.getSeen());
            entityManager.merge(eventPostNotEntity); 
        }
        catch(NoResultException ex){ 
            EventPost eventPost = postOps.getEventPostEntity(eventPostNot.getEventPost());
            User user = userOps.getUserEntity(eventPostNot.getUser());
            createEventPostNotification(eventPost, user);
            return;     
        }
    }
    
    public void removeEventPostNotification(EventPostNotificationJSON postNot){
        try{
            EventPostNotification postNotification = getEventPostNotificationEntity(postNot);
            this.entityManager.remove(postNotification);
        }
        catch(NoResultException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public EventPostNotification getEventPostNotificationEntity(EventPostNotificationJSON eventPostNotification) throws NoResultException{
        if(eventPostNotification != null && eventPostNotification.getEventPost() != null){
            EventPostNotificationId postNotificationId = new EventPostNotificationId();
            User user = userOps.getUserEntity(eventPostNotification.getUser());
            EventPost post = postOps.getEventPostEntity(eventPostNotification.getEventPost());
            postNotificationId.setPost_Id(post.getPost_Id());
            postNotificationId.setEvent_Id(post.getEvent().getEvent_Id());
            postNotificationId.setUserId(user.getUserId());
            
            EventPostNotification eventPostEntity = entityManager.find(EventPostNotification.class, postNotificationId);
            if(eventPostEntity == null)
                throw new NoResultException();
            return eventPostEntity;
        }
        else
            throw new NoResultException("No Event Post Notifications found");
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<TopicFollowNotificationJSON> getTopicFollowNotificationInfo(TopicFollowNotificationJSON topicFollowNotification) throws NoResultException{
        if(topicFollowNotification.getUser() != null || topicFollowNotification.getTopic() != null){
            Set<TopicFollowNotification> topicFollowers = null;
            if(topicFollowNotification.getUser() == null && topicFollowNotification.getTopic() != null){
                Topic topic = topicOps.getTopicEntity(topicFollowNotification.getTopic());
                topicFollowers = this.entityManager.createNamedQuery("TopicFollowNotification.findByTopicId",TopicFollowNotification.class)
                            .setParameter(":topicId", topic.getTopic_Id())
                            .setParameter("seen", topicFollowNotification.getSeen())
                            .getResultList().stream().collect(Collectors.toSet());
            }
            else if(topicFollowNotification.getUser()!= null && topicFollowNotification.getTopic() == null){
                User user = userOps.getUserEntity(topicFollowNotification.getUser());
                topicFollowers = this.entityManager.createNamedQuery("TopicFollowNotification.findByUserId",TopicFollowNotification.class)
                                 .setParameter("userId", user.getUserId())
                                 .setParameter("seen", topicFollowNotification.getSeen())
                                 .getResultList().stream().collect(Collectors.toSet());
            }
            else{
                Topic topic = topicOps.getTopicEntity(topicFollowNotification.getTopic());
                User user = userOps.getUserEntity(topicFollowNotification.getUser()); 
                TopicFollowNotificationId topicFollowNotificationId = new TopicFollowNotificationId();
                topicFollowNotificationId.setTopic_Id(topic.getTopic_Id());
                topicFollowNotificationId.setUserId(user.getUserId());
                topicFollowers = Collections.singleton (this.entityManager.find(TopicFollowNotification.class, topicFollowNotificationId));
            }
           return topicFollowers.stream().map(topicFollower ->{
            return new TopicFollowNotificationJSON(0, new TopicJSON(topicFollower.getTopic_Id(), null, null, null, null, null),
                        null,
                        new UserJSON(topicFollower.getUser().getUsername()),false);
           }).collect(Collectors.toSet());
        }
        else 
            throw new NoResultException("No topic Follows found for User");
    }
/*     
    public Set<FollowNotificationJSON> getFollowNotificationInfo(FollowNotificationJSON userFollowNotification) throws NoResultException{
        Set<String> userfollowers = getUserFollowers(userFollowNotification);
        return userfollowers.stream().map(candidate ->{
            return new FollowNotificationJSON(0, null, new UserJSON(candidate), userFollowNotification.getUser());
        }).collect(Collectors.toSet());
    }   
*/
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public  Set<UserFollowNotificationJSON> getUserFollowNotificationInfo(UserFollowNotificationJSON userFollowNotification) throws NoResultException{
        if(userFollowNotification.getUser() != null || userFollowNotification.getFollower() != null){
            Set<FollowNotification> userFollowers = null;
            if(userFollowNotification.getUser() != null && userFollowNotification.getFollower() == null){
                User user = userOps.getUserEntity(userFollowNotification.getUser());
                userFollowers = this.entityManager.createNamedQuery("FollowNotification.findByUserId",FollowNotification.class)
                            .setParameter("userId", user.getUserId())
                            .setParameter("seen", userFollowNotification.getSeen())
                            .getResultList().stream().collect(Collectors.toSet());
            }
            else if(userFollowNotification.getUser()== null && userFollowNotification.getFollower() != null){
                User user = userOps.getUserEntity(userFollowNotification.getUser());
                userFollowers = this.entityManager.createNamedQuery("FollowNotification.findByFollowerId",FollowNotification.class)
                                 .setParameter("followerId", user.getUserId())
                                 .setParameter("seen", userFollowNotification.getSeen())
                                 .getResultList().stream().collect(Collectors.toSet());
            }
            else{
                User follower = userOps.getUserEntity(userFollowNotification.getFollower());
                User user = userOps.getUserEntity(userFollowNotification.getUser()); 
                FollowNotificationId userFollowNotificationId = new FollowNotificationId();
                userFollowNotificationId.setFollower_Id(follower.getUserId());
                userFollowNotificationId.setUserId(user.getUserId());
                userFollowers = Collections.singleton (this.entityManager.find(FollowNotification.class, userFollowNotificationId));
            }

            return userFollowers.stream().map(topicFollower ->{
             return new UserFollowNotificationJSON(0, new UserJSON(topicFollower.getUser().getUsername()),
                         null, 
                         new UserJSON(topicFollower.getFollower().getUsername()), userFollowNotification.getSeen());
            }).collect(Collectors.toSet());
         }
         else 
             throw new NoResultException("No User Follows found for User");
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<EventNotificationJSON> getEventNotificationInfo(EventNotificationJSON eventNotification) throws NoResultException{
        if(eventNotification.getUser() == null && eventNotification.getEvent() != null){
            Stream<EventNotification> query = entityManager.createNamedQuery("EventNotification.findByEventId", EventNotification.class)
                                        .setParameter("eventId", eventNotification.getEvent().getEventID())
                                        .setParameter("seen", eventNotification.getSeen())
                                        .getResultStream();
                
                return  query.map(eventNotInst ->{
                                return new EventNotificationJSON(0, LocalDateTime.ofInstant(eventNotInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                                            new EventJSON(eventNotInst.getEvent_Id(), null, null,null, null, null, null, null, null, null, null, null, null, null,null),
                                                                new UserJSON(eventNotInst.getUserId(), null), eventNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if(eventNotification.getEvent() == null && eventNotification.getUser() != null){
                Stream<EventNotification> query = entityManager.createNamedQuery("EventNotification.findByUserId", EventNotification.class)
                .setParameter("userId", eventNotification.getUser().getUserId())
                .setParameter("seen", eventNotification.getSeen())
                .getResultStream();

                return  query.map(eventNotInst ->{
                                return new EventNotificationJSON(0, LocalDateTime.ofInstant(eventNotInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                    new EventJSON(eventNotInst.getEvent_Id(), null, null, null, null,null ,null, null, null, null, null, null, null, null,null),
                                        new UserJSON(eventNotInst.getUserId(), null), eventNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else{
            EventNotification eventNotEntity = getEventNotificationEntity(eventNotification);
                    return Collections.singleton(new EventNotificationJSON(0, LocalDateTime.ofInstant(eventNotEntity.getCreated().toInstant(), ZoneId.systemDefault()), 
                        new EventJSON(eventNotEntity.getEvent_Id(), null, null, null, null,null , null, null, null, null, null, null, null, null, null),
                            new UserJSON(eventNotEntity.getUserId(), null), eventNotEntity.getSeen()));
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<EventPostNotificationJSON> getEventPostNotificationInfo(EventPostNotificationJSON eventPostNotification) throws NoResultException{
        if(eventPostNotification != null && (eventPostNotification.getUser()== null && eventPostNotification.getPost() == null && eventPostNotification.getEventPost().getEvent() != null)){
                Stream<EventPostNotification> query = entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByEventId", EventPostNotification.class)
                                        .setParameter("eventId", eventPostNotification.getEventPost().getEvent().getEventID())
                                        .setParameter("seen", eventPostNotification.getSeen())
                                        .getResultStream();             
                return  query.map(eventPostNotInst ->{
                                return new EventPostNotificationJSON(0,  
                                                                new EventPostJSON(eventPostNotInst.getPost_Id(), null, null, 0, 0, null, null, null ,null),
                                                                 LocalDateTime.ofInstant(eventPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),
                                                                new UserJSON(eventPostNotInst.getUserId(), null), eventPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if(eventPostNotification.getPost() == null && eventPostNotification.getEventPost().getEvent() == null && eventPostNotification.getUser() != null){
                Stream<EventPostNotification> query = entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByUserId", EventPostNotification.class)
                .setParameter("userId", eventPostNotification.getUser().getUserId())
                .setParameter("seen", eventPostNotification.getSeen())
                .getResultStream();

                return  query.map(eventPostNotInst ->{
                                return new EventPostNotificationJSON(0,  
                                    new EventPostJSON(eventPostNotInst.getPost_Id(), null,null, 0, 0, null, 
                                           new EventJSON(eventPostNotInst.getEvent_Id(), null,null,null,null,null,null,null,null,null,null,null,null, null, null), null, null),
                                            LocalDateTime.ofInstant(eventPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),    
                                    new UserJSON(eventPostNotInst.getUserId(), null),eventPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        
        else if(eventPostNotification.getPost() != null && eventPostNotification.getEventPost().getEvent() == null && eventPostNotification.getUser() == null){
                Stream<EventPostNotification> query = entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByPostId", EventPostNotification.class)
                .setParameter("postId", eventPostNotification.getPost().getPostId())
                .setParameter("seen", eventPostNotification.getSeen())
                .getResultStream();

                return  query.map(eventPostNotInst ->{
                                return new EventPostNotificationJSON(0,  
                                    new EventPostJSON(eventPostNotInst.getPost_Id(), null,null, 0, 0, null, 
                                           new EventJSON(eventPostNotInst.getEvent_Id(), null,null,null,null,null,null,null,null,null,null,null,null, null, null),null, null),
                                             LocalDateTime.ofInstant(eventPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),    
                                    new UserJSON(eventPostNotInst.getUserId(), null), eventPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if (eventPostNotification.getPost() != null && eventPostNotification.getUser() != null && eventPostNotification.getEventPost().getEvent() == null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByPostId&UserId", EventPostNotification.class)
                .setParameter("postId", eventPostNotification.getEventPost().getPostId())
                .setParameter("userId", eventPostNotification.getUser().getUserId())
                .setParameter("seen", eventPostNotification.getSeen())
                .getResultStream();

                return query.map(eventPostNontInst ->{
                        return new EventPostNotificationJSON(0, 
                            new EventPostJSON(eventPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null,null), 
                             LocalDateTime.ofInstant(eventPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(eventPostNontInst.getUserId(), null), eventPostNontInst.getSeen());
                }).collect(Collectors.toSet());
        }
          else if (eventPostNotification.getPost() != null  && eventPostNotification.getUser() == null && eventPostNotification.getEventPost().getEvent() != null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByPostId&EventId", EventPostNotification.class)
                .setParameter("postId", eventPostNotification.getEventPost().getPostId())
                .setParameter("eventId", eventPostNotification.getEventPost().getEvent().getEventID())
                .setParameter("seen", eventPostNotification.getSeen())
                .getResultStream();

                return query.map(eventPostNontInst ->{
                        return new EventPostNotificationJSON(0, 
                            new EventPostJSON(eventPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null, null), 
                             LocalDateTime.ofInstant(eventPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(eventPostNontInst.getUserId(), null), eventPostNontInst.getSeen());
                }).collect(Collectors.toSet());
        }
        else if (eventPostNotification.getPost() == null  && eventPostNotification.getUser() != null && eventPostNotification.getEventPost().getEvent() != null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("EventPostNotification.getEventPostNotificationByUserId&EventId", EventPostNotification.class)
                .setParameter("eventId", eventPostNotification.getEventPost().getEvent().getEventID())
                .setParameter("userId", eventPostNotification.getUser().getUserId())
                .setParameter("seen", eventPostNotification.getSeen())
                .getResultStream();

                return query.map(eventPostNontInst ->{
                        return new EventPostNotificationJSON(0, 
                            new EventPostJSON(eventPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null, null), 
                             LocalDateTime.ofInstant(eventPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(eventPostNontInst.getUserId(), null), eventPostNontInst.getSeen());
                }).collect(Collectors.toSet());
        }
        else{
            EventPostNotification eventPostNot = getEventPostNotificationEntity(eventPostNotification);
                return Collections.singleton(new EventPostNotificationJSON(0, 
                    new EventPostJSON(eventPostNot.getPost_Id(), null, null, 0, 0,  null, null, null, null),
                         LocalDateTime.ofInstant(eventPostNot.getCreated().toInstant(), ZoneId.systemDefault()),
                        new UserJSON(eventPostNot.getUserId(), null), eventPostNot.getSeen()));
        }
        
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<TopicPostNotificationJSON> getTopicPostNotificationInfo(TopicPostNotificationJSON topicPostNotification){
        if(topicPostNotification.getTopicPost().getTopic() != null && topicPostNotification.getUser()== null && topicPostNotification.getPost()== null){
                Stream<TopicPostNotification> query = entityManager.createNamedQuery("TopicPostNotification.getTopicNotificationByTopicId", TopicPostNotification.class)
                                        .setParameter("topicId", topicPostNotification.getTopicPost().getTopic().getTopicId())
                                        .setParameter("seen", topicPostNotification.getSeen())
                                        .getResultStream();
                
                return  query.map(topicPostNotInst ->{
                                return new TopicPostNotificationJSON(0,  
                                                                new TopicPostJSON(topicPostNotInst.getPost_Id(), null,null, 0, 0, null, null, null,null),
                                                                 LocalDateTime.ofInstant(topicPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),
                                                                new UserJSON(topicPostNotInst.getUserId(), null), topicPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if(topicPostNotification.getUser() != null && topicPostNotification.getTopicPost().getTopic() == null && topicPostNotification.getPost() == null){
                Stream<TopicPostNotification> query = entityManager.createNamedQuery("TopicPostNotification.getTopicNotificationByUserId", TopicPostNotification.class)
                .setParameter("userId", topicPostNotification.getUser().getUserId())
                .setParameter("seen", topicPostNotification.getSeen())
                .getResultStream();

                return  query.map(topicPostNotInst ->{
                                return new TopicPostNotificationJSON(0,  
                                    new TopicPostJSON(topicPostNotInst.getPost_Id(), null,null, 0, 0, null, null, null, null),
                                    LocalDateTime.ofInstant(topicPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),    
                                    new UserJSON(topicPostNotInst.getUserId(), null), topicPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        
        else if(topicPostNotification.getUser() == null && topicPostNotification.getTopicPost().getTopic() == null && topicPostNotification.getPost() != null){
                Stream<TopicPostNotification> query = entityManager.createNamedQuery("TopicPostNotification.getTopicNotificationByPostId", TopicPostNotification.class)
                .setParameter("postId", topicPostNotification.getPost().getPostId())
                .setParameter("seen", topicPostNotification.getSeen())
                .getResultStream();

                return  query.map(topicPostNotInst ->{
                                return new TopicPostNotificationJSON(0,  
                                    new TopicPostJSON(topicPostNotInst.getPost_Id(), null, null, 0, 0, null, null, null, null),
                                    LocalDateTime.ofInstant(topicPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),    
                                    new UserJSON(topicPostNotInst.getUserId(), null), topicPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if (topicPostNotification.getPost() == null && topicPostNotification.getUser() != null && topicPostNotification.getTopicPost().getTopic() != null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("TopicPostNotification.getTopicPostNotificationByPostId&UserId", EventPostNotification.class)
                .setParameter("postId", topicPostNotification.getTopicPost().getPostId())
                .setParameter("userId", topicPostNotification.getUser().getUserId())
                .setParameter("seen", topicPostNotification.getSeen())
                .getResultStream();
                return query.map(topicPostNontInst ->{
                        return new TopicPostNotificationJSON(0, 
                            new TopicPostJSON(topicPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null, null), 
                            LocalDateTime.ofInstant(topicPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(topicPostNontInst.getUserId(), null), topicPostNotification.getSeen());
                }).collect(Collectors.toSet());
        }else if (topicPostNotification.getPost() == null && topicPostNotification.getUser() != null && topicPostNotification.getTopicPost().getTopic() != null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("TopicPostNotification.getTopicPostNotificationByUserId&TopicId", EventPostNotification.class)
                .setParameter("userId", topicPostNotification.getUser().getUserId())
                .setParameter("seen", topicPostNotification.getSeen())
                .setParameter("topicId", topicPostNotification.getTopicPost().getTopic().getTopicId())
                .getResultStream();
                return query.map(topicPostNontInst ->{
                        return new TopicPostNotificationJSON(0, 
                            new TopicPostJSON(topicPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null, null), 
                            LocalDateTime.ofInstant(topicPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(topicPostNontInst.getUserId(), null), topicPostNontInst.getSeen());
                }).collect(Collectors.toSet());
        }else if (topicPostNotification.getPost() != null && topicPostNotification.getUser() == null && topicPostNotification.getTopicPost().getTopic() != null){
            Stream<EventPostNotification> query =entityManager.createNamedQuery("TopicPostNotification.getTopicPostNotificationByTopicId&PostId", EventPostNotification.class)
                .setParameter("postId", topicPostNotification.getTopicPost().getPostId())
                .setParameter("topicId", topicPostNotification.getTopicPost().getTopic().getTopicId())
                .setParameter("seen", topicPostNotification.getSeen())
                .getResultStream();
                return query.map(topicPostNontInst ->{
                        return new TopicPostNotificationJSON(0, 
                            new TopicPostJSON(topicPostNontInst.getPost_Id(), null, null, 0, 0, null, null, null, null), 
                             LocalDateTime.ofInstant(topicPostNontInst.getCreated().toInstant(), ZoneId.systemDefault()), 
                                new UserJSON(topicPostNontInst.getUserId(), null), topicPostNontInst.getSeen());
                }).collect(Collectors.toSet());
        }

        else{
            TopicPostNotification topicPostNot = getTopicPostNotificationEntity(topicPostNotification);
                return Collections.singleton(new TopicPostNotificationJSON(0, 
                    new TopicPostJSON(topicPostNot.getPost_Id(), null, null, 0, 0,  null, null, null, null),
                         LocalDateTime.ofInstant(topicPostNot.getCreated().toInstant(), ZoneId.systemDefault()),
                        new UserJSON(topicPostNot.getUserId(), null), topicPostNot.getSeen()));
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<PostNotificationJSON> getPostNotificationInfo(PostNotificationJSON postNotification){
        if(postNotification != null && (postNotification.getUser()!= null && postNotification.getPost()!= null)){
                Stream<PostNotification> query = entityManager.createNamedQuery("PostNotification.getByUserId", PostNotification.class)
                                        .setParameter("userId", postNotification.getPost().getUser().getUserId())
                                        .setParameter("seen", postNotification.getSeen())
                                        .getResultStream();
                
                return  query.map(topicPostNotInst ->{
                                return new PostNotificationJSON(0,  
                                new PostJSON(topicPostNotInst.getPost_Id(), null, null, 0, 0, null, null, null),
                                                                 LocalDateTime.ofInstant(topicPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),
                                                                new UserJSON(topicPostNotInst.getUserId(), null), topicPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else if(postNotification.getPost() != null && postNotification.getUser() == null){
                Stream<PostNotification> query = entityManager.createNamedQuery("PostNotification.getByPostId", PostNotification.class)
                .setParameter("postId", postNotification.getPost().getPostId())
                .setParameter("seen", postNotification.getSeen())
                .getResultStream();

                return  query.map(topicPostNotInst ->{
                                return new PostNotificationJSON(0,  
                                    new PostJSON(topicPostNotInst.getPost_Id(), null, null, 0, 0, null, null, null),
                                     LocalDateTime.ofInstant(topicPostNotInst.getCreated().toInstant(), ZoneId.systemDefault()),    
                                    new UserJSON(topicPostNotInst.getUserId(), null), topicPostNotInst.getSeen());
                            }).collect(Collectors.toSet());
        }
        else{
            PostNotification topicPostNot = getPostNotificationEntity(postNotification);
                return Collections.singleton(new PostNotificationJSON(0, 
                    new PostJSON(topicPostNot.getPost_Id(), null, null, 0, 0, null, null, null),
                         LocalDateTime.ofInstant(topicPostNot.getCreated().toInstant(), ZoneId.systemDefault()),
                        new UserJSON(topicPostNot.getUserId(), null), topicPostNot.getSeen()));
            }
    }
}
