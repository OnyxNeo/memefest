package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.Services.TopicOperations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.PathSegment;

//@RolesAllowed({"User", "Admin"})
@RequestScoped
public class SearchTopicSubResource extends Resource{
    
    @Inject
    private TopicOperations topicOps;

   //@RolesAllowed({"User","Admin"})
    public String searchItem(PathSegment segment){
        String term = segment.getMatrixParameters().get("Term").get(0);
        TopicJSON topic = new TopicJSON(null, term, null, null, null, null);
        Set<TopicJSON> results = topicOps.searchTopic(topic);
        StringBuilder builder = new StringBuilder();
        ListIterator<TopicJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()){
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if (iterator.hasPrevious()) {
                    builder.append(",");
                }
                builder.append(entity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }
        }
        return builder.toString();
    }
}
