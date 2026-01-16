package com.memefest.Services.Impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.Topic;
import com.memefest.DataAccess.TopicCategory;
import com.memefest.DataAccess.TopicCategoryId;
import com.memefest.DataAccess.TopicFollower;
import com.memefest.DataAccess.TopicFollowerId;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.CategoryOperations;
import com.memefest.Services.NotificationOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.TopicOperations;
import com.memefest.Services.UserOperations;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.ScheduleExpression;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import jakarta.transaction.TransactionScoped;


@Stateless(name = "TopicService")
public class TopicService implements TopicOperations{


    @Resource
    private TimerService timerService;
    
    @TransactionScoped
    //@PersistenceContext(unitName = "TopicService")
    private EntityManager entityManager;

    @EJB
    private CategoryOperations catOps;

    @EJB
    private UserOperations userOperations;

    @EJB
    private PostOperations postOperations;

    @EJB
    private NotificationOperations notOps;


    private EntityManagerFactory factory;

    @PostConstruct
    public void init(){
        String databaseName = "Memefest";
        String serverName = "CHHUMBUCKET";
        String instanceName = "MSSQLSERVER";
        String username = "Neutron";
        String password = "ScoobyDoo24";
        String encrypt = "false";
        int portNumber = 1433;
        boolean trustServerCertificate = true;


        String dataSourceName = "DataSource/TopicService";
        String unitName = "TopicServicePersistenceUnit";  
        
        SQLServerDataSource ssDataSource = new SQLServerDataSource();
        ssDataSource.setDatabaseName(databaseName);
        ssDataSource.setTrustServerCertificate(trustServerCertificate);
        ssDataSource.setServerName(serverName);
        ssDataSource.setInstanceName(instanceName);
        ssDataSource.setUser(username);
        ssDataSource.setPassword(password);
        ssDataSource.setPortNumber(portNumber);
        ssDataSource.setEncrypt(encrypt);
        try{
            Context context = new InitialContext();   
            try {

                context.rebind(dataSourceName, (DataSource) ssDataSource);
            }catch (NamingException e) {
                try {
                    context.bind(dataSourceName,(DataSource) ssDataSource);
                //ssDataSource = (DataSource) context.lookup("DataSource/Memefest");
                } catch (NamingException ec) {
                    throw new RuntimeException(ec);
                }
            }
        }catch(NamingException ex){
            throw new RuntimeException(ex);
        }
            Map<String, Object> memeProps = new HashMap<>();
            memeProps.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.JTA.name());
            memeProps.put(PersistenceUnitProperties.TARGET_SERVER, TargetServer.None);
            memeProps.put(PersistenceUnitProperties.JDBC_USER, username);
            memeProps.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
            //memeProps.put(PersistenceUnitProperties.CONNECTION_POOL_JTA_DATA_SOURCE, "DataSource/Memefest");
            memeProps.put(PersistenceUnitProperties.JTA_DATASOURCE, dataSourceName);
            memeProps.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS, unitName);
            memeProps.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            PersistenceProvider provider = new PersistenceProvider();

            org.eclipse.persistence.jpa.config.PersistenceUnit unit = new PersistenceUnitImpl(unitName);
            unit.setProvider("org.eclipse.persistence.jpa.PersistenceProvider");
        //unit.setJtaDataSource("DataSource/Memefest" );

        unit.setClass("com.memefest.DataAccess.UserSecurity");
        unit.setClass("com.memefest.DataAccess.CategoryFollower");
        unit.setClass("com.memefest.DataAccess.Category");
        unit.setClass("com.memefest.DataAccess.Event");
        unit.setClass("com.memefest.DataAccess.EventCategory");
        unit.setClass("com.memefest.DataAccess.EventImage");
        unit.setClass("com.memefest.DataAccess.EventNotification");
        unit.setClass("com.memefest.DataAccess.EventPost");
        unit.setClass("com.memefest.DataAccess.EventPostNotification");
        unit.setClass("com.memefest.DataAccess.EventVideo");
        unit.setClass("com.memefest.DataAccess.FollowNotification");
        unit.setClass("com.memefest.DataAccess.Image");
        unit.setClass("com.memefest.DataAccess.Post");
        unit.setClass("com.memefest.DataAccess.PostCategory");
        unit.setClass("com.memefest.DataAccess.PostImage");
        unit.setClass("com.memefest.DataAccess.PostNotification");
        unit.setClass("com.memefest.DataAccess.PostReply");
        unit.setClass("com.memefest.DataAccess.PostVideo");
        unit.setClass("com.memefest.DataAccess.JokeOfDay");
        unit.setClass("com.memefest.DataAccess.Sponsor");
        unit.setClass("com.memefest.DataAccess.JokeOfDayPost");
        unit.setClass("com.memefest.DataAccess.PostTaggedUser");
        unit.setClass("com.memefest.DataAccess.RepostTaggedUser");
        unit.setClass("com.memefest.DataAccess.Interact");
        unit.setClass("com.memefest.DataAccess.Repost");
        unit.setClass("com.memefest.DataAccess.SubCategory");
        unit.setClass("com.memefest.DataAccess.Topic");
        unit.setClass("com.memefest.DataAccess.TopicCategory");
        unit.setClass("com.memefest.DataAccess.TopicFollower");
        unit.setClass("com.memefest.DataAccess.TopicFollowNotification");
        unit.setClass("com.memefest.DataAccess.TopicImage");
        unit.setClass("com.memefest.DataAccess.TopicPost");
        unit.setClass("com.memefest.DataAccess.TopicPostNotification");
        unit.setClass("com.memefest.DataAccess.TopicVideo");
        unit.setClass("com.memefest.DataAccess.User");
        unit.setClass("com.memefest.DataAccess.UserAdmin");
        unit.setClass("com.memefest.DataAccess.UserFollower");
        unit.setClass("com.memefest.DataAccess.Video");

        unit.setExcludeUnlistedClasses(false);
        //unit.setName("Memefest");
        unit.setTransactionType(PersistenceUnitTransactionType.JTA);     
        unit.setName(unitName);
        unit.setJtaDataSource(dataSourceName);
        //PersistenceProvider provider = new PersistenceProvider();
        //persistenceUnit.setExcludeUnlistedClasses(false);
        //persistenceUnit.getPersistenceUnitInfo().
        this.factory = provider.createContainerEntityManagerFactory(unit.getPersistenceUnitInfo(), memeProps);
        //EntityManagerFactoryWrapper wrapper = new EntityManagerFactoryWrapper(factory

        this.entityManager = factory.createEntityManager();
            //entityManager.joinTransaction();
      
    }

    @PreDestroy
    public void destroy(){
        //factory.close();
        entityManager.close();
    }

    public void createScheduledTopic(TopicJSON topic, LocalDateTime postDate){
        ScheduleExpression schedule = new ScheduleExpression()
                                        .year(postDate.getYear())
                                        .month(postDate.getMonthValue())
                                        .dayOfMonth(postDate.getDayOfMonth()).hour(postDate.getHour())
                                        .minute(postDate.getMinute()).second(postDate.getSecond());
        TimerConfig timerConf = new TimerConfig(topic, true);
        timerService.createCalendarTimer(schedule, timerConf);
    }
    
    public void cancelScheduledTopic(TopicJSON topic){
        Collection<Timer> timers = timerService.getTimers();
        for (Timer timerInst : timers) {
            TopicJSON scheduledInst = (TopicJSON) timerInst.getInfo();
            if(topic.getTopicId() != null && scheduledInst.getTopicId() == scheduledInst.getTopicId()
            || topic.getTitle() == scheduledInst.getTitle()){
                    timerInst.cancel();
            }
        }
    }

    public Map<TopicJSON, LocalDateTime> getScheduledTopics(TopicJSON topic){
        Map<TopicJSON, LocalDateTime> events = new HashMap<TopicJSON, LocalDateTime>(); 
        for(Timer timer : timerService.getAllTimers()){
            if(timer.getInfo() instanceof TopicJSON){
                TopicJSON timerInfo = (TopicJSON) timer.getInfo();
                if((topic != null && topic.getTopicId()!= null && timerInfo.getTopicId() == topic.getTopicId() && timerInfo.getTopicId()!= null)
                         || (topic!= null && timerInfo.getTitle()!= null && timerInfo.getTitle() != null && timerInfo.getTitle().equalsIgnoreCase(topic.getTitle())) 
                            || topic == null){
                    ScheduleExpression schedule = timer.getSchedule();
                    LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(schedule.getYear()), 
                                            Integer.parseInt(schedule.getMonth()), 
                                            Integer.parseInt(schedule.getDayOfMonth()),
                                            Integer.parseInt(schedule.getHour()),
                                            Integer.parseInt(schedule.getMinute()), 
                                            Integer.parseInt(schedule.getSecond()));
                    events.put(timerInfo, dateTime);   
                }
            }
        }
        return events;
    } 

    @Timeout
    public void sendTopic(Timer timer) {
        if(timer.getInfo() instanceof TopicJSON){
            TopicJSON topic = (TopicJSON) timer.getInfo();
            topic.setCreated(LocalDateTime.now());
            createTopic(topic);
        }
    }

    public void editScheduledTopic(Map<TopicJSON, LocalDateTime> scheduledTopics){
        scheduledTopics.entrySet().forEach(candidate -> {
            TopicJSON topic = candidate.getKey();
            if(topic.isCancelled()){
                cancelScheduledTopic(topic);
            }
            else{
                createScheduledTopic(topic, candidate.getValue());
            }
        });
    }
    
    //add custom exception to show object was not created
    public void createTopic(TopicJSON topic) {
        try{
             getTopicEntity(topic);
        }
        catch(NoResultException ex){
            Topic foundTopic = new Topic();
            foundTopic.setTitle(topic.getTitle());
            //foundTopic.setCreated(Date.from(topic.getCreated().atZone(ZoneId.systemDefault()).toInstant()));
            this.entityManager.persist(foundTopic);
        }
    }
    //add custom exception to show object was not created
    public void createTopicCategories(TopicJSON topic) throws NoResultException{
        Topic foundTopic = getTopicEntity(topic);
        Set<CategoryJSON> categories = topic.getCategories();
        if(categories == null) 
            return;    
        for(CategoryJSON category : categories){
            if(category == null)
                continue;
            Category categoryEntity = null;
            try{
                categoryEntity = catOps.getCategoryEntity(category);
            }
            catch(NoResultException ex){    
                catOps.editCategory(category);
            }
            categoryEntity = catOps.getCategoryEntity(category);
            if (categoryEntity == null)
                continue;
            TopicCategory topicCategory = new TopicCategory();
            topicCategory.setCategory(categoryEntity);
            topicCategory.setTopic(foundTopic);
            entityManager.persist(topicCategory);

        }
            /* 
            foundTopic.setSubCategories(subCategories);
            MainCategory mainCat = null;
            for (CategoryJSON candidate : categories){
                try{
                    mainCat = catOps.getMainCategoryEntity(candidate);
                    if(mainCat != null){
                        foundTopic.setMainCategory(mainCat);
                        entityManager.persist(foundTopic);
                    }
                }
                catch(NoResultException exp){
                    continue;
                }
            }
            */            
    }   
  
    public void removeTopicCategories(TopicJSON topic) {
        if(topic.getCategories() == null)
            return;
        try{
            Topic foundTopic = getTopicEntity(topic);
            if(topic.getCancelCategories() == null)
                return;
            Set<CategoryJSON> categories = topic.getCancelCategories();
            for (CategoryJSON cat : categories) {
                try{
                    TopicCategory topicCategory = getTopicCategory(foundTopic, catOps.getCategoryEntity(cat));
                    if (topicCategory != null)
                        this.entityManager.remove(topicCategory);
                }
                catch(NoResultException ex){
                    continue;
                }       
            } 
        }
        catch(NoResultException ex){
            return;
        }
    }

    private TopicCategory getTopicCategory(Topic topic, Category category){
        TopicCategoryId topicCategoryId = new TopicCategoryId();
        topicCategoryId.setCat_Id(category.getCat_Id());
        topicCategoryId.setTopic_Id(topic.getTopic_Id());
        return entityManager.find(TopicCategory.class, topicCategoryId);
    }

  
    private Set<CategoryJSON> getTopicCategories(TopicJSON topic)throws NoResultException{
        Topic topicEntity = getTopicEntity(topic);
        Set<CategoryJSON> topics = new HashSet<CategoryJSON>();
        Stream<TopicCategory> topicCats = entityManager.createNamedQuery("TopicCategory.getByTopicId", TopicCategory.class)
              .setParameter("topicId", topicEntity.getTopic_Id()).getResultStream();
        if (topicCats== null)
            throw new NoResultException();
        topics = topicCats.map(candidate ->{
        return new CategoryJSON(candidate.getCat_Id(),null,null,null,null);
        }).collect(Collectors.toSet());
    return topics;
    }
  
    public Topic getTopicEntity(TopicJSON topic) throws NoResultException {
        if(topic == null)
            throw new NoResultException();
        Topic foundTopic = null;
        if ((topic != null) && topic.getTopicId() != null) {
            foundTopic = (Topic)this.entityManager.find(Topic.class, topic.getTopicId());
            if(foundTopic == null)
                throw new NoResultException("Topic with topic Id not found");
        } 
        else if(topic.getTitle() != null){
            foundTopic = this.entityManager.createNamedQuery("Topic.getTopicByTitle", Topic.class)
                            .setParameter(1, topic.getTitle()).getSingleResult();
        }
        else throw new NoResultException();
        return foundTopic;
    }
    
    //add custom exception to show object was not created
    public void createTopicFollowers(TopicJSON topic) {
        Topic foundTopic = null;
        try{
            foundTopic = getTopicEntity(topic);
        }
        catch(NoResultException ex){
           createTopic(topic);
           try{
                foundTopic = getTopicEntity(topic);
           }
           catch(NoResultException exp){
            return;
           } 
        }
        for(UserJSON user : topic.getFollowedBy()) {
            User newFollower = this.userOperations.getUserEntity(user);
            if (newFollower == null)
                return; 
            TopicFollower follower = getTopicFollower(user, topic);
            if (follower == null) {
                TopicFollower newTopicFollower = new TopicFollower();
                newTopicFollower.setTopic(foundTopic);
                newTopicFollower.setFollower(newFollower);
                this.entityManager.persist(newTopicFollower);
            }
        }
    }
  
    private TopicFollower getTopicFollower(UserJSON user, TopicJSON topic) {
        TopicFollower foundFollower = null;
        if (user != null && topic != null && user.getUserId() != null && topic.getTopicId() != null) {
            TopicFollowerId followerId = new TopicFollowerId();
            followerId.setUserId(user.getUserId());
            followerId.setTopic_Id(topic.getTopicId());
            foundFollower = (TopicFollower)this.entityManager.find(TopicFollower.class, followerId);
            return foundFollower;
        } 
        if (user != null && topic != null) {
            Topic topicEntity = getTopicEntity(topic);
            User userEntity = null;
            try{
                userEntity = this.userOperations.getUserEntity(user);
            }catch(NoResultException ex){
                return null;
            }
            if (topicEntity == null || userEntity == null)
                return null;
            user.setUserId(user.getUserId());
            topic.setTopicId(topic.getTopicId());
            return getTopicFollower(user, topic);
        } 
        return null;
    }
  
    public void removeTopicFollowers(TopicJSON topic) {
        Set<UserJSON> followers = topic.getFollowedBy();
        Set<UserJSON> canceledFollowers = (Set<UserJSON>)followers.stream().filter(candidate -> candidate.isCancelled()).collect(Collectors.toSet());
        if (canceledFollowers != null && canceledFollowers.size() > 0)
        for (UserJSON user : canceledFollowers) {
            TopicFollower follower = getTopicFollower(user, topic);
            if (follower != null)
                this.entityManager.remove(follower); 
            } 
    }
  
    //add custom exception to show object was not created
    public void editTopicFollowers(TopicJSON topic){
        try{
            getTopicEntity(topic);
        }
        catch(NoResultException ex){
            return;
        } 
        Set<UserJSON> followers = topic.getFollowedBy();
        Set<UserJSON> updatedFollowers = (Set<UserJSON>)followers.stream().filter(candidate -> !candidate.isCancelled()).collect(Collectors.toSet());
        Set<UserJSON> deletedFollowers = (Set<UserJSON>)followers.stream().filter(candidate -> candidate.isCancelled()).collect(Collectors.toSet());
        if (updatedFollowers != null && updatedFollowers.size() > 0)
        for (UserJSON user : updatedFollowers) {
            TopicFollower follower = getTopicFollower(user, topic);
            if (follower == null)
                createTopicFollowers(topic); 
        }  
        if (deletedFollowers != null && deletedFollowers.size() > 0)
            for (UserJSON user : deletedFollowers) {
            TopicFollower follower = getTopicFollower(user, topic);
            if (follower != null)
                this.entityManager.remove(follower); 
        }  
    }

    //add custom exception to show object was not created
    public void editTopic(TopicJSON topic) {
        Topic foundTopic = null;
        if(topic.isCancelled())
            removeTopic(topic);
        if (topic.getCategories() != null && topic.getCategories().size() > 0)
                for (CategoryJSON category : topic.getCategories())
                    catOps.editCategory(category);
        try {
            foundTopic = getTopicEntity(topic);
            if (foundTopic == null)
                return; 
            if (topic.getTitle() != null && topic.getTitle().equalsIgnoreCase(foundTopic.getTitle()))
                foundTopic.setTitle(topic.getTitle()); 
            if (topic.getCreated() != null )
                foundTopic.setCreated(Date.from(topic.getCreated().atZone(ZoneId.systemDefault()).toInstant() ));      
        } catch (NoResultException e) {
            createTopic(topic);
            editTopic(topic);
            return;
        }        
        this.entityManager.merge(foundTopic);
        if (topic.getPosts() != null && topic.getPosts().size() > 0)
                for (PostJSON post : topic.getPosts())
                    postOperations.editPost(post);
        if (topic.getFollowedBy() != null && topic.getFollowedBy().size() > 0)
                editTopicFollowers(topic);
        createTopicCategories(topic);
        try{
            removeTopicCategories(topic);
        }
        catch(NoResultException ex){
            ex.printStackTrace();
        }
    }
  
    public void removeTopic(TopicJSON topic) {
        if (topic.isCancelled()) {
            Topic foundTopic = null;
            try{
                foundTopic = getTopicEntity(topic);
            }
            catch(NoResultException ex){
                return;
            }
            if (foundTopic != null) {
                for (PostJSON post : topic.getPosts())
                    postOperations.editPost(post); 
                removeTopicFollowers(topic);
                this.entityManager.remove(foundTopic);
            } 
        } 
    }

    public TopicJSON getTopicInfo(TopicJSON topic) throws NoResultException,EJBException{
        if(topic == null)
            throw new NoResultException("topic is null yo");
        Topic topicEntity = getTopicEntity(topic);
        if (topicEntity == null)
            throw new NoResultException();
        TopicJSON topicJSON = null; 

        Set<TopicFollower> topicFollowers = topicEntity.getFollowedBy();
        Set<CategoryJSON> categories = null;
        try{
            categories = getTopicCategories(topic);
        }
        catch(NoResultException ex){
            
        }
        Set<UserJSON> users = (Set<UserJSON>)topicFollowers.stream().map(topicFollower -> new UserJSON(topicFollower.getUser()
                                .getUsername())).collect(Collectors.toSet());
        Set<TopicPostJSON> posts = postOperations.getTopicPostsByTopic(topic);
       /*  Set<TopicPostJSON> posts = (Set<TopicPostJSON>)topicEntity.getPosts().stream().map(topicPost ->{
                                    Post postInfo = topicPost.getPost();
                                    return new TopicPostJSON(topicPost.getPost_Id(), postInfo.getComment(), 
                                    LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()),
                                    postInfo.getUpvotes(), postInfo.getDownvotes(), 
                                    new UserJSON(postInfo.getUser().getUsername()), 
                                    new TopicJSON(topicPost.getTopic().getTopic_Id(), topicPost.getTopic().getTitle(),
                                    LocalDateTime.ofInstant(topicPost.getTopic().getCreated().toInstant(), ZoneId.systemDefault()),
                                   null, null, null),null,null);
                                }).collect(Collectors.toSet());
        */
        if (topicEntity != null) {
           topicJSON = new TopicJSON(topicEntity.getTopic_Id(), topicEntity.getTitle(), 
                                    LocalDateTime.ofInstant(topicEntity.getCreated().toInstant(), ZoneId.systemDefault()),
                                    categories, posts, users);
        } 
        return topicJSON;
    }

    public Set<TopicJSON> searchTopic(TopicJSON topic){
        List<Topic> topics = null;
        if(topic == null)
            topics = this.entityManager.createNamedQuery("Topic.getAll", Topic.class).getResultList();
        else if(topic.getTitle() != null)
            topics = this.entityManager.createNamedQuery("Topic.searchByTitle", Topic.class)
                            .setParameter(1, topic.getTitle()).getResultList();
            return topics.stream().map(topicEntity ->{
                TopicJSON topicJSON = null; 
                Set<TopicFollower> topicFollowers = topicEntity.getFollowedBy();
                Set<CategoryJSON> categories = null;
                try{
                    categories = getTopicCategories(topic);}
                catch(NoResultException ex){

                }
                Set<UserJSON> users = (Set<UserJSON>)topicFollowers.stream().map(topicFollower -> new UserJSON(topicFollower.getUser()
                                .getUsername())).collect(Collectors.toSet());
               /*  Set<TopicPostJSON> posts = (Set<TopicPostJSON>)topicEntity.getPosts().stream().map(topicPost ->{
                                    Post postInfo = topicPost.getPost();
                                    return new TopicPostJSON(topicPost.getPost_Id(), postInfo.getComment(), 
                                    LocalDateTime.ofInstant(postInfo.getCreated().toInstant(), ZoneId.systemDefault()),
                                    postInfo.getUpvotes(), postInfo.getDownvotes(), 
                                    new UserJSON(postInfo.getUser().getUsername()), 
                                    new TopicJSON(topicPost.getTopic().getTopic_Id(), topicPost.getTopic().getTitle(),
                                    LocalDateTime.ofInstant(topicPost.getTopic().getCreated().toInstant(), ZoneId.systemDefault()),
                                   null, null, null),null, null);
                                }).collect(Collectors.toSet());
                */
                Set<TopicPostJSON> posts = postOperations.getTopicPostsByTopic(topic);
                                topicJSON = new TopicJSON(topicEntity.getTopic_Id(), topicEntity.getTitle(), 
                                    LocalDateTime.ofInstant(topicEntity.getCreated().toInstant(), ZoneId.systemDefault()),
                                    categories, posts, users);
                return topicJSON;
            }).collect(Collectors.toSet());
            

    }
}
