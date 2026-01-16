package com.memefest.Jaxrs;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.EventOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Event")
@PermitAll
public class EventResource extends Resource{
    
    @Inject
    private EventOperations eventOps;

    @Context
    private SecurityContext context;

    @OPTIONS
    //@Path("/")
    public Response eventOptions(){
        return Response.ok().build();
    } 
    
    @DELETE
    @Path("/{EventId: \\d+}")
    public Response removeEvent(@Context SecurityContext context,
                                    @PathParam("EventId") Long eventId){
        
        eventOps.removeEvent(new EventJSON(eventId, null, null,
                                         null, null, null,
                                             null, null, null, 
                                             null, null,
                                              null,
                                              new UserJSON(
                                                //context.getUserPrincipal().getName()),
                                              "lando"),
                                                null, null, 0));
        return Response.ok().build();
    }


    @GET
    @Path("/{EventId: \\d+}")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response getEvent(@PathParam("EventId") Long eventId)throws JsonProcessingException{
        EventJSON event = eventOps.getEventInfo(new EventJSON(eventId, null, null,
                 null, null, null, null, null,
                     null, null, null, null, null, null, null, 0));
        eventExtendedView();
        return Response.ok().entity(mapper.writeValueAsString(event)).build();    
    }


    @GET
    //@Path("/Search")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getEvent(@PathParam("Title") String title,
                                @PathParam("Category") String category,
                                    @PathParam("Venue") String venue){
        UserJSON user = new UserJSON("lando");
        EventJSON event = null;
        if(title != null || category != null || venue != null)          
            event = new EventJSON(null, title, null, null, 
        null, null, null, null, null,
         null, null, venue, user, null, 
         null, 0);
         if(category != null){
            Set<CategoryJSON> categories = new HashSet<CategoryJSON>();
            CategoryJSON cat = new CategoryJSON(null, category, null, null, null);
            categories.add(cat);
            event.setCategories(categories);    
         }
        Set<EventJSON> results = eventOps.searchEvents(event);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<EventJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()){
            try{
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                String entity = mapper.writeValueAsString(iterator.next());
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
            event.setPostedBy(new UserJSON(
                "lando"));
                // /context.getUserPrincipal().getName()));
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
