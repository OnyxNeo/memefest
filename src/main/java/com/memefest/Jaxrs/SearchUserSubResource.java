package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.UserOperations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;

@RequestScoped
//@RolesAllowed({"User","Admin"})
public class SearchUserSubResource extends Resource{
  
    @Inject
    private UserOperations userOps;

    @GET
    @Path("Search")
    public Response searchItem(PathSegment segment){
        String userEntity = segment.getMatrixParameters().get("username").get(0);
        UserJSON user = new UserJSON(userEntity);
        Set<UserJSON> results = userOps.searchByUsername(user);
        ListIterator<UserJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
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
        return Response.ok().entity(builder.toString()).build();
    }

}
