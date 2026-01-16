package com.memefest.Services.Impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.memefest.DataAccess.JokeOfDay;
import com.memefest.DataAccess.JokeOfDayPost;
import com.memefest.DataAccess.JokeOfDayScheduleBackup;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.Sponsor;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.JokeOfDayJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.SponsorJSON;
import com.memefest.Services.JokeOfDayOperations;
import com.memefest.Services.PostOperations;
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

@Stateless(name = "JokeOfDayService")
public class JokeOfDayService implements JokeOfDayOperations{
    

    @EJB
    private UserOperations userOps;

    @EJB
    private PostOperations postOps;

    @Resource
    private TimerService timerService;

    private EntityManagerFactory factory;

    @TransactionScoped
    private EntityManager entityManager;

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


        String dataSourceName = "DataSource/JokeOfDayService";
        String unitName = "JokeOfDayServicePersistenceUnit";  
        
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
        factory.close();
        entityManager.close();
    }  

    public void scheduleJokeOfDay(JokeOfDayJSON jokeOfDay){
        ScheduleExpression schedule = new ScheduleExpression().year(jokeOfDay.getDate().getYear())
                                            .dayOfMonth(jokeOfDay.getDate().getDayOfMonth())
                                            .hour(jokeOfDay.getDate().atStartOfDay().getHour())
                                            .minute(jokeOfDay.getDate().atStartOfDay().getMinute());
        TimerConfig timerConf = new TimerConfig(jokeOfDay, true);       
        timerService.createCalendarTimer(schedule, timerConf);
        JokeOfDayScheduleBackup jkSchedule = new JokeOfDayScheduleBackup();
        jkSchedule.setPunchline(jokeOfDay.getPunchline());
        jkSchedule.setAuthor(jokeOfDay.getUser().getName());
        jkSchedule.setTimestamp(Date.from(jokeOfDay.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        this.entityManager.persist(jkSchedule);
    }

    public void cancelScheduledJokeOfDay(JokeOfDayJSON jokeOfDay){
        Collection<Timer> timers = timerService.getTimers();
        for (Timer timerInst : timers) {
            JokeOfDayJSON scheduledInst = (JokeOfDayJSON) timerInst.getInfo();
            if(jokeOfDay.getJokeId() != null && scheduledInst.getJokeId() == scheduledInst.getJokeId()
            || jokeOfDay.getPunchline().equalsIgnoreCase(scheduledInst.getPunchline()) 
                || jokeOfDay.getDate().getDayOfYear() == scheduledInst.getDate().getDayOfYear()){
                    timerInst.cancel();
            }
        }
        
    }    

    public Set<JokeOfDayJSON> getScheduledJokeOfDay (JokeOfDayJSON topic){
        Set<JokeOfDayJSON> events = new HashSet<JokeOfDayJSON>(); 
        for(Timer timer : timerService.getAllTimers()){
            if(timer.getInfo() instanceof JokeOfDayJSON){
                JokeOfDayJSON timerInfo = (JokeOfDayJSON) timer.getInfo();
                if((topic != null && topic.getJokeId()!= null && timerInfo.getJokeId() == topic.getJokeId() 
                        && timerInfo.getJokeId()!= null)
                        || (topic!= null && timerInfo.getPunchline()!= null && timerInfo.getPunchline() != null 
                            && timerInfo.getPunchline().equalsIgnoreCase(topic.getPunchline())) 
                            || topic == null || timerInfo.getDate().getDayOfYear() == topic.getDate().getDayOfYear()){
                    
                    events.add(timerInfo);   
                }
            }
        }
        return events;
    }

     @Timeout
    public void sendJokeOfDay(Timer timer) {
        if(timer.getInfo() instanceof  EventJSON){
            JokeOfDayJSON jokeOfDay = (JokeOfDayJSON) timer.getInfo();
            editJokeOfDay(jokeOfDay);                                            
        }
    }

    public void editJokeOfDay(JokeOfDayJSON jokeOfDay) throws NoResultException{
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        jokeOfDay.setComments(getComments(jokeOfDay));
        jokeOfDay.setLikes(jokeOfDayEntity.getLikes());
        jokeOfDay.setPunchline(jokeOfDayEntity.getPunchline());
        jokeOfDay.setDate(LocalDate.now());
        this.entityManager.persist(jokeOfDayEntity);
    }

    public void jokeOfDayComment(JokeOfDayJSON jokeOfDayJSON){
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDayJSON);
        Set<PostJSON> posts = jokeOfDayJSON.getComments();
        for (PostJSON postJSON : posts) {
            Post post = null;
            try{
                post = postOps.getPostEntity(postJSON);
            }   
            catch(EJBException ex){
                postOps.editPost(postJSON);
                post = postOps.getPostEntity(postJSON);
            }
            JokeOfDayPost jokeOfDayPost = new JokeOfDayPost();
            jokeOfDayPost.setJokeOfDay(jokeOfDayEntity);
            jokeOfDayPost.setPost(post);
            entityManager.persist(jokeOfDayPost);
        }
    }

    private JokeOfDay getJokeOfDayEntity(JokeOfDayJSON jokeOfDay){
        JokeOfDay jokeOfDayJSON = entityManager.createNamedQuery("JokeOfDay.getOnDay", JokeOfDay.class)
            .setParameter("year", jokeOfDay.getDate().getYear())
            .setParameter("month", jokeOfDay.getDate().getMonth())
            .setParameter("day", jokeOfDay.getDate().getDayOfMonth()).getSingleResult();
        return jokeOfDayJSON;
    }


    public Set<JokeOfDayJSON> getJokeOfDayBetween(LocalDate startDate , LocalDate endDate){
        return this.entityManager.createNamedQuery("JokeOfDay.findJokesBetweenDates", JokeOfDayPost.class)
            .setParameter(1, startDate)
            .setParameter(2, endDate)
            .getResultList().stream().map(candidate -> {
                return getJokeOfDayInfo(new JokeOfDayJSON(candidate.getPost_Id(), null, null,
                         0, null, null));
            }).collect(Collectors.toSet());
    }

    public JokeOfDayJSON getJokeOfDayInfo(JokeOfDayJSON jokeOfDay){
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        jokeOfDay.setDate(LocalDate.ofInstant(jokeOfDayEntity.getTimestamp().toInstant(), ZoneId.systemDefault()));
        jokeOfDay.setComments(getComments(jokeOfDay));
        jokeOfDay.setLikes(jokeOfDayEntity.getLikes());
        jokeOfDay.setPunchline(jokeOfDayEntity.getPunchline());
        Sponsor sponsor = jokeOfDayEntity.getUser();
        SponsorJSON sponsorJSON = new SponsorJSON(null, sponsor.getName());
        sponsorJSON.setEmail(sponsor.getEmail());
        jokeOfDay.setUser(sponsorJSON);

        return jokeOfDay;
    }
    
    public Set<PostJSON> getComments(JokeOfDayJSON jokeOfDay){
        JokeOfDay jOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        return this.entityManager.createNamedQuery("JokeOfDay.getComments", JokeOfDayPost.class)
            .setParameter("jokeId", jOfDayEntity.getJoke_Id())
            .getResultList().stream().map(candidate -> {
                return postOps.getPostInfo(new PostJSON(candidate.getPost_Id(), null, null, 0, 0, 
                        null, null, null, null));
            }).collect(Collectors.toSet());
    }

}
