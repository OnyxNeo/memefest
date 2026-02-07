package com.memefest.Services.Impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.memefest.DataAccess.JokeOfDay;
import com.memefest.DataAccess.JokeOfDayPost;
import com.memefest.DataAccess.JokeOfDayScheduleBackup;
import com.memefest.DataAccess.Post;
import com.memefest.DataAccess.Sponsor;
import com.memefest.DataAccess.JSON.CommentJSON;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.JokeOfDayJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.SponsorJSON;
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.JokeOfDayOperations;
import com.memefest.Services.PostOperations;

import io.jsonwebtoken.lang.Collections;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.ScheduleExpression;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "JokeOfDayService")
public class JokeOfDayService implements JokeOfDayOperations{

    @EJB
    private PostOperations postOps;

    @Resource
    private TimerService timerService;

    @EJB
    private DataSourceOps datasourceOps;

    //@TransactionScoped
    private EntityManager entityManager;

    //@PostActivate
    @PostConstruct
    public void init(){
        this.entityManager = datasourceOps.getEntityManagerFactory().createEntityManager();
    }

    @PreDestroy
    //@PrePassivate
    public void destroy(){
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public JokeOfDayJSON editJokeOfDay(JokeOfDayJSON jokeOfDay) throws NoResultException{
        try{
            JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
            jokeOfDay.setComments(getComments(jokeOfDay));
            jokeOfDay.setLikes(jokeOfDayEntity.getLikes());
            jokeOfDay.setPunchline(jokeOfDayEntity.getPunchline());
            jokeOfDay.setDate(LocalDate.now());
            this.entityManager.persist(jokeOfDayEntity);
        }
        catch(NoResultException ex){
            JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
            jokeOfDay.setComments(getComments(jokeOfDay));
            jokeOfDay.setLikes(jokeOfDayEntity.getLikes());
            jokeOfDay.setPunchline(jokeOfDayEntity.getPunchline());
            jokeOfDay.setDate(LocalDate.now());
            this.entityManager.persist(jokeOfDayEntity);
            this.entityManager.flush();
            jokeOfDay.setJokeId(jokeOfDayEntity.getJoke_Id());
        }
        return jokeOfDay;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public JokeOfDayJSON jokeOfDayComment(JokeOfDayJSON jokeOfDayJSON){
        this.entityManager.joinTransaction();        
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDayJSON);
        Set<CommentJSON> posts = jokeOfDayJSON.getComments();
        Set<CommentJSON> postsWithIds = new HashSet<CommentJSON>();
        for (CommentJSON postJSON : posts) {
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
            entityManager.flush();
            postJSON.setPostId(post.getPost_Id());
            postsWithIds.add(postJSON);
        }
        jokeOfDayJSON.setComments(postsWithIds);
        return jokeOfDayJSON;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private JokeOfDay getJokeOfDayEntity(JokeOfDayJSON jokeOfDayJSON){
        JokeOfDay jokeOfDay = null;
        if( jokeOfDayJSON != null && jokeOfDayJSON.getJokeId() != null && jokeOfDayJSON.getJokeId() != 0){
            jokeOfDay = this.entityManager.find(JokeOfDay.class, jokeOfDayJSON.getJokeId());
            if (jokeOfDay == null)
                throw new NoResultException();  
            else
                return jokeOfDay;
        }
        else if(jokeOfDayJSON.getDate() != null){
            jokeOfDay =  entityManager.createNamedQuery("JokeOfDay.getOnDay", JokeOfDay.class)
            /* .setParameter("year", jokeOfDay.getDate().getYear())
            .setParameter("month", jokeOfDay.getDate().getMonth())
            .setParameter("day", jokeOfDay.getDate().getDayOfMonth()).getSingleResult();
            */
                .setParameter("date", Date.from(jokeOfDayJSON.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())).getSingleResult();
            return jokeOfDay;
        }
        else
            throw new NoResultException();
    }


    public Set<JokeOfDayJSON> getJokeOfDayBetween(LocalDate startDate , LocalDate endDate){
        return this.entityManager.createNamedQuery("JokeOfDay.findJokesBetweenDates", JokeOfDayPost.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList().stream().map(candidate -> {
                return getJokeOfDayInfo(new JokeOfDayJSON(candidate.getPost_Id(), null, null,
                         0, null, null));
            }).collect(Collectors.toSet());
    }

    public JokeOfDayJSON getJokeOfDay(){
        JokeOfDayJSON jokeOfDay = new JokeOfDayJSON(null, null, LocalDate.now(), 0, null, null);
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        jokeOfDay.setJokeId(jokeOfDayEntity.getJoke_Id());
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

    public JokeOfDayJSON getJokeOfDayInfo(JokeOfDayJSON jokeOfDay){
        JokeOfDay jokeOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        jokeOfDay.setJokeId(jokeOfDayEntity.getJoke_Id());
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
    
    public Set<CommentJSON> getComments(
        JokeOfDayJSON jokeOfDay){
        JokeOfDay jOfDayEntity = getJokeOfDayEntity(jokeOfDay);
        return this.entityManager.createNamedQuery("JokeOfDay.getComments", Post.class)
            .setParameter("jokeId", jOfDayEntity.getJoke_Id())
            .getResultList().stream().map(candidate -> {
                return postOps.getPostInfo(new PostJSON(candidate.getPost_Id(), null, null, 0, 0, 
                        null, null, null, null));
            }).map(candidate ->{
                return new CommentJSON(candidate.getPostId(),
                    candidate.getComment(), 
                    candidate.getCreated(), 
                    candidate.getUpvotes(), candidate.getDownvotes(),
                    candidate.getUser(), 
                    candidate.getCategories(), 
                     null, candidate.getTaggedUsers());
            }).collect(Collectors.toSet());
    }   

}
