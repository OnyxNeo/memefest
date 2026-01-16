package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.TopicFollowNotificationJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.NotificationOperations;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

public class TopicFollowNotificationSubResource extends Resource{
    
    private NotificationOperations notOps;

    @Context
    private SecurityContext context;

    public TopicFollowNotificationSubResource(NotificationOperations notOps){
        this.notOps = notOps;
    }

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getTopicFollowNotification(@DefaultValue("false") @MatrixParam("Seen") boolean seen,
                                                    @MatrixParam("TopicTitle") String title){
        TopicJSON topic = new TopicJSON(null, title, null, null, null, null);
        TopicFollowNotificationJSON topicFollowNot = new TopicFollowNotificationJSON(null, topic, null, 
                            new UserJSON(context.getUserPrincipal().getName()), seen); 
        Set<TopicFollowNotificationJSON> results = notOps.getTopicFollowNotificationInfo(topicFollowNot);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<TopicFollowNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext())
            try{
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    @RolesAllowed({"User","Admin"})
    public Response setTopicFollowNotification(@Context SecurityContext context,
                                                String topicFollowNotEntity) throws JsonProcessingException{
        if(topicFollowNotEntity== null){
            return Response.noContent().build();
        }
        else{
            TopicFollowNotificationJSON topicFollowNot = mapper.readValue(topicFollowNotEntity, TopicFollowNotificationJSON.class);
            topicFollowNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editTopicFollowNotification(topicFollowNot);
            return Response.ok().build();
        }
    }

    @OPTIONS
    @RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }

    @DELETE
    @Consumes("application/json")
    public Response removeTopicPostNotification(String topicFollowNotEntity) throws JsonProcessingException{
        if(topicFollowNotEntity == null){
            return Response.noContent().build();
        }
        else{
            TopicFollowNotificationJSON topicFollowNot = mapper.readValue(topicFollowNotEntity, TopicFollowNotificationJSON.class);
            topicFollowNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.removeTopicFollowNotification(topicFollowNot);
            return Response.ok().build();
        }
    }
}
