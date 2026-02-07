package com.memefest.Services.Impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.Event;
import com.memefest.DataAccess.EventPost;
import com.memefest.DataAccess.EventPostId;
import com.memefest.DataAccess.Interact;
import com.memefest.DataAccess.InteractId;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.PostCategory;
import com.memefest.DataAccess.PostCategoryId;
import com.memefest.DataAccess.PostReply;
import com.memefest.DataAccess.PostReplyId;
import com.memefest.DataAccess.PostTaggedUser;
import com.memefest.DataAccess.Repost;
import com.memefest.DataAccess.RepostId;
import com.memefest.DataAccess.RepostTaggedUser;
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
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.EventOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.TopicOperations;
import com.memefest.Services.UserOperations;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.ejb.TransactionRolledbackLocalException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Stateless(name = "PostService")
//@SessionScoped
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PostService implements PostOperations{
    
    //@TransactionScoped
    //@PersistenceContext(unitName = "PostServicePersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @EJB
    private UserOperations userOperations;

    @EJB
    private CategoryOperations catOps;

    @EJB
    private EventOperations eventOperations;

    @EJB
    private TopicOperations topicOperations;

    @EJB
    private DataSourceOps dataSourceOps;


    @PostConstruct
    //@PostActivate
    public void init(){
        //entityManager = dataSourceOps.getPersistenceContext().getEmf().createEntityManager();
        //this.entityManager = dataSourceOps.getEntityManager("PostServicePersistenceUnit");
        //entityManager.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, "ScoobyDoo24");
        //entityManager.setProperty(PersistenceUnitProperties.JDBC_USER, "Neutron");
        entityManager = dataSourceOps.getEntityManagerFactory().createEntityManager();
    }

    @PreDestroy
    // /@PrePassivate
    public void destroy(){
        entityManager.close();
    }
    

    /* */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    //@Transactional
    //add custom exception to show object was not created
   //@Transactional
    private PostJSON createPost(PostJSON post) {
        entityManager.joinTransaction();
        User user = null;
        user = this.userOperations.getUserEntity(post.getUser());
        //EntityManagerFactory fact = Persistence.createEntityManagerFactory(null, null);
        //entityManager.setProperty("", fact);
        LocalDateTime created = post.getCreated() != null ? post.getCreated() :  LocalDateTime.now();
        Post newPost = new Post();
        newPost.setComment(new String(post.getComment().getBytes(), StandardCharsets.UTF_8));
        newPost.setCreated(Date.from(created.atZone(ZoneId.systemDefault()).toInstant()));
        newPost.setUser(user);
        this.entityManager.persist(newPost);
        if(post.getCategories() != null)
            post.getCategories().stream().forEach(category -> {
                    catOps.editCategory(category);
                    Category categoryEntity = catOps.getCategoryEntity(category);
                    createPostCategory(newPost, categoryEntity);
        }); 
        /* post.setPostId(newPost.getPost_Id());
        */
        entityManager.flush();
        post.setPostId(newPost.getPost_Id());
        return post;
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

    private Set<UserJSON> getPostTaggedUsers(Post post){
        entityManager.merge(post);
        return post.getTaggedUsers().stream().map(candidate -> {
            User user = candidate.getUser();
            UserJSON userEntity =  new UserJSON(user.getUserId(), null, user.getUsername(),
                     0, false, user.getF_name(), user.getL_Name(), null, null, null, null);
            return userEntity;
        }).collect(Collectors.toSet());
    }


    private void editPostTaggedUsers(PostJSON post){
        Set<PostTaggedUser> tags =  getTaggedUsers(post);
        Post postEntity = getPostEntity(post);
        postEntity.setTaggedUsers(tags);
        entityManager.merge(postEntity);
    }

    private Set<PostTaggedUser> getTaggedUsers(PostJSON post) throws NoResultException, EJBException{
        Post postEntity =  getPostEntity(post);
        if(post.getTaggedUsers() == null)
            return Collections.emptySet();
        return post.getTaggedUsers().stream().map(candidate -> {
            try{
                User user = userOperations.getUserEntity(candidate);
                PostTaggedUser result = new PostTaggedUser();
                result.setPost(postEntity);
                result.setTaggedUser(user);
                return result;
            }
            catch(NoResultException ex){
                ex.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());
    }

    private Set<UserJSON> getRepostTaggedUsers(Repost post){
        entityManager.merge(post);
        if(post.getTaggedUsers() == null)
            return Collections.emptySet();
        return post.getTaggedUsers().stream().map(candidate -> {
            User user = candidate.getUser();
            UserJSON userEntity =  new UserJSON(user.getUserId(), null, user.getUsername(),
                     0, false, user.getF_name(), user.getL_Name(), null, null, null, null);
            return userEntity;
        }).collect(Collectors.toSet());
    }


    private void editRepostTaggedUsers(RepostJSON post){
        Set<RepostTaggedUser> tags =  getTaggedUsers(post);
        UserJSON user = post.getOwner();
        User userEntity = userOperations.getUserEntity(user);
        Post postEntity = getPostEntity(post);
        Repost repost = getRepostEntity(postEntity, userEntity);
        repost.setTaggedUsers(tags);
        entityManager.merge(repost);
    }

    private Set<RepostTaggedUser> getTaggedUsers(RepostJSON post) throws NoResultException, EJBException{
        UserJSON user = post.getOwner();
        User userEntity = userOperations.getUserEntity(user);
        Post postEntity = getPostEntity(post);
        Repost repost = getRepostEntity(postEntity, userEntity);
        return repost.getTaggedUsers().stream().map(candidate -> {
            try{
                RepostTaggedUser result = new RepostTaggedUser();
                result.setPost(repost);
                result.setTaggedUser(candidate.getUser());
                return result;
            }
            catch(NoResultException ex){
                ex.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());
    }

    public Set<PostJSON> getAllPosts(){
        Stream<Post> results = entityManager.createNamedQuery("Post.getAll", Post.class).getResultStream();
        return results.map(candidate -> {
            return getPostInfo(new PostJSON(candidate.getPost_Id(),
                null, null, 0, 0,
                 null, null, null,null));
        }).collect(Collectors.toSet());
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

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private void createPostCategory(Post post, Category category){
        entityManager.joinTransaction();
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
            return  new CategoryJSON(catEntity.getCategory().getCat_Id(), 
                catEntity.getCategory().getCat_Name(), null, null, null);
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
        entityManager.joinTransaction();
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
                ;
                event = eventOperations.getEventEntity(eventOperations.editEvent(eventPost.getEvent()));
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
        // /entityManager.joinTransaction();
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
        //entityManager.joinTransaction();
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
        entityManager.joinTransaction();
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
        PostJSON post = getPostInfo(new PostJSON(postEntity.getPost_Id(), null, null,
             0, 0,null, null,null, null));
        UserJSON owner = userOperations.getUserInfo(
            new UserJSON(userEntity.getUserId(), null,
             userEntity.getUsername(), 0, false, userEntity.getF_name(), userEntity.getL_Name(), null,
              null, null, null));
        owner.setAvatar("");
        Repost repostEntity = getRepostEntity(postEntity, userEntity);
        Set<UserJSON> taggedUsers = getRepostTaggedUsers(repostEntity);
        RepostJSON repostInfo = new RepostJSON(post.getPostId(),post.getComment(),
                                      post.getCreated(),
                                        post.getUpvotes(), post.getDownvotes(), post.getUser(), owner, post.getCategories(), null, taggedUsers);
        return repostInfo;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EventPostJSON getEventPostInfo(EventPostJSON eventPost) throws 
                                                                NoResultException, 
                                                                    EJBTransactionRolledbackException{
        //entityManager.joinTransaction();
        EventPost eventPostEntity = getEventPostEntity(eventPost);
        Post postEntity = eventPostEntity.getPost();
        Set<CategoryJSON> categories = getPostCategories(new PostJSON(postEntity.getPost_Id(), null,
                     null, 0, 0, null, null, null, null));
        eventPost.setComment(postEntity.getComment());
        eventPost.setCreated(LocalDateTime.ofInstant(postEntity.getCreated().toInstant(), ZoneId.systemDefault()));
        int downVotes = 0;
        int upvotes = 0;
                
        try {
            Set<Interact> interactions = getInteractionsForPost(eventPost);
            upvotes = interactions.stream().filter(candidate ->{
                                return candidate.getInteract();
                            }).map(candidate -> {
                                return 1;
                            }).reduce(0, (x, y) ->{
                                return x+y;
                            });
            downVotes = interactions.size() - upvotes;
        } catch (NoResultException e) {
                    
        }
        eventPost.setDownvotes(downVotes);
        eventPost.setUpvotes(upvotes);
        eventPost.setPostId(eventPostEntity.getPost_Id());
        eventPost.setUser(new UserJSON(postEntity.getUser().getUserId(), null, 
            postEntity.getUser().getUsername(), 0, false, null, null,
             null, null, null, null));
        eventPost.setEvent(new EventJSON(eventPostEntity.getEvent().getEvent_Id(),
         eventPostEntity.getEvent().getEvent_Title(), null, null,
            null, null, null, null,null, null, null,
                null, null, null,null, 0));
        eventPost.setCategories(categories);
        return eventPost;
    }

    public Set<EventPostJSON> getEventPostsByEvent(EventJSON event) throws EJBException{
        Event eventEntity = eventOperations.getEventEntity(event);
        return eventEntity.getPosts().stream().map(candidate ->{
                        return getEventPostInfo(new EventPostJSON(candidate.getPost_Id(), null, null, 
                                                        0, 0, null,
                            new EventJSON(candidate.getEvent().getEvent_Id(), null, null,
                                null, null, null, null, null,
                                    null, null, null, null, null, null, null, 0)
                                        , null, null, null));
                    }).collect(Collectors.toSet());   
    }    

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    //add custom exception to show object was not created
    public void editRepost(RepostJSON post){
        //entityManager.joinTransaction();
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
            editRepostTaggedUsers(post);
            return;
        }
    }


    public Set<RepostJSON> getRepostsByUser(UserJSON user)throws NoResultException{
        User userEntity = userOperations.getUserEntity(user);
        return entityManager.createNamedQuery("Repost.findByUserId", Repost.class)
                    .setParameter("userId", userEntity.getUserId())
                        .getResultStream().map(candidate ->{
                            return getRepostInfo(new RepostJSON(candidate.getPost().getPost_Id(), null, null,
                                         0, 0,
                                             new UserJSON(candidate.getUser().getUserId(), null, null, 0, false, 
                                                null, null, null, null, null, null),
                                                    null , null, null, null));
                }).collect(Collectors.toSet());
    }


    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removeRepost(RepostJSON post){
        //entityManager.joinTransaction();
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

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    //add custom exception to show object was not created
    private PostWithReplyJSON createPostReplies(PostWithReplyJSON post) {
        entityManager.joinTransaction();
        Post parent = null; 
        try{
            parent = getPostEntity((PostJSON)post);
        }
        catch(NoResultException ex){
            PostJSON newPost = createPost((PostJSON)post);
            post.setPostId(newPost.getPostId());
            return createPostReplies(post);
        }
        if (post.getPosts() == null)
            return post;
        Set<PostJSON> comments = new HashSet<PostJSON>();
        for(PostJSON postInst : post.getPosts()) {
            Post child = null;
            try{
                child = getPostEntity(postInst);
                try{
                    getPostReplyEntity(child,parent);
                }
                catch(NoResultException ex){
                    createPostReply(child, parent);
                }
            }
            catch(NoResultException ex){
                PostJSON newPost = createPost(postInst);
                child = getPostEntity(newPost);
                createPostReply(child, parent);
                comments.add(newPost);
            }
        }
        post.setPosts(comments);
        return post;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private PostReply getPostReplyEntity(Post post, Post parent) throws NoResultException{
        //entityManager.joinTransaction();
        PostReplyId postRepId = new PostReplyId();
        postRepId.setPost_Id(post.getPost_Id());
        postRepId.setPost_Info(parent.getPost_Id());
        PostReply result = null;
        result = entityManager.find(PostReply.class, postRepId);
        if(result != null)
            return result;
        throw new NoResultException();
    }

    //@TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private void createPostReply(Post post, Post parent){
        //entityManager.joinTransaction();
        entityManager.merge(post);
        entityManager.merge(parent);
            if (post == null || parent == null)
                return;
         //editPost(post);
            try{
                PostReplyId postReplyId = new PostReplyId();
                postReplyId.setPost_Id(post.getPost_Id());
                postReplyId.setPost_Info(parent.getPost_Id());
                PostReply result = this.entityManager.find(PostReply.class, postReplyId);
                if(result == null){
                    PostReply postReply = new PostReply();
                    postReply.setParent(parent);
                    postReply.setPost(post);
                    postReply.setPost_Id(post.getPost_Id());
                    postReply.setPost_Info(parent.getPost_Id());
                    this.entityManager.persist(postReply);
                }
            }
            catch(NoResultException ex){
                PostReply postReply = new PostReply();
                postReply.setParent(parent);
                postReply.setPost(post);
                postReply.setPost_Id(post.getPost_Id());
                postReply.setPost_Info(parent.getPost_Id());
                this.entityManager.persist(postReply);
            }
    }

    //@TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public PostWithReplyJSON editPostReplies(PostWithReplyJSON post){
        /* 
        try{
            getPostEntity(post);
        }
        catch(NoResultException ex){
            createPost(post);
            //editPostReplies(post);
            //return;
        }
        */
        return createPostReplies(post);
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
    public PostJSON editPost(PostJSON post){
        //entityManager.joinTransaction();
        try{
            Post postEntity = getPostEntity(post);
            this.userOperations.getUserEntity(post.getUser()); 
            if (post.getComment() != null)
                postEntity.setComment(post.getComment());  
            if (post.getCreated() != null)
                postEntity.setCreated(Date.from(post.getCreated().atZone(ZoneId.systemDefault()).toInstant())); 
            this.entityManager.persist(postEntity);
            editPostCategory(post);
            editPostTaggedUsers(post);
            return post;
        }catch(NoResultException ex){
            post = createPost(post);
            return post;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void togglePostUpvote(PostJSON post, UserJSON user){
        this.entityManager.joinTransaction();
        try {
            Interact interact = getInteractEntity(user, post);
            if(interact.getInteract() == false){
                interact.setInteract(true);
                entityManager.persist(interact);   
            }
            else
                this.entityManager.remove(interact);
        } catch (NoResultException e) {
            Interact interact = new Interact();
            User userEntity = userOperations.getUserEntity(user);
            Post postEntity = getPostEntity(post);
            interact.setPost(postEntity);
            interact.setUser(userEntity);
            interact.setInteract(true);
            this.entityManager.persist(interact);
            this.entityManager.flush();    
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void togglePostDownvote(PostJSON post, UserJSON user){
        try {
            Interact interact = getInteractEntity(user, post);
            if(interact.getInteract() == true){
                interact.setInteract(false);
                entityManager.persist(interact);    
            }
            else
                this.entityManager.remove(interact);
        } catch (NoResultException e) {
            Interact interact = new Interact();
            User userEntity = userOperations.getUserEntity(user);
            Post postEntity = getPostEntity(post);
            interact.setPost(postEntity);
            interact.setUser(userEntity);
            interact.setInteract(false);
            this.entityManager.persist(interact);
            this.entityManager.flush();    
        }
    }

    private int getPostReplyCount(PostJSON post){
        Post postEntity  = getPostEntity(post);
        try{
            return this.entityManager.createNamedQuery("PostReplyEntity.getCommentCount", Integer.class)
                    .setParameter("postId", postEntity.getPost_Id()).getSingleResult().intValue();
        }catch(NoResultException ex){
            return 0;
        }
    }

    private Interact getInteractEntity(UserJSON user, PostJSON post) throws NoResultException{
        InteractId interactId = new InteractId();
        User userEntity = userOperations.getUserEntity(user);
        Post postEntity = getPostEntity(post);
        interactId.setPost_Id(postEntity.getPost_Id());
        interactId.setUserId(userEntity.getUserId());
        
        Interact interact = this.entityManager.find(Interact.class, interactId);
        if(interact == null)
            throw new NoResultException();
        
        return interact;
    }
    

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //add custom exception to show object was not created
    public PostWithReplyJSON editPostWithReply(PostWithReplyJSON postWithReply){       
         //entityManager.joinTransaction();
        /*try{
            getPostEntity((PostJSON)postWithReply);
            editPost(postWithReply);
            //editPost(postWithReply);
        }
        catch(NoResultException ex){
            createPost((PostJSON)postWithReply);
            //editPostWithReply(postWithReply);
        }
        */
        //createPostReplies(postWithReply);
        return editPostReplies(postWithReply);
        //removePostReplies(postWithReply);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removePostWithReply(PostWithReplyJSON postWithReply){
        //entityManager.joinTransaction();
        try{
            entityManager.joinTransaction();
            Post postEntity = getPostEntity((PostJSON)postWithReply);
            if (postEntity != null && postEntity.getPost_Id()!= null) {
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
            //entityManager.joinTransaction();
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
        entityManager.joinTransaction();
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
        int downVotes = 0;
        int upvotes = 0;
                
        try {
            Set<Interact> interactions = getInteractionsForPost(topicPost);
            upvotes = interactions.stream().filter(candidate ->{
                                return candidate.getInteract();
                            }).map(candidate -> {
                                return 1;
                            }).reduce(0, (x, y) ->{
                                return x+y;
                            });
            downVotes = interactions.size() - upvotes;
        } catch (NoResultException e) {
                    
        }
        topicPost.setDownvotes(downVotes);
        topicPost.setUpvotes(upvotes);
        topicPost.setPostId(topicPostEntity.getPost_Id());
        topicPost.setUser(new UserJSON(postEntity.getUser().getUserId(), null,
            postEntity.getUser().getUsername(), 0, false, null, null, null
            , null, null, null));
        topicPost.setTopic(topicOperations.getTopicInfo(
            new TopicJSON(topicPostEntity.getTopic_Id(), null, null, null, null,
             null)));        
        //topicPost.setCategories(categories);
        return new TopicPostJSON(topicPostEntity.getPost_Id(), 
                            postEntity.getComment(), 
                            LocalDateTime.ofInstant(postEntity.getCreated().toInstant(), ZoneId.systemDefault()), 
                            upvotes, 
                            downVotes,
                            new UserJSON(postEntity.getUser().getUserId(), null, null, 0, false, null, null,
                                 null, null, null, null), 
                            new TopicJSON(topicPostEntity.getTopic().getTopic_Id(), null, null, null, null, null), null, null, null);
    }

    public Set<TopicPostJSON> getTopicPostsByTopic(TopicJSON topic) throws NoResultException, EJBException{
        Topic topicEntity = topicOperations.getTopicEntity(topic);
        Stream<TopicPost> topicPosts = entityManager.createNamedQuery("TopicPost.findByTopicId", TopicPost.class)
                                .setParameter("topicId", topicEntity.getTopic_Id()).getResultStream();
        return topicPosts.map(candidate ->{
                        return getTopicPostInfo(new TopicPostJSON(candidate.getPost_Id(), null, null, 
                                                        0, 0, null,
                                                new TopicJSON(candidate.getTopic_Id(), null, null, null, null, null)
                                                        , null, null, null));
                    }).collect(Collectors.toSet());   
    }

    //@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PostJSON getPostInfo(PostJSON post) throws NoResultException{
        Post postInfo = getPostEntity(post);
        Set<UserJSON> taggedUsers = getPostTaggedUsers(postInfo);
        Set<CategoryJSON> categories = getPostCategories(new PostJSON(postInfo.getPost_Id(), null, null, 0, 0, null, null, null, null));
            int downVotes = 0;
            int upvotes = 0;            
            try {
                Set<Interact> interactions = getInteractionsForPost(post);
                upvotes = interactions.stream().filter(candidate ->{
                        return candidate.getInteract();
                            }).map(candidate -> {
                                return 1;
                            }).reduce(0, (x, y) ->{
                                return x+y;
                            });
                    downVotes = interactions.size() - upvotes;
            } catch (NoResultException e) {

            }      
        post = new PostJSON(postInfo.getPost_Id(), postInfo.getComment(), 
        LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()), 
            upvotes, downVotes, new UserJSON(postInfo.getUser().getUserId(),
                    null, postInfo.getUser().getUsername(), 0, false,
                         postInfo.getUser().getF_name(), postInfo.getUser().getL_Name(), null, null, null,
                          null), categories, null, taggedUsers);
        UserJSON user = post.getUser();
        user.setAvatar("");
        post.setUser(user);
        post.setCommentCount(getPostReplyCount(post));
        return post;
    }


    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeTopicPost(TopicPostJSON topicPost){
        //entityManager.joinTransaction();
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
        //entityManager.joinTransaction();
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
                TopicJSON foundTopic = topicOperations.editTopic(topicPost.getTopic());
                topic = topicOperations.getTopicEntity(foundTopic);
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
    public Post getPostEntity(PostJSON post) throws NoResultException{
        //entityManager.joinTransaction();
        if(post == null)
            throw new NoResultException();
        Post postEntity = null;
        if (post.getPostId() != null && post != null){
            postEntity = this.entityManager.find(Post.class, post.getPostId());
            if (postEntity != null)
                return postEntity;
            else throw new NoResultException();
        }
        else{
            throw new NoResultException();
        }
        /* 
        else if(post.getComment() != null) {
            Query query = this.entityManager.createNamedQuery("Post.getPostByComment", Post.class);
            String comment = '"' + post.getComment() + '*' + '"';
            query.setParameter(1, comment);
            postEntity = (Post) query.getSingleResult();
             if (postEntity != null)
                return postEntity;
            else
             throw new NoResultException();
        }
        throw new NoResultException();
        */
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void removePost(PostJSON post) {
        //entityManager.joinTransaction();
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

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<PostReply> getPostReplyEntities(PostWithReplyJSON postWithReply) throws NoResultException, 
                                                                                        TransactionRolledbackLocalException,
                                                                                            EJBTransactionRolledbackException,
                                                                                                EJBException{
        entityManager.joinTransaction();                                                                                            
        Set<PostReply> result = new HashSet<>();
        List<PostReply> postReplies = this.entityManager.createNamedQuery("PostReplyEntity.getRepliesOfPostId", PostReply.class)
                                        .setParameter("postId", postWithReply.getPostId())
                                            .getResultList();
        result = (Set<PostReply>)postReplies.stream().collect(Collectors.toSet());
        return result;
    }

    //@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<PostJSON> getPostWithReplyInfo(PostWithReplyJSON postWithReply) throws NoResultException,
                                                                                            TransactionRolledbackLocalException,
                                                                                                EJBException,
                                                                                                    EJBTransactionRolledbackException{
                                                                                                
        Set<PostReply> postReplyEntities = getPostReplyEntities(postWithReply);
        if (postReplyEntities == null)
            return null;
        Set<PostJSON> postReplys = (Set<PostJSON>)postReplyEntities.stream().map(candidate -> 
                                  new PostJSON(candidate.getPost_Id(), null, null, 0, 0, null, null, null, null))
                                    .collect(Collectors.toSet());
        return postReplys;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EventPost getEventPostEntity(EventPostJSON event) throws NoResultException, 
                                                                EJBException,
                                                                     EJBTransactionRolledbackException, 
                                                                        TransactionRolledbackLocalException{
        //entityManager.joinTransaction();
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

    private Set<Interact> getInteractionsForPost(PostJSON post)throws NoResultException{
        Post postEntity = getPostEntity(post);
        return this.entityManager.createNamedQuery("Interact.findByPostId", Interact.class)
                    .setParameter("postId", postEntity.getPost_Id()).getResultStream()
                        .collect(Collectors.toSet());
    }

    public boolean isLikedByUser(PostJSON post, UserJSON user){
        Interact interact = null;
        try{
           interact = getInteractEntity(user, post);   
        }
        catch(NoResultException ex){
            return false;
        }
        return interact.getInteract();
    }

    public boolean isDownvotedByUser(PostJSON post, UserJSON user) throws NoResultException{
        Interact interact = null;
        try{
           interact = getInteractEntity(user, post);   
        }
        catch(NoResultException ex){
            return false;
        }
        if(interact == null)
            return false;
        else
            return !interact.getInteract();
    }

    public Set<PostJSON> searchPost(PostJSON post) throws EJBException, NoResultException{
        List<Post> posts = null;
        if(post == null)
            posts = this.entityManager.createNamedQuery("Post.getAll", Post.class).getResultList();
        else if(post.getComment() != null &&((post.getUser() != null && post.getUser().getUsername() == null
                                                    && post.getUser().getUserId() == null) || 
                                                        post.getUser() == null))
            posts = this.entityManager.createNamedQuery("Post.searchByComment",Post.class)
                        .setParameter(1, post.getComment())
                        .getResultList();
        else if(post.getUser() != null && post.getUser().getUserId() == null && post.getUser().getUsername() != null ){
            UserJSON postedBy = userOperations.getUserInfo(new UserJSON(post.getUser().getUsername()));
            posts = this.entityManager.createNamedQuery("Post.findByUserId",Post.class)
                        .setParameter("userId", postedBy.getUserId())
                        .getResultList();
        }
        else if(post.getUser()!= null && post.getUser().getUserId() != null && post.getUser().getUsername() == null && post.getComment() == null)
            posts = this.entityManager.createNamedQuery("Post.findByUserId", Post.class)
                        .setParameter( "userId", post.getUser().getUserId())
                        .getResultList();
        else
            return Collections.singleton(getPostInfo(post));
        if(posts != null)
            return posts.stream().map(postInfo ->{
                int downVotes = 0;
                int upvotes = 0;
                
                try {
                    Set<Interact> interactions = getInteractionsForPost(post);
                    upvotes = interactions.stream().filter(candidate ->{
                                return candidate.getInteract();
                            }).map(candidate -> {
                                return 1;
                            }).reduce(0, (x, y) ->{
                                return x+y;
                            });
                    downVotes = interactions.size() - upvotes;
                } catch (NoResultException e) {
                    
                }
                Set<CategoryJSON> categories = getPostCategories(new PostJSON(postInfo.getPost_Id(), null, null, 0, 0, null, null, null, null));
                    return new PostJSON(postInfo.getPost_Id(), postInfo.getComment(), 
                        LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()), 
                    upvotes, downVotes,
                        new UserJSON(postInfo.getUser().getUserId(), null, null, 0, false, 
                                null, null, null, null, null, null),
                            categories,null, null);
                 }).collect(Collectors.toSet());
        else throw new NoResultException();

    }

}
