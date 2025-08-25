package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.EventOperations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Event")
public class EventResource extends Resource{
    
    @Inject
    private EventOperations eventOps;

    @Context
    private SecurityContext context;

    @OPTIONS
    public Response eventOptions(){
        return Response.ok().build();
    } 
    
    @DELETE
    @Path("/{EventId: \\d+}")
    public Response removeEvent(@Context SecurityContext context,
                                    @PathParam("EventId") int eventId){
        
        eventOps.removeEvent(new EventJSON(eventId, null, null,
                                         null, null, null,
                                             null, null, null, 
                                             null, null,
                                              null,
                                              new UserJSON(context.getUserPrincipal().getName()),
                                               null, null));
        return Response.ok().build();
    }


    @GET
    @Path("/{EventId: \\d+}")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getEvent(@PathParam("EventId") int eventId)throws JsonProcessingException{
        EventJSON event = eventOps.getEventInfo(new EventJSON(eventId, null, null,
                 null, null, null, null, null,
                     null, null, null, null, null, null, null));
        return Response.ok().entity(mapper.writeValueAsString(event)).build();    
    }


    @GET
    @Path("/Search")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getEvent(@MatrixParam("Title") String title,
                                @MatrixParam("Username") String username){
        UserJSON user = new UserJSON(username);
        EventJSON event = new EventJSON(0, title, null, null, 
        null, null, null, null, null,
         null, null, null, user, null, 
         null);
        Set<EventJSON> results = eventOps.searchEvents(event);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<EventJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
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
        return Response.ok().entity(builder.toString()).build();
    }

    @OPTIONS
    @Path("/Search")
    public Response searchOptions(){
        return Response.ok().build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response setEvent(String eventEntity)throws JsonProcessingException{
            EventJSON event = mapper.readValue(eventEntity, EventJSON.class);
            event.setPostedBy(new UserJSON(context.getUserPrincipal().getName()));
            eventOps.editEvent(event);
            event = eventOps.getEventInfo(event);
            eventEntity = mapper.writeValueAsString(event);
            return Response.ok().entity(eventEntity).build();
    }   

    @OPTIONS
    @Path("/{EventId: \\d+}")
    public Response eventIdOptions(){
        return Response.ok().build();
    }

}
