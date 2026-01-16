package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.Services.PostOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;

@PermitAll
//@RolesAllowed({"User", "Admin"})
public class SearchPostSubResource extends SearchResource{
  
    @Inject
    private PostOperations postOps;

    //@RolesAllowed({"User","Admin"})
    public Response searchItem(PathSegment segment){
        String term = segment.getMatrixParameters().get("Term").get(0);
        PostJSON post = new PostJSON(null, term, null, 0, 0, null, null, null, null);
        Set<PostJSON> results = postOps.searchPost(post);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<PostJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
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
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    } 
}
