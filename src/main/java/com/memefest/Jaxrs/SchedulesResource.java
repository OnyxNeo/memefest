package com.memefest.Jaxrs;

import com.memefest.Services.EventOperations;
import com.memefest.Services.TopicOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("Schedule")
@RequestScoped
@PermitAll
public class SchedulesResource extends Resource{
    
    @Inject
    private TopicOperations topicOps;
    
    @Inject
    private EventOperations eventOps;

    @OPTIONS
    public Response scheduleOptions(){
        return Response.ok().build();
    }

    @Path("Topic")
    public TopicScheduleSubResource getTopicSubResource(){
        return new TopicScheduleSubResource(topicOps);
    }
}
