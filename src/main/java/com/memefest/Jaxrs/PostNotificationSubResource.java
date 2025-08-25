package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostNotificationJSON;
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

public class PostNotificationSubResource extends Resource{

    private NotificationOperations notOps;

    @Context
    private SecurityContext context;

    protected PostNotificationSubResource(NotificationOperations notOps){
        super();
        this.notOps = notOps;
    }    

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getPostNotification(@Context SecurityContext context,
                                            @MatrixParam("Comment") String comment,
                                                @MatrixParam("Username") String username,
                                                    @DefaultValue("false") @MatrixParam("Seen") boolean seen){
        UserJSON user = new UserJSON(username);
        PostJSON post = new PostJSON(0, comment, null,
         0, 0, user, null, null);
        PostNotificationJSON postNot = new PostNotificationJSON(0, post, null,
                         new UserJSON(context.getUserPrincipal().getName()), seen);
        Set<PostNotificationJSON> results = notOps.getPostNotificationInfo(postNot);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<PostNotificationJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while (iterator.hasNext()){
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
    @RolesAllowed({"User","Admin"})
    public Response setTopicPostNotification(@Context SecurityContext context,
                                                String postNotEntity) throws JsonProcessingException{
        if(postNotEntity == null){
            return Response.noContent().build();
        }
        else{
            PostNotificationJSON postNot = mapper.readValue(postNotEntity, PostNotificationJSON.class);
            postNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
            notOps.editPostNotification(postNot);
            return Response.ok().build();
        }
    }

    @DELETE
    @RolesAllowed({"User","Admin"})
    public Response removePostNotification(String postNotEntity) throws JsonProcessingException{
        PostNotificationJSON postNot = mapper.readValue(postNotEntity,PostNotificationJSON.class);
        postNot.setUser(new UserJSON(context.getUserPrincipal().getName()));
        notOps.removePostNotification(postNot);
        return Response.ok().build();
    }


    @OPTIONS
    @RolesAllowed({"User","Admin"})
    public Response getOptions(){
        return Response.ok().build();
    }
}
