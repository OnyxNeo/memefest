package com.memefest.Jaxrs;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.EventOperations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;

@RequestScoped
//@RolesAllowed({"User", "Admin"})
public class SearchEventSubResource extends Resource{
    
    @Inject
    private EventOperations eventOps;

    //@RolesAllowed({"User", "Admin"})
    public String searchItem(PathSegment searchValues){
        String term = null;
        String venue = null;
        String postedBy = null;
        MultivaluedMap<String,String> matrixParams = searchValues.getMatrixParameters();
        for (Entry<String,List<String>> param : matrixParams.entrySet()) {
            if(param.getKey().equalsIgnoreCase("Term"))
                term = param.getValue().get(0);
            else if(param.getKey().equalsIgnoreCase("Venue"))
                venue = param.getValue().get(0);
            else if(param.getKey().equalsIgnoreCase("PostedBy"))
                postedBy = param.getValue().get(0);
        }
        EventJSON event = new EventJSON(null, term, null, null, 
                            null, null, null, null
                                , null, null, null, 
                                venue, new UserJSON(postedBy), null, null, 0);
        Set<EventJSON> results = eventOps.searchEvents(event);
        ListIterator<EventJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        StringBuilder builder = new StringBuilder("[");
        while (iterator.hasNext()) {
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
