package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventNotificationJSON;
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

//@RolesAllowed({"User", "Admin"})
public class EventNotificationSubResource extends Resource{
    
    protected NotificationOperations notOps;

    public EventNotificationSubResource(NotificationOperations notOps){
        super();
        this.notOps = notOps;
    }

    @Context 
    private SecurityContext context;

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response getEventNotification(@Context SecurityContext context,
                                            @DefaultValue("false") 
                                                @MatrixParam("Seen") boolean seen,
                                                    @MatrixParam("EventTitle") String title){
        EventJSON event = new EventJSON(0, title, null, 
                                        null, null, null, 
                                        null, null, null, null,
                                         null, null, null, null,
                                          null);
        EventNotificationJSON eventNot = new EventNotificationJSON(0, null, event, 
                                new UserJSON(context.getUserPrincipal().getName()), seen);   
        Set<EventNotificationJSON> results = notOps.getEventNotificationInfo(eventNot);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<EventNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()){   
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
        }
        builder.append("]");
        return Response.ok().entity(results).build();
    }

    // maybe some should be form params
    @PUT
    //@RolesAllowed({"User","Admin"})
    @Consumes("application/json") 
    public Response setEventNotification(@Context SecurityContext context,
                                            String eventNotEntity) throws JsonProcessingException{    
        if(eventNotEntity == null)
            return Response.noContent().build();
        
        else{
            EventNotificationJSON eventNot = mapper.readValue(eventNotEntity, EventNotificationJSON.class);
            eventNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editEventNotification(eventNot);
            return Response.ok().build();
        }
    }

    @DELETE
    @Consumes("application/json")
    public Response removeEventNotification(@Context SecurityContext context,
                                                String eventNotEntity) throws JsonProcessingException{
        EventNotificationJSON eventNot = mapper.readValue(eventNotEntity, EventNotificationJSON.class);
        eventNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        notOps.removeEventNotification(eventNot);
        return Response.ok().build();
    }

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }

}
