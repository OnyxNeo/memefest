package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.Services.CategoryOperations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class SearchCategorySubResource extends SearchSubResource{
    
    @Inject
    private CategoryOperations catOps;
    
    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    @Path("/Search")
    public Response searchOptions(){
        return Response.ok().build();
    }

    @GET
    //@RolesAllowed({"User","Admin"})
    @Path("/Search")
    @Produces({"application/json"})
    public Response searchItem(@MatrixParam("Category") String cat, @MatrixParam("Term") String term){
        CategoryJSON category = new CategoryJSON(0, term, null, null, null);   
        Set<CategoryJSON> results = catOps.searchCategory(category);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<CategoryJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while (iterator.hasNext())
            try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

}
