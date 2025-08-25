package com.memefest.Services.Impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.Event;
import com.memefest.DataAccess.EventPost;
import com.memefest.DataAccess.EventPostId;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.PostCategory;
import com.memefest.DataAccess.PostCategoryId;
import com.memefest.DataAccess.PostReply;
import com.memefest.DataAccess.PostReplyId;
import com.memefest.DataAccess.Repost;
import com.memefest.DataAccess.RepostId;
import com.memefest.DataAccess.Topic;
import com.memefest.DataAccess.TopicPost;
import com.memefest.DataAccess.TopicPostId;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventPostJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.RepostJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.CategoryOperations;
import com.memefest.Services.EventOperations;
import com.memefest.Services.FeedsOperations;
import com.memefest.Services.NotificationOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.TopicOperations;
import com.memefest.Services.UserOperations;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.ejb.TransactionRolledbackLocalException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.Query;

@Stateless(name = "PostService")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PostService implements PostOperations{
    
    @PersistenceContext(unitName = "memeFest", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @EJB
    private UserOperations userOperations;

    @EJB
    private CategoryOperations catOps;

    @EJB
    private FeedsOperations feedEndPointService;

    @EJB
    private EventOperations eventOperations;

    @EJB
    private TopicOperations topicOperations;

    @EJB
    private NotificationOperations notOps;

    /* */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //add custom exception to show object was not created
    private void createPost(PostJSON post) {
        User user = null;
        user = this.userOperations.getUserEntity(post.getUser());
        Post newPost = new Post();
        newPost.setComment(post.getComment());
        newPost.setCreated(Date.from(post.getCreated().atZone(ZoneId.systemDefault()).toInstant()));
        newPost.setUser(user);
        newPost.setUpvotes(post.getUpvotes());
        newPost.setDownvotes(post.getDownvotes());
        this.entityManager.persist(newPost);
        if(post.getCategories() != null)
            post.getCategories().stream().forEach(category -> {
                    catOps.editCategory(category);
                    Category categoryEntity = catOps.getCategoryEntity(category);
                    createPostCategory(newPost, categoryEntity);
        });
    }

    private void editPostCategory(PostJSON post){
        try{
            Post postEntity = getPostEntity(post);
            if(post.getCategories() != null)
                post.getCategories().stream().forEach(category -> {
                        catOps.editCategory(category);
                        Category categoryEntity = catOps.getCategoryEntity(category);
                        createPostCategory(postEntity, categoryEntity);
            });
        }
        catch(NoResultException ex){
            return;
        }
    }

    public void removePostCategory(PostJSON post){
        Post postEntity = getPostEntity(post);
        if(post.getCategories() != null)        
            post.getCategories().stream().forEach(category -> {     
                catOps.editCategory(category);
                    Category categoryEntity = catOps.getCategoryEntity(category);
                        removePostCategory(postEntity, categoryEntity);
        });
    }

    private void createPostCategory(Post post, Category category){
        PostCategory postCat = new PostCategory();
        postCat.setCat_Id(category.getCat_Id());
        postCat.setPost_Id(post.getPost_Id());
        entityManager.persist(postCat);
    }

    

    private Set<CategoryJSON> getPostCategories(PostJSON post){
        if(post== null || post.getCategories() == null)
            return null;
        List<PostCategory> categories = this.entityManager.createNamedQuery("PostCategory.findByPostId", PostCategory.class)
                                            .setParameter(1, post.getPostId()).getResultList();
        return categories.stream().map(catEntity -> {
            return  new CategoryJSON(catEntity.getCategory().getCat_Id(), catEntity.getCategory().getCat_Name(), null, null, null);
        }).collect(Collectors.toSet());        
    }

    private void removePostCategory(Post postEntity, Category catEntity){
            PostCategoryId postCat = new PostCategoryId();
            postCat.setCat_Id(catEntity.getCat_Id());
            postCat.setPost_Id(postEntity.getPost_Id());
            entityManager.remove(entityManager.find(PostCategory.class,postCat));
    }
    /* */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void createEventPost(Post post, Event event){   
            EventPost  eventPostEntity = new EventPost();
            eventPostEntity.setEvent(event);
            eventPostEntity.setPost(post);
            entityManager.persist(eventPostEntity);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editEventPost(EventPostJSON eventPost){
        if(eventPost== null || eventPost.getEvent() == null)
            return;
        try{
            getEventPostEntity(eventPost);
        }
        catch(NoResultException | EJBException e){
            Event event = null;
            Post postEntity  = null;
            
            try{
                event = eventOperations.getEventEntity(eventPost.getEvent());
            }
            catch(NoResultException | EJBException ec){
                eventOperations.editEvent(eventPost.getEvent());
                event = eventOperations.getEventEntity(eventPost.getEvent());
            }
            try{
                postEntity = getPostEntity((PostJSON)eventPost);    
            }catch(NoResultException | EJBException ex) {
                //find if user is an admin then execute createtopic otherwise exit method 
                createPost((PostJSON)eventPost);
                postEntity = getPostEntity(eventPost);
            }
            createEventPost (postEntity, event);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeEventPost(EventPostJSON eventPost){
        if(eventPost.isCancelled() == false)
            return;
        EventPost eventPostEntity = null;
        try{
            eventPostEntity = getEventPostEntity(eventPost);
        } catch(NoResultException | EJBException e){
            return;
        }
        this.entityManager.remove(eventPostEntity);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //add custom exception to show object was not created
    private void createRepost(Post post, User user)throws NoResultException{
        if(post == null || user == null)
            throw new NoResultException(); 
        Repost repostEntity = new Repost();
        repostEntity.setUserId(user.getUserId());
        repostEntity.setPost(post);
        this.entityManager.persist(repostEntity);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Repost getRepostEntity(Post post, User owner) throws NoResultException, 
                                                        TransactionRolledbackLocalException, 
                                                            EJBTransactionRolledbackException{
        if(owner == null || post == null)
            throw new NoResultException();
        RepostId repostId = new RepostId();
        repostId.setPost_Id(post.getPost_Id());
        repostId.setUserId(owner.getUserId());
        Repost repost = entityManager.find(Repost.class, repostId);
        if(repost == null)
            throw new NoResultException("No Repost found ");
        return repost; 
    }
/* 
    public Set<RepostJSON> searchRepost(RepostJSON repost){
        //if(repos)
    }
*/
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public RepostJSON getRepostInfo(RepostJSON repost){
        Post postEntity = null;
        User userEntity = null;
        postEntity = getPostEntity(repost);
        userEntity = userOperations.getUserEntity(repost.getOwner());
        PostJSON post = getPostInfo(new PostJSON(postEntity.getPost_Id(), null, null, 0, 0,null, null,null));
        UserJSON owner = userOperations.getUserInfo(new UserJSON(userEntity.getUserId(), userEntity.getUsername()));
        getRepostEntity(postEntity, userEntity);

        RepostJSON repostInfo = new RepostJSON(post.getPostId(),post.getComment(),
                                      post.getCreated(),
                                        post.getUpvotes(), post.getDownvotes(), post.getUser(), owner, post.getCategories(), null);
        return repostInfo;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EventPostJSON getEventPostInfo(EventPostJSON eventPost) throws 
                                                                NoResultException, 
                                                                    EJBTransactionRolledbackException{
        EventPost eventPostEntity = getEventPostEntity(eventPost);
        Post postEntity = eventPostEntity.getPost();
        Set<CategoryJSON> categories = getPostCategories(new PostJSON(postEntity.getPost_Id(), null, null, 0, 0, null, null, null));
        eventPost.setComment(postEntity.getComment());
        eventPost.setCreated(LocalDateTime.ofInstant(postEntity.getCreated().toInstant(), ZoneId.systemDefault()));
        eventPost.setDownvotes(postEntity.getDownvotes());
        eventPost.setUpvotes(postEntity.getUpvotes());
        eventPost.setPostId(eventPostEntity.getPost_Id());
        eventPost.setUser(new UserJSON(postEntity.getUser().getUserId(), postEntity.getUser().getUsername()));
        eventPost.setEvent(new EventJSON(eventPostEntity.getEvent().getEvent_Id(), eventPostEntity.getEvent().getEvent_Title(), null, null, null, null, null, null,null, null, null, null, null, null,null));
        eventPost.setCategories(categories);
        return eventPost;
    }

    public Set<EventPostJSON> getEventPostsByEvent(EventJSON event) throws EJBException{
        Event eventEntity = eventOperations.getEventEntity(event);
        return eventEntity.getPosts().stream().map(candidate ->{
                        return getEventPostInfo(new EventPostJSON(candidate.getPost_Id(), null, null, 
                                                        0, 0, null, new EventJSON(candidate.getEvent().getEvent_Id(), null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                                                        , null, null));
                    }).collect(Collectors.toSet());   
    }    

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editRepost(RepostJSON post){
        if(post == null|| post.getOwner() == null)
            return;
        Post postEntity = null;
        try{
            postEntity = getPostEntity((PostJSON)post);
        }
        catch(NoResultException | EJBException ex){
            editPost(post);
        }
        postEntity = getPostEntity(post);
        User user = userOperations.getUserEntity(post.getOwner());
        if(postEntity == null)
            throw new NoResultException("OOOPS");
        try{
            getRepostEntity(postEntity, user);
        }
        catch(NoResultException | EJBException ex){
            createRepost(postEntity, user);
            //editRepost(post);
            return;
        }
    }

    public Set<RepostJSON> getRepostsByUser(UserJSON user)throws NoResultException{
        User userEntity = userOperations.getUserEntity(user);
        return entityManager.createNamedQuery("Repost.findByUserId", Repost.class)
                    .setParameter("userId", userEntity.getUserId())
                        .getResultStream().map(candidate ->{
                            return getRepostInfo(new RepostJSON(candidate.getPost().getPost_Id(), null, null, 0, 0,
                                             new UserJSON(candidate.getUser().getUserId(),
                                              null),null , null, null));
                }).collect(Collectors.toSet());
    }


    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removeRepost(RepostJSON post){
        if(post.isCanceled() ==  false)
            return;
        try{
            Post postEntity = getPostEntity(post);
            User userEntity = userOperations.getUserEntity(post.getOwner());
            Repost repostEntity = getRepostEntity(postEntity, userEntity);
            this.entityManager.remove(repostEntity);
        }
        catch(NoResultException | EJBException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //add custom exception to show object was not created
    private void createPostReplies(PostWithReplyJSON post) {
        Post parent = null; 
        try{
            parent = getPostEntity((PostJSON)post);
        }
        catch(NoResultException | EJBException ex){
            createPost((PostJSON)post);
            createPostReplies(post);
            return;
        }
        if (post.getPosts() == null)
            return;
        for(PostJSON postInst : post.getPosts()) {
            Post child = null;
            try{
                child = getPostEntity(postInst);
                try{
                    getPostReplyEntity(postInst, (PostJSON) post);
                }
                catch(NoResultException | EJBException ex){
                    createPostReply(child, parent);
                }
            }
            catch(NoResultException |EJBException ex){
                createPost(postInst);
                child = getPostEntity(postInst);
                createPostReply(child, parent);   
            }
            
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private PostReply getPostReplyEntity(PostJSON post, PostJSON parent) throws NoResultException{
        PostReplyId postRepId = new PostReplyId();
        postRepId.setPost_Id(post.getPostId());
        postRepId.setPost_Info(parent.getPostId());

        PostReply result = null;
        result = entityManager.find(PostReply.class, postRepId);
        if(result != null)
            return result;
        throw new NoResultException();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //@TransactionAttribute(TransactionAttributeType.MANDATORY)
    private void createPostReply(Post post, Post parent){
            if (post == null || parent == null)
                return;
         //editPost(post);
            try{
                PostReplyId postReplyId = new PostReplyId();
                postReplyId.setPost_Id(post.getPost_Id());
                postReplyId.setPost_Info(parent.getPost_Id());
                PostReply result =this.entityManager.find(PostReply.class, postReplyId);
                if(result== null){
                    PostReply postReply = new PostReply();
                    postReply.setParent(parent);
                    postReply.setPost(post);
                    this.entityManager.persist(postReply);
                }
            }
            catch(NoResultException ex){
                PostReply postReply = new PostReply();
                postReply.setParent(parent);
                postReply.setPost(post);
                this.entityManager.persist(postReply);
            }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editPostReplies(PostWithReplyJSON post){
        try{
            getPostEntity(post);
        }
        catch(NoResultException | EJBException ex){
            createPost(post);
            editPostReplies(post);
            return;
        }
        createPostReplies(post);
            //editPostReplies(post);
            //return;
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removePostReplies(PostWithReplyJSON postWithReply) {
        if(postWithReply.getPosts() == null)
            return;
        for (PostJSON post : postWithReply.getPosts()) {
            removePost(post); 
        }

    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editPost(PostJSON post) {
        try{
            Post postEntity = getPostEntity(post);
            this.userOperations.getUserEntity(post.getUser()); 
            if (post.getComment() != null)
                postEntity.setComment(post.getComment());  
            if (post.getCreated() != null)
                postEntity.setCreated(Date.from(post.getCreated().atZone(ZoneId.systemDefault()).toInstant())); 
            if (postEntity.getDownvotes() != 0)
                postEntity.setDownvotes(post.getDownvotes()); 
            if (postEntity.getUpvotes() != 0)
                postEntity.setUpvotes(post.getUpvotes()); 
            this.entityManager.merge(postEntity);
            postEntity = getPostEntity(post);
            editPostCategory(post);
        }catch(NoResultException ex){
            createPost(post);
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editPostWithReply(PostWithReplyJSON postWithReply){       
        try{
            getPostEntity((PostJSON)postWithReply);
            editPost(postWithReply);
            //editPost(postWithReply);
        }
        catch(NoResultException |EJBException ex){
            createPost((PostJSON)postWithReply);
            //editPostWithReply(postWithReply);
        }
        //createPostReplies(postWithReply);
        editPostReplies(postWithReply);
        //removePostReplies(postWithReply);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removePostWithReply(PostWithReplyJSON postWithReply){
        try{
            Post postEntity = getPostEntity((PostJSON)postWithReply);
            if (postEntity != null && postEntity.getPost_Id()!= 0) {
                this.entityManager.remove(postEntity);
            }
        }
        catch(NoResultException | EJBException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //add custom exception to show object was not created
    private void createTopicPost(Topic topic, Post post){
            TopicPost  topicPostEntity = new TopicPost();
            topicPostEntity.setTopic(topic);
            topicPostEntity.setPost(post);
            entityManager.persist(topicPostEntity);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TopicPost getTopicPostEntity(TopicPostJSON post) throws NoResultException,
                                                                        EJBTransactionRolledbackException,
                                                                            TransactionRolledbackLocalException, 
                                                                                EJBException{
        if(post == null){
            throw new NoResultException("No result for TopicPost");
        } 
        Topic topicEntity = topicOperations.getTopicEntity(post.getTopic());
        Post postEntity = getPostEntity((PostJSON)post);
        TopicPostId topicPostId = new TopicPostId();
        topicPostId.setTopic_Id(topicEntity.getTopic_Id());
        topicPostId.setPost_Id(postEntity.getPost_Id());
        TopicPost topicPost = this.entityManager.find(TopicPost.class, topicPostId);
        if(topicPost == null)
            throw new NoResultException(); 
        return topicPost;    
    }



    //@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TopicPostJSON getTopicPostInfo(TopicPostJSON topicPost) throws NoResultException,
                                                                            TransactionRolledbackLocalException,
                                                                                EJBTransactionRolledbackException,
                                                                                    EJBException{
        TopicPost topicPostEntity = getTopicPostEntity(topicPost);
        Post postEntity = topicPostEntity.getPost();
        //Set<CategoryJSON> categories = getTopicCategories(new TopicJSON(topicPostEntity.getTopic_Id(), null, null, null, null, null));
        topicPost.setComment(postEntity.getComment());
        topicPost.setCreated(LocalDateTime.ofInstant(postEntity.getCreated().toInstant(), ZoneId.systemDefault()));
        topicPost.setDownvotes(postEntity.getDownvotes());
        topicPost.setUpvotes(postEntity.getUpvotes());
        topicPost.setPostId(topicPostEntity.getPost_Id());
        topicPost.setUser(new UserJSON(postEntity.getUser().getUserId(), postEntity.getUser().getUsername()));
        topicPost.setTopic(topicOperations.getTopicInfo(new TopicJSON(topicPostEntity.getTopic_Id(), null, null, null, null, null)));
        //topicPost.setCategories(categories);
        return new TopicPostJSON(topicPostEntity.getPost_Id(), 
                            postEntity.getComment(), 
                            LocalDateTime.ofInstant(postEntity.getCreated().toInstant(), ZoneId.systemDefault()), 
                            postEntity.getUpvotes(), 
                            postEntity.getDownvotes(),
                            new UserJSON(postEntity.getUserId(), null), 
                            new TopicJSON(topicPostEntity.getTopic().getTopic_Id(), null, null, null, null, null), null, null);
    }

    public Set<TopicPostJSON> getTopicPostsByTopic(TopicJSON topic) throws EJBException{
        Topic topicEntity = topicOperations.getTopicEntity(topic);
        return topicEntity.getPosts().stream().map(candidate ->{
                        return getTopicPostInfo(new TopicPostJSON(candidate.getPost_Id(), null, null, 
                                                        0, 0, null, new TopicJSON(candidate.getTopic_Id(), null, null, null, null, null)
                                                        , null, null));
                    }).collect(Collectors.toSet());   
    }

    //@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PostJSON getPostInfo(PostJSON post) throws NoResultException{
        Post postInfo = getPostEntity(post);
        Set<CategoryJSON> categories = getPostCategories(new PostJSON(postInfo.getPost_Id(), null, null, 0, 0, null, null, null));
        return new PostJSON(postInfo.getPost_Id(), postInfo.getComment(), 
        LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()), 
            postInfo.getUpvotes(), postInfo.getDownvotes(), new UserJSON(postInfo.getUser().getUserId(), postInfo.getUser().getUsername()),categories,null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeTopicPost(TopicPostJSON topicPost){
        if(topicPost.isCancelled() == false)
            return;
        try{
            TopicPost topicPostEntity = getTopicPostEntity(topicPost);
            this.entityManager.remove(topicPostEntity);
        }
        catch(NoResultException | EJBException ex){
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public void editTopicPost(TopicPostJSON topicPost){
        if(topicPost== null || topicPost.getTopic() == null)
            return;
        try{
            getTopicPostEntity(topicPost);
            editPost(topicPost);
        }
        catch(NoResultException | EJBException e){
            Topic topic = null;
            Post postEntity  = null;
            
            try{
                topic = topicOperations.getTopicEntity(topicPost.getTopic());
            }
            catch(NoResultException | EJBException ec){
                topicOperations.createTopic(topicPost.getTopic());
                topic = topicOperations.getTopicEntity(topicPost.getTopic());
            }

            try{
                postEntity = getPostEntity((PostJSON)topicPost);    
            }catch(NoResultException | EJBException ex) {
                //find if user is an admin then execute createtopic otherwise exit method 
                createPost((PostJSON)topicPost);
                postEntity = getPostEntity(topicPost);
            }
            createTopicPost(topic, postEntity);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Post getPostEntity(PostJSON post) throws NoResultException,
                                                        TransactionRolledbackLocalException,
                                                            EJBTransactionRolledbackException,
                                                                EJBException {
        if(post == null)
            throw new NoResultException();
        Post postEntity = null;
        if (post.getPostId() != 0 && post != null){
            postEntity = this.entityManager.find(Post.class, Integer.valueOf(post.getPostId()));
            if (postEntity != null)
                return postEntity;
            else throw new NoResultException();
        }
        else if(post.getComment() != null) {
            Query query = this.entityManager.createNamedQuery("Post.getPostByComment",Post.class);
            query.setParameter(1, post.getComment());
            postEntity =(Post) query.getSingleResult();
             if (postEntity != null)
                return postEntity;
            else
             throw new NoResultException();
        }
        throw new NoResultException();
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void removePost(PostJSON post) {
        if (post.isCancelled()) {
            try{
                Post postEntity = getPostEntity(post);
                if (postEntity != null) {
                    if (post instanceof PostWithReplyJSON)
                        removePostReplies((PostWithReplyJSON)post); 
                    this.entityManager.remove(postEntity);
                }
            }
            catch(NoResultException | EJBException ex){
                return;
            } 
        } 
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<PostReply> getPostReplyEntities(PostWithReplyJSON postWithReply) throws NoResultException, 
                                                                                        TransactionRolledbackLocalException,
                                                                                            EJBTransactionRolledbackException,
                                                                                                EJBException{
        Set<PostReply> result = new HashSet<>();
        List<PostReply> postReplies = this.entityManager.createNamedQuery("PostReplyEntity.getRepliesOfPostId", PostReply.class)
                                        .setParameter("postId", Integer.valueOf(postWithReply.getPostId()))
                                            .getResultList();
        result = (Set<PostReply>)postReplies.stream().collect(Collectors.toSet());
        return result;
    }

    //@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PostWithReplyJSON getPostWithReplyInfo(PostWithReplyJSON postWithReply) throws NoResultException,
                                                                                            TransactionRolledbackLocalException,
                                                                                                EJBException,
                                                                                                    EJBTransactionRolledbackException{
        PostJSON postInfo = getPostInfo(postWithReply);
        postWithReply.setComment(postInfo.getComment());
        postWithReply.setDownvotes(postInfo.getDownvotes());
        postWithReply.setCategories(postInfo.getCategories());
        postWithReply.setCreated(postInfo.getCreated());
        postWithReply.setPostId(postInfo.getPostId());
        postWithReply.setUser(postInfo.getUser());
        postWithReply.setUpvotes(postInfo.getUpvotes());                                                                                                
        Set<PostReply> postReplyEntities = getPostReplyEntities(postWithReply);
        if (postReplyEntities == null)
            return null;
        try{
            Set<PostJSON> postReplys = (Set<PostJSON>)postReplyEntities.stream().map(candidate -> 
                                  new PostJSON(candidate.getPost_Id(), null, null, 0, 0, null, null, null))
                                    .collect(Collectors.toSet());
            postWithReply.setPosts(postReplys);
        }catch (NoResultException e) {
            
        }
        return postWithReply;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EventPost getEventPostEntity(EventPostJSON event) throws NoResultException, 
                                                                EJBException,
                                                                     EJBTransactionRolledbackException, 
                                                                        TransactionRolledbackLocalException{
        if(event == null){
            throw new NoResultException("No result for EventPost");
        } 
        Event eventEntity = eventOperations.getEventEntity(event.getEvent());

        Post postEntity = getPostEntity(event);

        EventPostId eventPostId = new EventPostId();
        eventPostId.setEvent_Id(eventEntity.getEvent_Id());
        eventPostId.setPost_Id(postEntity.getPost_Id());
        EventPost eventPost = this.entityManager.find(EventPost.class,eventPostId);
        if(eventPost == null)
            throw new NoResultException(); 
        return eventPost;
    }

    public Set<PostJSON> searchPost(PostJSON post) throws EJBException, NoResultException{
        List<Post> posts = null;
        if(post == null)
            posts = this.entityManager.createNamedQuery("Post.getAll", Post.class).getResultList();
        else if(post.getComment() != null &&((post.getUser() != null && post.getUser().getUsername() == null
                                                    && post.getUser().getUserId() == 0) || 
                                                        post.getUser() == null))
            posts = this.entityManager.createNamedQuery("Post.searchByComment",Post.class)
                        .setParameter(1, post.getComment())
                        .getResultList();
        else if(post.getUser() != null && post.getUser().getUserId() == 0 && post.getUser().getUsername() != null ){
            UserJSON postedBy = userOperations.getUserInfo(new UserJSON(post.getUser().getUsername()));
            posts = this.entityManager.createNamedQuery("Post.findByUserId",Post.class)
                        .setParameter("userId", postedBy.getUserId())
                        .getResultList();
        }
        else if(post.getUser()!= null && post.getUser().getUserId() != 0 && post.getUser().getUsername() == null && post.getComment() == null)
            posts = this.entityManager.createNamedQuery("Post.findByUserId", Post.class)
                        .setParameter( "userId", post.getUser().getUserId())
                        .getResultList();
        else
            return Collections.singleton(getPostInfo(post));
        if(posts != null)
            return posts.stream().map(postInfo ->{
                Set<CategoryJSON> categories = getPostCategories(new PostJSON(postInfo.getPost_Id(), null, null, 0, 0, null, null, null));
                    return new PostJSON(postInfo.getPost_Id(), postInfo.getComment(), 
                        LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()), 
                    postInfo.getUpvotes(), postInfo.getDownvotes(), new UserJSON(postInfo.getUser().getUserId(), postInfo.getUser().getUsername()),categories,null);
                 }).collect(Collectors.toSet());
        else throw new NoResultException();

    }

}
