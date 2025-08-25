package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.TopicPostNotificationJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.NotificationOperations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

public class TopicPostNotificationSubResource  extends Resource{
    
    private NotificationOperations notOps;

    @Context
    private SecurityContext context;

    protected TopicPostNotificationSubResource(NotificationOperations notOps){
        this.notOps = notOps;
    }

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getTopicPostNotification(@Context SecurityContext context,
                                                @DefaultValue("false")
                                                    @MatrixParam("Seen") boolean seen,
                                                        @MatrixParam("TopicTitle") String topicTitle,
                                                            @MatrixParam("Comment") String comment,
                                                                @MatrixParam("Username") String username){
        UserJSON user = new UserJSON(username);
        TopicJSON topic = new TopicJSON(0, topicTitle, null, null, null, null);
        TopicPostJSON topicPost = new TopicPostJSON(0, comment, null, 0, 0, user, topic, null, null);                                            
        TopicPostNotificationJSON topicPostNot = new TopicPostNotificationJSON(0, topicPost, null,
                                    new UserJSON(context.getUserPrincipal().getName()), seen);
        Set<TopicPostNotificationJSON> results = notOps.getTopicPostNotificationInfo(topicPostNot);
        ListIterator<TopicPostNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        StringBuilder builder = new StringBuilder("[");
        while(iterator.hasNext())
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    @RolesAllowed({"User","Admin"})
    public Response setTopicPostNotification(@Context SecurityContext context,
                                                String topicPostNotEntity) throws JsonProcessingException{
        if(topicPostNotEntity == null){
            return Response.noContent().build();
        }
        else{
            TopicPostNotificationJSON topicPostNot = mapper.readValue(topicPostNotEntity, TopicPostNotificationJSON.class);
            topicPostNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editTopicPostNotification(topicPostNot);
            return Response.ok().build();
        }
    }

    @OPTIONS
    @RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }
}
