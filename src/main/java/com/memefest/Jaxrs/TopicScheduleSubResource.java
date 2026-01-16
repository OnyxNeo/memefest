package com.memefest.Jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.ScheduledTopicJSON;
import com.memefest.DataAccess.JSON.ScheduledTopicsJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.Services.TopicOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@PermitAll
public class TopicScheduleSubResource extends Resource{
    
    private TopicOperations topicOps;

    TopicScheduleSubResource(TopicOperations topicOps){
        super();
        this.topicOps = topicOps;
    }

    @OPTIONS
    public Response topicOptions(){
        return Response.ok().build();
    }

    @GET
    @Produces("apllication/json")
    public Response getScheduled(@QueryParam("title") String title, 
                                        @QueryParam("from") String from, 
                                            @QueryParam("to") String to){
        TopicJSON topic = null;
        if(title != null) 
            topic = new TopicJSON(null, title, null, null, null, null);
        return Response.ok(new ScheduledTopicsJSON(topicOps.getScheduledTopics(topic))).build();
    }

    @PUT
    @Consumes("application/json")
    public Response setScheduledTopic(String json) throws JsonProcessingException{
        ScheduledTopicJSON scheduledTopic = mapper.readValue(json, ScheduledTopicJSON.class);
        topicOps.createScheduledTopic(scheduledTopic.getTopic(), scheduledTopic.getTimestamp());
        return Response.ok().build();
    }

    @GET
    @Path("/{Title: \\d+}")
    @Produces("application/json")
    public Response getScheduled(@PathParam("Title") String title){
        ScheduledTopicsJSON scheduledTopics = new ScheduledTopicsJSON( topicOps.getScheduledTopics(new TopicJSON(null, title,
                                                 null, null, null, null)));
        return Response.ok(scheduledTopics).build();
    }

    @DELETE
    @Path("/{Title: \\d+}")
    @Produces("application/json")
    public Response cancelScheduled(@PathParam("Title") String title){
        TopicJSON topic = new TopicJSON(null, title, null, null, null, null);
        topicOps.cancelScheduledTopic(topic);
        return Response.ok().build();
    }

    @OPTIONS
    @Path("/{Title: \\d+}")
    public Response titleOptions(){
        return Response.ok().build();
    }
}


