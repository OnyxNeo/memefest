package com.memefest.Services.Impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;



import com.memefest.DataAccess.CategoryFollower;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.PostReply;
import com.memefest.DataAccess.TopicFollower;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.UserFollower;
import com.memefest.DataAccess.UserFollowerId;
import com.memefest.DataAccess.UserSecurity;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.UserSecurityJSON;
import com.memefest.Services.AdminOperations;
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.PostOperations;
import com.memefest.Services.UserOperations;
import com.memefest.Services.UserSecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.RollbackException;
  
//add role based security
//@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "userservice")
//@SessionScoped
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserService  implements UserSecurityService, UserOperations, AdminOperations{

    @EJB
    private PostOperations postOps;

    @EJB
    private DataSourceOps dataSourceOps;

    //@TransactionScoped
    private EntityManager entityManager;

    
    @PostConstruct
    //@PostActivate
    public void init(){
        this.entityManager = dataSourceOps.getEntityManagerFactory().createEntityManager();

    }
    
    @PreDestroy
    //@PrePassivate
    public void destroy(){
        //factory.close();
        entityManager.close();
    }  

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public UserJSON createUser(String username, String firstName,
                                //String middleName
                                String lastName, 
                                    int contacts, String email, boolean verified, String password, String accessToken,
                                        String refreshToken)throws RollbackException{
        entityManager.joinTransaction();
        User newUser = new User();
        //entityManager.joinTransaction();
        //newUser.setUserId(0);
        newUser.setUsername(username);
        newUser.setF_name(firstName);
        //newUser.setM_name(middleName);
        newUser.setL_Name(lastName);
        newUser.setPhone_No(contacts);
        newUser.setEmail(email);
        newUser.setVerified(verified);
        /* 
        securityDetails.setUser(newUser);
        */
        entityManager.persist(newUser);
        //entityManager.joinTransaction();
        entityManager.flush();
        UserSecurity securityDetails = new UserSecurity();
        securityDetails.setUserId(newUser.getUserId());
        securityDetails.setPassword(password);
        securityDetails.setAccessTkn(accessToken);
        securityDetails.setRefreshTkn(refreshToken);
        newUser.setSecurityDetails(securityDetails);
        //entityManager.persist(newUser);
        entityManager.persist(newUser);
        UserSecurityJSON userSecurity = new UserSecurityJSON(accessToken, null, refreshToken, null, username);
        
        UserJSON user = new UserJSON(newUser.getUserId(), email, username, contacts, verified, firstName,
                    lastName, userSecurity, null, null, null);
        return user;
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public UserJSON createUser(UserJSON userJSON) throws RollbackException{
        entityManager.joinTransaction();
        return createUser(userJSON.getUsername(), userJSON.getFirstName(), 
                        //userJSON.getMiddleName(),
                        userJSON.getLastName(), userJSON.getContacts(), userJSON.getEmail() , userJSON.isVerified(), 
                            userJSON.getUserSecurity().getPassword(), userJSON.getUserSecurity().getAccessTkn(), 
                                userJSON.getUserSecurity().getRefreshTkn());
    }

    public void setSecurityDetails(UserSecurityJSON security){

        Query query = null;
        if(security.getPassword() != null){
            query = entityManager.createNamedQuery("UserSecurity.updatePassword");
            query.setParameter("username", security.getUser().getUsername());
            query.setParameter("password", security.getPassword());
            query.setParameter("accessTkn", security.getAccessTkn());
            query.setParameter("refreshTkn", security.getRefreshTkn());
            
        }
        else{
            query = entityManager.createNamedQuery("UserSecurity.updateJsonTkns");
            query.setParameter("username", security.getUser().getUsername());
            query.setParameter("accessTkn", security.getAccessTkn());
            query.setParameter("refreshTkn", security.getRefreshTkn()); 
        }
        int result = query.executeUpdate();
        if(result == 0){
            throw new RuntimeException("Unable to update password.");
        }
    }

    public Set<User> getUserEntities(){
       try{
        Stream<User> query = entityManager.createNamedQuery("UserEntity.findAllUsers", User.class) .getResultStream();
        return query.collect(Collectors.toSet());
       } 
       catch(NoResultException ex){
        return null;
       }
    }

    public Set<UserJSON> getAllUsers(){
        return getUserEntities().stream().map(candidate ->{
            return getUserInfo(new UserJSON(candidate.getUserId(), null, null, 0, false, null, null, null, null, null, null));
        }).collect(Collectors.toSet());
    }

    //return a no result exception if user does not exist
    public UserSecurityJSON getSecurityDetails(UserJSON user){  
        Query query = null; 
        
        if(user.getUserId() != null){
            query = entityManager.createNamedQuery("UserSecurity.findByUserId");
            query.setParameter("userId", user.getUserId());
        }
        else if(user.getUsername() != null){
            query = entityManager.createNamedQuery("UserSecurity.findByUsername");
            query.setParameter("username", user.getUsername());
        }
        else if(user.getEmail() != null){
            query = entityManager.createNamedQuery("UserSecurity.findByEmail");
            query.setParameter("email", user.getEmail());
        }


        try{
            Object[] security = (Object[])  query.getSingleResult();
            Long userId = (Long) security[0];
            String username = (String) security[1];
            String accessTkn = (String) security[3];
            String refreshTkn = (String) security[4];   
            UserSecurityJSON securityJSON = new 
            UserSecurityJSON(accessTkn, null, refreshTkn, userId, username);
            //UserJSON userDetails = new UserJSON(user.getUserId(), user.getUsername());
            return securityJSON;
        }
        catch(NoResultException ex){
            return null;
        }
            
    }

    public boolean emailExists(UserJSON user){
        if(user.getEmail() != null){
            Query query = entityManager.createNamedQuery("User.emailExists");
            query.setParameter("email", user.getEmail());
            Long count = (Long) query.getSingleResult();
            if(count == 1){
                return true;
            };
            return false;
        }
        else return false;
    }


    //return a no result exception if user does not exist
    public UserJSON getUserDetails(UserJSON user){
        Query query = null;
        if(user.getUserId()!= null){
            query = entityManager.createNamedQuery("UserSecurity.findByEmail");
            query.setParameter("email", user.getEmail());
        }
        else if(user.getUsername()!= null){
            query = entityManager.createNamedQuery("UserSecurity.findByUsername");
            query.setParameter("username", user.getUsername());
        }
        else if(user.getEmail()!= null){
            query = entityManager.createNamedQuery("UserSecurity.findByUserId");
            query.setParameter("userId", user.getUserId());
        }
        try{
            Object[] userDetails = (Object[]) query.getSingleResult();
            Long userId = (Long) userDetails[0];
            String username = (String) userDetails[1];
            String email = (String) userDetails[2];
            String accessTkn = (String) userDetails[3];
            String refreshTkn = (String) userDetails[4];
            user.setEmail(email);
            user.setUserId(userId);
            user.setUsername(username);
            user.setUserSecurity(new UserSecurityJSON(accessTkn, null, refreshTkn, userId, username));
            return user;
        }
        catch(NoResultException ex){
            return null;
        }
    }
    
    public boolean isAdmin(UserJSON user) throws NoResultException{
        if(user.getUserId() != null){
            User userDetails = entityManager.find(User.class, user.getUserId());
            if(userDetails!= null){
                if(userDetails.getAdmin() != null)
                    return true;
                else{
                    return false;
                }
            }
        }
        else{
            Query query = entityManager.createNamedQuery("Admin.isAdminByUsername");
            query.setParameter("username", user.getUsername());
            Long count = (Long) query.getSingleResult();
            if(count == 1){
                return true;
            };
            return false;
        }

            return false;
    }

    //return a no result exception if user does not exist
    public UserSecurityJSON getUserPassword(UserSecurityJSON userSecurityDetails) throws NoResultException{
        Query query = null;
        if(userSecurityDetails.getUser().getUserId() == null && userSecurityDetails.getUser().getUsername() != null){
            query = entityManager.createNamedQuery("UserSecurity.getUserPasswordFromUsername");
            query.setParameter("username", userSecurityDetails.getUser().getUsername());
        }
        else
        {
            query = entityManager.createNamedQuery("UserSecurity.getUserPasswordFromUserId");
            query.setParameter("userId", userSecurityDetails.getUser().getUserId());
        }
        
        Object[] security = (Object[]) query.getSingleResult();
        Long userId = (Long) security[1];
        String username = (String) security[2];
        String password = (String) security[0];
        return new UserSecurityJSON(null, password, null, userId, username);
    }

    public void setUserPassword(UserSecurityJSON userSecurity){
        Query query = null;
        if(userSecurity.getUser().getUserId() == null && userSecurity.getUser().getUsername()!= null){
            query = entityManager.createNamedQuery("UserSecurity.updatePasswordFromUsername");
            query.setParameter("username", userSecurity.getUser().getUsername());
            query.setParameter("password", userSecurity.getPassword());
        }
        else
        {
            query = entityManager.createNamedQuery("UserSecurity.updatePasswordFromUserId");
            query.setParameter("userId", userSecurity.getUser().getUserId());
            query.setParameter("password", userSecurity.getPassword());
        }
        int result = query.executeUpdate();
        if(result == 0){
            throw new RuntimeException("Unable to update password.");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //commit the followings of user from category entity
    public UserJSON editUser(UserJSON user){
        try {
            User userEntity = getUserEntity(user);
            userEntity.setEmail(user.getEmail());
            userEntity.setF_name(user.getFirstName());
            userEntity.setL_Name(user.getLastName());
            userEntity.setPhone_No(user.getContacts());
            userEntity.setUsername(user.getUsername());
            entityManager.merge(userEntity);
            return user;
        } catch (NoResultException e) {
            return createUser(user);
        }
    }



    //@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserJSON getUserInfo(UserJSON user) throws NoResultException, EJBException{
        if(user==null)
            throw new NoResultException("User Not Found");
        User userEntity = getUserEntity(user);
        Set<TopicJSON> topicsFollowing = null;
        Set<CategoryJSON> categoriesFollowing = null;
        Set<PostJSON> posts = null;
        try {
            topicsFollowing = getTopicFollowers(userEntity);
            categoriesFollowing = getCategoryFollowers(userEntity);
            posts = getUserPosts(new UserJSON(userEntity.getUserId(), null, null, 0, false, 
                null, null, null, null, null, null));  
        } catch (NoResultException ex) {
        
        }
        UserJSON userJSON = new UserJSON(userEntity.getUserId(), userEntity.getEmail(), userEntity.getUsername(),
                                userEntity.getPhone_No(),userEntity.isVerified(), userEntity.getF_name(),
                                    userEntity.getL_Name(), null,posts, 
                                        categoriesFollowing, topicsFollowing);
        return userJSON;
    }

    public Set<TopicFollower> getTopicFollowerEntities(User user){
        if(user == null)
            return null;
        Set<TopicFollower> topicsFollowed = null;
        topicsFollowed = user.getTopicFollowing();
        if(topicsFollowed == null){    
            List<TopicFollower> topics = entityManager.createNamedQuery("TopicFollower.findByUserId", TopicFollower.class)
                            .setParameter("userId", user.getUserId()).getResultList();
            return topics.stream().collect(Collectors.toSet());
        }
        return topicsFollowed;
    }

    public Set<PostJSON> getUserPosts(UserJSON user){
        User userEntity = getUserEntity(user);
        Set<Post> userPosts = getUserPostEntities(userEntity);
        Set<PostJSON> postJSONs = new HashSet<PostJSON>();
        if(userPosts == null)
            return null;
        for(Post p : userPosts){
            postJSONs.add(new PostJSON(p.getPost_Id(), null, null, 0,
            0,null,null,null,null));
        }
        return postJSONs;
    }


    public Set<PostWithReplyJSON> getComments(UserJSON user)throws EJBException{
        User userEntity = getUserEntity(user);
        Set<PostWithReplyJSON> parentPosts = new HashSet<PostWithReplyJSON>();
        Set<PostReply> postReplies = entityManager.createNamedQuery("PostReplyEntity.getRepliesByUserId", PostReply.class)
                                        .setParameter("userId", userEntity.getUserId()).getResultList()
                                        .stream().collect(Collectors.toSet());
        for(PostReply postReply : postReplies) {
            Set<PostJSON> posts = postReplies.stream()
                    .filter(candidate ->{
                        if(candidate.getPost_Info() == postReply.getPost_Info())
                            return true;
                        else 
                            return false;
                    })
                    .map(candidate -> {
                        return postOps.getPostInfo(new PostJSON(candidate.getPost_Id(), null, null, 0, 0,
                            user, null, null, null));
                    }).collect(Collectors.toSet());
            PostJSON parentInfo = postOps.getPostInfo(new PostJSON(postReply.getPost_Info(), null, null, 0,
                 0, user, null, null, null));
            
            PostWithReplyJSON postWithReply = new PostWithReplyJSON(postReply.getPost_Info(), parentInfo.getComment(), 
                parentInfo.getCreated(),parentInfo.getUpvotes(),0, user, posts, 
                parentInfo.getCategories(), parentInfo.getCanceledCategories(), parentInfo.getTaggedUsers());
            postWithReply.setPosts(posts);
            parentPosts.add(postWithReply);
        }
        return parentPosts;
    }


    private Set<Post> getUserPostEntities(User user){
        if(user == null)
            return null;    
            List<Post> posts = entityManager.createNamedQuery("Post.getByUserId", Post.class)
                            .setParameter(1, user.getUserId()).getResultList();
        return posts.stream().collect(Collectors.toSet());
    }

    public boolean isFollowedByUser(UserJSON user, UserJSON follower){
        UserFollowerId userFollowerId = new UserFollowerId();
        User userEntity = getUserEntity(user);
        User followerEntity = getUserEntity(follower);
        userFollowerId.setFollowerId(followerEntity.getUserId());
        userFollowerId.setUserId(userEntity.getUserId()); 
        UserFollower userFollower = entityManager.find(UserFollower.class, userFollowerId);
        if(userFollower != null)
            return true;
        else
            return false;
    }

    public void toggleFollowedBy(UserJSON user, UserJSON follower){
        if(isFollowedByUser(user, follower)){
            removeFollower(user, follower);
        }
        else{
            addFollower(user, follower);
        }
    }

    public Set<TopicJSON> getTopicFollowers(User user)throws NoResultException{
        Set<TopicFollower> topicFollowers = getTopicFollowerEntities(user);
        Set<TopicJSON> topicJSONs = new HashSet<TopicJSON>();
        if(topicFollowers == null)
            return null;
        for(TopicFollower tf : topicFollowers){
            topicJSONs.add(new TopicJSON(tf.getTopic_Id(), null, null, null, null,
            null));
        }
        return topicJSONs;
    }

    public Set<CategoryFollower> getCategoryFollowerEntities(User user){
        if(user == null)
            return null;
        Set<CategoryFollower> categoryFollowers = null;
        categoryFollowers = user.getCategoriesFollowing();
        if(categoryFollowers == null){    
            List<CategoryFollower> topics  = entityManager.createNamedQuery("TopicFollower.findByUserId", CategoryFollower.class)
                .setParameter("userId", user.getUserId()).getResultList();
            return topics.stream().collect(Collectors.toSet());
        }
        return categoryFollowers;
    }

    public Set<CategoryJSON> getCategoryFollowers(User user){
        Set<CategoryFollower> topicFollowers = getCategoryFollowerEntities(user);
        Set<CategoryJSON> categJSONs = new HashSet<CategoryJSON>();
        if(topicFollowers == null)
            return null;
        for(CategoryFollower tf : topicFollowers){
            categJSONs.add(new CategoryJSON(tf.getCat_Id(), null, null, null, null));
        }
        return categJSONs;
    }

    //@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserEntity(UserJSON user)throws NoResultException{
        if(user.getUserId() == null && user.getUsername() == null)
            throw new NoResultException("No User Found");
        User userEntity = null;
        if(user.getUserId() != null){
            try {
                userEntity = entityManager.find(User.class, user.getUserId());
                if(userEntity == null)
                    throw new NoResultException(); 
            } catch (NoResultException ex) {
                Query query = entityManager.createNamedQuery("User.findUsersByUsername", User.class);
                query.setParameter("username", user.getUsername());
                userEntity = (User) query.getSingleResult();
            }
        }
        else if(user.getUsername() != null){
            userEntity =(User) entityManager.createNamedQuery("User.findUsersByUsername", User.class)
            .setParameter("username", user.getUsername()).getSingleResult();
        }
        if(userEntity == null)
            throw new NoResultException("No User Found");
        return userEntity; 
    }

    public Set<UserJSON> searchByUsername(UserJSON userJSON){
        if(userJSON  != null &&  userJSON.getUsername() != null ){
            List<User> users = entityManager.createNamedQuery("User.searchByUsername", User.class)
                                .setParameter("username", "%" + userJSON.getUsername() + "%").getResultList();
            return users.stream().map(user ->{ 
                                UserJSON userInst  = new UserJSON(user.getUserId(), null, null,
                                 0, false, null, null, null, null, null, null);
                                    return userInst;
                    }).collect(Collectors.toSet());
            }
        else{
            return null;
        }
    }

    public void deleteUser(UserJSON user){
        User userEntity = getUserEntity(user);
        if(userEntity == null)
            return;
        entityManager.remove(userEntity);
        entityManager.remove(userEntity.getSecurityDetails());
        entityManager.flush();
    }

    public Set<UserJSON> getFollowers(UserJSON user){
        try{
            User userEntity = getUserEntity(user);
            Set<UserJSON>  following = userEntity.getUserFollowedBy().stream().map(userEntityInst -> {
                                            UserJSON userInst = new UserJSON(userEntityInst.getFollower().getUsername());
                                            return userInst;
                        
            }).collect(Collectors.toSet());
            return following;
        }
        catch(NoResultException ex){
            return null;
        }
    }

    public Set<UserJSON> getFollowing(UserJSON user){
        try{
            User userEntity = getUserEntity(user);
            Set<UserJSON>  following = userEntity.getUserFollowing().stream().map(userEntityInst -> {
                                            UserJSON userInst = new UserJSON(userEntityInst.getUser().getUsername());
                                            return userInst;
                        
            }).collect(Collectors.toSet());
            return following;
        }
        catch(NoResultException ex){
            return null;
        }
    }

    public void addFollower(UserJSON user, UserJSON follower) throws NoResultException{
        if(user == null || follower == null)
            throw new  NoResultException("No User found");
        
        User userEntity = getUserEntity(user);
        User followerEntity = getUserEntity(follower);
        UserFollower userFollowerEntity = new UserFollower();
        userFollowerEntity.setFollower_Id(followerEntity.getUserId());
        userFollowerEntity.setUserId(userEntity.getUserId());
        entityManager.persist(userFollowerEntity);
    }

    public void removeFollower(UserJSON user, UserJSON follower){
        if(user == null || follower == null)
            throw new  NoResultException("No User found");
        User userEntity = getUserEntity(user);
        User followerEntity = getUserEntity(follower);
        UserFollowerId userFollowerId = new UserFollowerId();
        userFollowerId.setFollowerId(followerEntity.getUserId());
        userFollowerId.setUserId(userEntity.getUserId());
        UserFollower userFollower = entityManager.find(UserFollower.class, userFollowerId);
        entityManager.remove(userFollower);
    }


}

