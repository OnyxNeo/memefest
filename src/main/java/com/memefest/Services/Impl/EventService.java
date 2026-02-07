package com.memefest.Services.Impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.Event;
import com.memefest.DataAccess.EventCategory;
import com.memefest.DataAccess.EventCategoryId;
import com.memefest.DataAccess.Image;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventPostJSON;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.VideoJSON;
import com.memefest.Services.CategoryOperations;
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.EventOperations;
import com.memefest.Services.ImageOperations;
import com.memefest.Services.NotificationOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.UserOperations;
import com.memefest.Services.VideoOperations;
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
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.transaction.TransactionScoped;


@TransactionManagement(TransactionManagementType.CONTAINER)
//add event Categories in next iteration
@Stateless(name = "EventService")
public class EventService implements EventOperations{

    @EJB
    private UserOperations userOperations;

    @EJB
    private CategoryOperations catOperations;

    @EJB
    private ImageOperations imageOps;

    @EJB
    private VideoOperations videoOps;

    @EJB 
    private PostOperations postOps;

    @EJB
    private NotificationOperations notOps;

    @Resource
    private TimerService timerService;

    @EJB
    private DataSourceOps datasourceOps;

    private EntityManagerFactory factory;

    @TransactionScoped
    private EntityManager entityManager;

    //@PostActivate
    @PostConstruct
    public void init(){
        this.entityManager = datasourceOps.getEntityManagerFactory().createEntityManager();
    }

    @PreDestroy
    //@PrePassivate
    public void destroy(){
        factory.close();
        entityManager.close();
    }  

    public void createScheduledEvent(EventJSON eventInfo, LocalDateTime dueDate){
        ScheduleExpression schedule = new ScheduleExpression().year(dueDate.getYear()).dayOfMonth(dueDate.getDayOfMonth())
                                        .hour(dueDate.getHour()).minute(dueDate.getMinute());
        TimerConfig timerConf = new TimerConfig(eventInfo, true);       
        timerService.createCalendarTimer(schedule, timerConf);
    }

    
    @Timeout
    public void sendEvent(Timer timer) {
        if(timer.getInfo() instanceof  EventJSON){
            EventJSON eventInfo = (EventJSON) timer.getInfo();
            createEvent(eventInfo);                                            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private EventJSON editEventCategory(EventJSON event){
        try{    
            Event eventEntity = getEventEntity(event);
            if(event.getCategories() != null)
                event.getCategories().forEach(category ->{
                    try{
                        Category cat = catOperations.getCategoryEntity(category);
                        createEventCategory(eventEntity, cat);
                    }
                    catch(NoResultException | EJBException ex){
                        try{
                            catOperations.editCategory(category);
                            Category cat = catOperations.getCategoryEntity(category);
                            createEventCategory(eventEntity, cat);
                        }
                        catch(NoResultException | EJBException exp){ 
                        }
                    }
                });
            if(event.getCanceledCategories() != null)
                event.getCanceledCategories().forEach( category ->{
                    try{
                        Category cat = catOperations.getCategoryEntity(category);
                        removeEventCategory(eventEntity, cat);
                    }
                    catch(NoResultException | EJBException ex){
                    }
                });
            return event;
        }
        catch(NoResultException ex){
            editEvent(event);
            return editEventCategory(event);
        }
    }

    private void removeEventCategory(Event event, Category category){
        EventCategoryId eventCatId = new EventCategoryId();
        eventCatId.setCat_Id(category.getCat_Id());
        eventCatId.setEvent_Id(event.getEvent_Id());
        entityManager.remove(entityManager.find(EventCategory.class, eventCatId));
    }

    private void createEventCategory(Event event, Category category){
        EventCategory eventCat = new EventCategory();
        eventCat.setCat_Id(category.getCat_Id());
        eventCat.setEvent_Id(event.getEvent_Id());
        entityManager.persist(eventCat);
    }

    private Set<CategoryJSON> getCategories(EventJSON event){
        if(event == null || event.getEventID() == null)
            return null;
        try{
            return entityManager.createNamedQuery("EventCategory.findByEventId", Category.class)
                                    .setParameter("eventId", event.getEventID()).getResultList().stream().map(category ->{
                                        return new CategoryJSON(category.getCat_Id(), category.getCat_Name(), null, null, null);
                                    }).collect(Collectors.toSet());
        }
        catch(NoResultException ex){
            return null;
        }
    }

    public void cancelScheduledEvent(EventJSON eventInfo) {
        timerService.getTimers().stream().filter(timer ->{
            if(timer.getInfo() instanceof EventJSON){
                EventJSON timerInfo = (EventJSON) timer.getInfo();
                if(timerInfo.getEventID().equals(eventInfo.getEventID()) || timerInfo.getEventTitle().equalsIgnoreCase(eventInfo.getEventTitle())){
                    return true;
                }
                return false;
            }
            else return false;
        }).forEach(timer ->  timer.cancel());
    }

    public Map<EventJSON, LocalDateTime> getScheduledEvents(EventJSON event){
        Map<EventJSON, LocalDateTime> events = new HashMap<EventJSON, LocalDateTime>(); 
        for(Timer timer : timerService.getAllTimers()){
            if(timer.getInfo()instanceof EventJSON){
                EventJSON timerInfo = (EventJSON) timer.getInfo();
                if(timerInfo.getEventID().equals(event.getEventID()) || timerInfo.getEventTitle().equalsIgnoreCase(event.getEventTitle())){
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

    public void editScheduledEvent(Map<EventJSON, LocalDateTime> scheduledEvents){
        scheduledEvents.entrySet().forEach(candidate -> {
            EventJSON event = candidate.getKey();
            if(event.isCanceled()){
                cancelScheduledEvent(event);
            }
            else{
                createScheduledEvent(event, candidate.getValue());
            }
            });
    }
/* 
    public Set<EventJSON> searchEvents(EventJSON event){
        Set<EventJSON> events = new HashSet<EventJSON>();
        if(event != null && event.getEventTitle() != null){
            Stream<Event> queryResults = entityManager.createNamedQuery("Event.searchEventsByTitle", Event.class)
                            .setParameter(1, event.getEventTitle()).getResultStream();
            events.addAll(queryResults.map(eventEntity ->{
                    return getEventInfo(new EventJSON(eventEntity.getEvent_Id(),null, null,null,null,null,null,null,null,null,null,null,null));
                }).collect(Collectors.toSet()));
        }
        /* 
        if(event != null && event.getEventDescription() != null){
            Query query = entityManager.createNamedQuery("Event.searchEventsByEventDescription");
            query.setParameter("eventTitle", "%" + event.getEventTitle() + "%");
            Set<Event> queryResults = (Set<Event>)query.getResultList();
            events.addAll(queryResults.stream().map(eventEntity ->{
                    return getEventInfo(new EventJSON(eventEntity.getEvent_Id(),null, null,null,null,null,null,null,null,null,null,null));
                }).collect(Collectors.toSet()));    
        }
        
        return events;
    }
    */

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //throw a custom exception to show object was not created
    //add logic to run as specified principals for changing some properties for events
    public EventJSON editEvent(EventJSON event){
        if(event == null || event.getEventTitle() == null && event.getEventID() == 0)
            throw new NoResultException();
        try{
            Event eventEntity = getEventEntity(event);
            //LocalDateTime eventDate = LocalDateTime.ofInstant(eventEntity.getDate_Posted().toInstant(), ZoneId.systemDefault());
            if(event.getEventTitle() != null)
                eventEntity.setEvent_Title(event.getEventTitle()); 
            if(event.getEventVenue() != null)
            eventEntity.setEvent_Venue(event.getEventVenue()); 
            if(event.getEventPin() != null)
                eventEntity.setEvent_Pin(event.getEventPin());
            if(event.getEventDate() != null)
                eventEntity.setEvent_Date(Date.from(event.getEventDate().atZone(ZoneId.systemDefault()).toInstant()));
            if(event.getEventDescription() != null)
                eventEntity.setEvent_Description(event.getEventDescription()); 
            if (event.getPosts() != null){
                for(EventPostJSON post : event.getPosts()){
                    postOps.editEventPost(post);
                }
            }
            editEventCategory(event);
            removeEvent(event);
            return event;
            /* 
            for(ImageJSON image : event.getPosters()){
                imageOps.editImage(image);
            }
            for(VideoJSON video : event.getClips()){
                videoOps.editVideo(video);
            }
            */
        }   
        catch(NoResultException | EJBException ex){
            event = createEvent(event);
            return event;
        }
        
    }


    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public EventJSON getEventInfo(EventJSON event)throws NoResultException{
        Event eventEntity = getEventEntity(event);
        Set<CategoryJSON> categories = getCategories(event);
        //User user = userOperations.getUserEntity(event.getPostedBy());
        UserJSON postedBy = new UserJSON(eventEntity.getPosted_By(), null, null, 0, 
                        false, null, null, null,
                             null, null, null);
        LocalDateTime eventDate = LocalDateTime.ofInstant(eventEntity.getEvent_Date().toInstant(), ZoneId.systemDefault());
        LocalDateTime datePosted = LocalDateTime.ofInstant(eventEntity.getDate_Posted().toInstant(), ZoneId.systemDefault());
        Long eventId = eventEntity.getEvent_Id();
        String eventPin = eventEntity.getEvent_Pin();
        String eventTitle = eventEntity.getEvent_Title();
        String eventDesc = eventEntity.getEvent_Description();
        String eventVenue = eventEntity.getEvent_Venue();
        Set<ImageJSON> eventImages = new HashSet<ImageJSON>();
        eventImages = eventEntity.getImages().stream().map(imgInst  -> {
                    Image imageInfo = imgInst.getImage(); 
                    return new  ImageJSON(imgInst.getImg_Id(), 
                imageInfo.getImg_Path(), imageInfo.getImg_Title());
            }).collect(Collectors.toSet());
        Set<EventPostJSON> posts = new HashSet<EventPostJSON>();
        posts = eventEntity.getPosts().stream().map(postInst -> {
                return new EventPostJSON(postInst.getPost_Id(), null, null, 
                        0, 0, null, new EventJSON(postInst.getEvent().getEvent_Id(),
                             null, null, null,null,null,null, 
                                null, null,null, null,null, null,null,null, 0), null,null, null);
            }).collect(Collectors.toSet());
        Set<VideoJSON> eventVideo = new HashSet<VideoJSON>();
        /* 
        eventVideo = eventEntity.getVideos().stream().map(vidInst -> {
                        Video vidInfo = vidInst.get
                        return new VideoJSON(vidInst.getVid_Id(), vidInst.getVid_Path(), vidInst.getVid_Title());
            }).collect(Collectors.toSet());
        */
        EventJSON eventJSON = new EventJSON(eventId, eventTitle, eventDesc, eventPin,eventDate,datePosted,eventVideo, eventImages,
                                        posts,null, null, eventVenue,postedBy, categories, null, 0); 
        return eventJSON;
    }   

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Event getEventEntity(EventJSON event) throws NoResultException{
        if(event == null || event.getEventTitle()== null && event.getEventID() == null)
            throw new NoResultException();
        /*if(event.getEventID() != 0){
            Event eventEntity = this.entityManager.find(Event.class, event.getEventID());
            if(eventEntity == null)
                throw new NoResultException();
            else
                return eventEntity;
        }
        else if(event.getEventTitle() !=  null){
            return this.entityManager.createNamedQuery("Event.getEventByTitle", Event.class)
                        .setParameter(1, event.getEventTitle()).getSingleResult();
        }
        else
            throw new NoResultException();
        */
        Event foundEvent = null;
        if(event.getEventID() != null) {
            foundEvent = (Event)this.entityManager.find(Event.class, event.getEventID());
            if (foundEvent == null){
                throw new NoResultException();
            }
            return foundEvent;          
        }
        else if(event.getEventTitle() != null && event.getEventID() == null){   
            foundEvent = (Event) this.entityManager.createNamedQuery("Event.getEventByTitle", Event.class)
                                .setParameter(1,event.getEventTitle()).getSingleResult();
            return foundEvent; 
        }
        else throw new NoResultException();
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    //throw a custom exception to show object was not created
    private EventJSON createEvent(EventJSON event){
            User postedBy = userOperations.getUserEntity(event.getPostedBy());
            Event eventEntity = new Event();
            eventEntity.setUser(postedBy);
            eventEntity.setEvent_Title(event.getEventTitle());
            eventEntity.setEvent_Description(event.getEventDescription());
            eventEntity.setEvent_Date(Date.from(event.getEventDate().atZone(ZoneId.systemDefault()).toInstant()));
            eventEntity.setEvent_Venue(event.getEventVenue());
            eventEntity.setDate_Posted(Date.from(Instant.now()));
            eventEntity.setEvent_Pin(event.getEventPin());
            entityManager.persist(eventEntity);
            if(event.getCategories() != null)
                event.getCategories().stream().forEach(category ->{
                    try{
                        Category cat = catOperations.getCategoryEntity(category);
                        createEventCategory(eventEntity, cat);
                    }
                    catch(NoResultException ex){
                        catOperations.editCategory(category);
                        Category cat = catOperations.getCategoryEntity(category);
                        createEventCategory(eventEntity, cat);
                }
            });
            this.entityManager.flush();
            event.setEventID(eventEntity.getEvent_Id());
            return event;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removeEvent (EventJSON event){
        if(event.isCanceled()){
            try{
                Event eventEntity = getEventEntity(event);
                if(eventEntity!= null){
                    entityManager.remove(eventEntity);
                }
            }
            catch(NoResultException ex){
                return;
            }
        }
    }

    //@TransactionAttribute.
    public Set<EventJSON> searchEvents(EventJSON event)throws NoResultException, EJBException{
        List<Event> events = new ArrayList<Event>();
        if(event == null){
            events = this.entityManager.createNamedQuery("Event.getAll", Event.class).getResultList();
        }
        else if(event.getEventTitle() != null && (event.getPostedBy()== null || (event.getPostedBy() != null && event.getPostedBy().getUsername() == null 
                                && event.getPostedBy().getUserId() == null)))
            events = this.entityManager.createNamedQuery("Event.searchByTitle", Event.class).setParameter(1, event.getEventTitle())
                .getResultList();
        else if(event.getPostedBy() != null && event.getPostedBy().getUserId() != null && event.getEventVenue() == null && event.getEventTitle() == null)
            events = this.entityManager.createNamedQuery("Event.searchByPostedBy", Event.class).setParameter(1, event.getPostedBy().getUserId())
                .getResultList();
        else if(event.getPostedBy() != null && event.getPostedBy().getUserId() == null && event.getPostedBy().getUsername() != null && event.getEventVenue() == null){
            User owner = userOperations.getUserEntity(new UserJSON(event.getPostedBy().getUsername()));
            events = this.entityManager.createNamedQuery("Event.searchByPostedBy", Event.class).setParameter(1, owner.getUserId())
                .getResultList();
        }
        else if(event.getEventVenue() != null && (event.getPostedBy()!= null && event.getPostedBy().getUsername() == null && event.getPostedBy().getUserId() == null ||
                                     event.getPostedBy() == null) && event.getEventTitle() == null)
             events = this.entityManager.createNamedQuery("Event.searchByVenue", Event.class).setParameter(1, event.getEventVenue())
                .getResultList();
        else if(event.getEventVenue() != null && event.getEventTitle() != null && (event.getPostedBy() != null &&  event.getPostedBy().getUsername() == null && event.getPostedBy().getUserId() == null|| event.getPostedBy() == null)) 
            events = this.entityManager.createNamedQuery("Event.searchByVenue&Title", Event.class)
                .setParameter(1, event.getEventVenue())
                .setParameter(2, event.getEventTitle())
                .getResultList();
        else if(event.getEventVenue() != null && (event.getPostedBy() != null && event.getPostedBy().getUserId() != null) && event.getEventTitle() == null)
            events = this.entityManager.createNamedQuery("Event.searchByVenue&PostedBy", Event.class)
                .setParameter(1, event.getEventVenue())
                .setParameter(2, event.getPostedBy().getUserId())
                .getResultList();
        else if(event.getEventVenue() != null && event.getPostedBy() != null && event.getPostedBy().getUserId() == null && event.getPostedBy().getUsername() != null && event.getEventTitle() == null){
            User owner = userOperations.getUserEntity(new UserJSON(event.getPostedBy().getUsername()));
            events = this.entityManager.createNamedQuery("Event.searchByVenue&PostedBy", Event.class)
                .setParameter(1, event.getEventVenue())
                .setParameter(2, owner.getUserId())
                .getResultList();
        }
        else if(event.getPostedBy()!= null && event.getPostedBy().getUserId() != null && event.getEventTitle() != null)
            events = this.entityManager.createNamedQuery("Event.searchByPostedBy&Title", Event.class)
                .setParameter(2, event.getPostedBy().getUserId())
                .setParameter(1, event.getEventTitle())
                .getResultList();
        else if(event.getPostedBy() != null && event.getPostedBy().getUserId() == null && event.getPostedBy().getUsername() != null && event.getEventTitle() != null){
            User owner = userOperations.getUserEntity(new UserJSON(event.getPostedBy().getUsername()));
            events = this.entityManager.createNamedQuery("Event.searchByPostedBy&Title", Event.class)
                .setParameter(2, owner.getUserId())
                .setParameter(1, event.getEventTitle())
                .getResultList();
        }
        else
            return Collections.singleton(getEventInfo(event));

        return events.stream().map(eventEntity ->{
            Set<CategoryJSON> categories = getCategories(new EventJSON(eventEntity.getEvent_Id(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0));

            UserJSON postedBy = userOperations.getUserInfo(new UserJSON(eventEntity.getPosted_By(), null, null, 0,
                                                                false, null, null, null, null, null, null));
            //UserJSON postedBy = new UserJSON(eventEntity.getPosted_By(), postedByEntity.getUsername());
            LocalDateTime eventDate = LocalDateTime.ofInstant(eventEntity.getEvent_Date().toInstant(), ZoneId.systemDefault());
            LocalDateTime datePosted = LocalDateTime.ofInstant(eventEntity.getDate_Posted().toInstant(), ZoneId.systemDefault());
            Long eventId = eventEntity.getEvent_Id();
            String eventPin = eventEntity.getEvent_Pin();
            String eventTitle = eventEntity.getEvent_Title();
            String eventDesc = eventEntity.getEvent_Description();
            String eventVenue = eventEntity.getEvent_Venue();
            Set<ImageJSON> eventImages = new HashSet<ImageJSON>();
            eventImages = eventEntity.getImages().stream().map(imgInst  -> {
                        Image imageInfo = imgInst.getImage(); 
                        return new  ImageJSON(imgInst.getImg_Id(), 
                    imageInfo.getImg_Path(), imageInfo.getImg_Title());
                }).collect(Collectors.toSet());
            Set<EventPostJSON> posts = new HashSet<EventPostJSON>();
            posts = eventEntity.getPosts().stream().map(postInst -> {
                    return new EventPostJSON(postInst.getPost_Id(), null, null, 
                            0, 0, null, new EventJSON(postInst.getEvent().getEvent_Id(),
                                 null, null, null,null,null,null, 
                                    null, null,null, null,null, null,null, null, 0), null, null,null);
                }).collect(Collectors.toSet());
            Set<VideoJSON> eventVideo = new HashSet<VideoJSON>();
        /* 
        eventVideo = eventEntity.getVideos().stream().map(vidInst -> {
                        Video vidInfo = vidInst.get
                        return new VideoJSON(vidInst.getVid_Id(), vidInst.getVid_Path(), vidInst.getVid_Title());
            }).collect(Collectors.toSet());
        */

        return new EventJSON(eventId, eventTitle, eventDesc, eventPin,eventDate,datePosted,eventVideo, eventImages,
                                        posts,null, null, eventVenue,postedBy, categories, null, 0);
        }).collect(Collectors.toSet());  
    }

    /*
    LocalDateTime.ofInstant(topicEntity.getCreated().toInstant(), ZoneId.systemDefault()) 
    private Set<EventImage> createEventImageEntities(EventJSON  event){
        Set<ImageJSON> eventImages = event.getPosters();
        Set<EventImage> eventImageEntities = new HashSet<EventImage>();

    }

    private EventImage createEventImageEntity(int eventId, int imageId){

    }



    public Set<EventJSON> getEventsByUser(UserJSON user){

    }

    public Set<EventJSON> getEventsBetweenDates(LocalDateTime startDateTime, LocalDateTime lastDateTime){
        return entityManager.createNamedQuery("EventJSON.findByDates", EventJSON.class)
                   .setParameter("startDateTime", startDateTime)
                   .setParameter("lastDateTime", lastDateTime)
                   .getResultList();
    }
    
    public EventJSON getEventInfo(EventJSON event){

    }
    
     public void getEventsByCategories(){
     }

     public void 
    */
}
