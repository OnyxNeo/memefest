package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.Services.TopicOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/Topic")
@PermitAll
//@RolesAllowed({"User","Admin"})
public class TopicResource extends Resource{
    
    @Inject
    private TopicOperations topicOps;

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response topicOptions(){
        return Response.ok().build();
    }

    @PUT
    //@RolesAllowed({"Admin","User"})
    @Consumes("application/json") 
    @Produces("application/json")
    public Response editTopic(String topicEntity) throws JsonProcessingException{
        TopicJSON topic = mapper.readValue(topicEntity, TopicJSON.class);
        topicOps.editTopic(topic);
        topic = topicOps.getTopicInfo(topic);
        return Response.ok().entity(mapper.writeValueAsString(topic)).build();
    }   

    @OPTIONS
    @Path("{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response topicIdOptions(){
        return Response.ok().build();
    }


    @GET
    @Path("{Id: \\d+ }")
    @Produces("application/json")
    //@RolesAllowed({"Admin","User"})
    public Response getTopic(@PathParam("Id") Long id)throws JsonProcessingException{
        TopicJSON topic = topicOps.getTopicInfo(new TopicJSON(id, null, null, null, null, null));
        topicExtendedView();
        return Response.ok().entity(mapper.writeValueAsString(topic)).build();
    }


    @GET
    @Path("Search")
    @Produces("application/json")
    @Consumes("application/json")
    //@RolesAllowed({"User", "Admin"})
    public Response searchTopics(@MatrixParam("Title") String title){
        TopicJSON topic = new TopicJSON(null, title, null, null, null, null);
        Set<TopicJSON> results = topicOps.searchTopic(topic);
        StringBuilder builder = new StringBuilder("[");
        ListIterator< TopicJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext())
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            } catch (JsonProcessingException ex) {
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    
    @OPTIONS
    @Path("Search")
    //@RolesAllowed({"User","Admin"})
    public Response searchOptions(){
        return Response.ok().build();
    }

}
