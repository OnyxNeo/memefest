package com.memefest.Jaxrs;

import com.memefest.Services.NotificationOperations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/Notification")
@RequestScoped
public class NotificationResource extends Resource{
    
    @Inject
    protected NotificationOperations notOps;

    @Path("/Event")
    public EventNotificationSubResource eventNotificationSubResource(){
        return new EventNotificationSubResource(notOps);
    }

    @Path("/Follow/Topic")
    public TopicFollowNotificationSubResource topicFollowNotificationSubResource(){
        return new TopicFollowNotificationSubResource(notOps);        
    }

    @Path("/Follow/User")
    public FollowNotificationSubResource followNotificationSubResource(){
        return new FollowNotificationSubResource(notOps);
    }

    @Path("/Post")
    public PostNotificationSubResource postNotificationSubResource(){
        return new PostNotificationSubResource(notOps);
    }

    @Path("/Post/Event")
    public EventPostNotificationSubResource eventPostNotificationSubResource(){
        return new EventPostNotificationSubResource(notOps);
    }

    @Path("/Post/Topic")
    public TopicPostNotificationSubResource topicPostNotificationSubResource(){
        return new TopicPostNotificationSubResource(notOps);
    }   


    @OPTIONS
    @RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }
}
