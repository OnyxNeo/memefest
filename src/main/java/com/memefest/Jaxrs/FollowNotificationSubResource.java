package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.UserFollowNotificationJSON;
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

public class FollowNotificationSubResource extends Resource{
    
    private NotificationOperations notOps;

    @Context
    private SecurityContext context;

    public FollowNotificationSubResource(NotificationOperations notOps){
        super();
        this.notOps = notOps;
    }


    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getFollowNotification(@Context SecurityContext context,
                                            @DefaultValue("false")
                                                @MatrixParam("Seen") boolean seen,
                                                    @MatrixParam("username") String username){
        UserJSON user = new UserJSON(username);
        UserFollowNotificationJSON  followNot = new UserFollowNotificationJSON(null, 
                                                new UserJSON(context.getUserPrincipal().getName()),
                                                 null, user, seen); 
        followNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        Set<UserFollowNotificationJSON> results = notOps.getUserFollowNotificationInfo(followNot);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<UserFollowNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
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
        return Response.ok().entity(results).build();
    }

    @PUT
    @Consumes("application/json")
    @RolesAllowed({"User","Admin"})
    public Response setFollowNotification(@Context SecurityContext context,
                                            String followNotEntity)throws JsonProcessingException{
        if(followNotEntity== null){
            return Response.noContent().build();
        }
        else{
            UserFollowNotificationJSON followNot = mapper.readValue(followNotEntity, UserFollowNotificationJSON.class);
            followNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editUserFollowNotification(followNot);
            return Response.ok().build();
        }
    }

    @DELETE
    @Consumes("application/json")
    public Response removeFollowNotification(@Context SecurityContext context,
                                                String followNotEntity) throws JsonProcessingException{
        UserFollowNotificationJSON followNot = mapper.readValue(followNotEntity, UserFollowNotificationJSON.class);
        followNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        notOps.removeUserFollowNotification(followNot);
        return Response.ok().build();
    }


    @OPTIONS
    @RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }
}
