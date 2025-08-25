package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.EventPostNotificationJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.NotificationOperations;

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

public class EventPostNotificationSubResource extends Resource{
    
    private NotificationOperations notOps;

    protected EventPostNotificationSubResource(NotificationOperations notOps){
        super();
        this.notOps = notOps;
    }

    @Context
    private SecurityContext context;

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response getEventPostNotification(@Context SecurityContext context,
                                                @DefaultValue("false")
                                                    @MatrixParam("seen") boolean seen,
                                                        @MatrixParam("EventTitle") String topicTitle,
                                                            @MatrixParam("Comment") String comment,
                                                                @MatrixParam("Username") String username){
        EventPostNotificationJSON eventPostNot = new EventPostNotificationJSON(0, null, null,
                                    new UserJSON(context.getUserPrincipal().getName()), seen);
        eventPostNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        Set<EventPostNotificationJSON> results = notOps.getEventPostNotificationInfo(eventPostNot);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<EventPostNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator(); 
        while(iterator.hasNext()){
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                continue;
            } 
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response setEventPostNotification(@Context SecurityContext context,
                                            String eventPostNotEntity)throws JsonProcessingException{
        if(eventPostNotEntity == null){
            return Response.noContent().build();
        }
        else{
            EventPostNotificationJSON eventPostNot = mapper.readValue(eventPostNotEntity, EventPostNotificationJSON.class);
            eventPostNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editEventPostNotification(eventPostNot);
            return Response.ok().build();
        }
    }

    @DELETE
    @Consumes("application/json")
    public Response removeEventNotification(@Context SecurityContext context,
                                                String eventNotEntity) throws JsonProcessingException{
        EventPostNotificationJSON eventNot = mapper.readValue(eventNotEntity, EventPostNotificationJSON.class);
        eventNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        notOps.removeEventPostNotification(eventNot);
        return Response.ok().build();
    }


    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }
}
