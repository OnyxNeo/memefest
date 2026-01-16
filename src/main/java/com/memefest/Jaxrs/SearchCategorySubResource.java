package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.Services.CategoryOperations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.PathSegment;

@RequestScoped
public class SearchCategorySubResource extends Resource{
    
    @Inject
    private CategoryOperations catOps;

    //@GET
    //@RolesAllowed({"User","Admin"})
    //@Path("/Search")
    //@Produces({"application/json"})
    protected String searchItem(PathSegment segment){
        String term = segment.getMatrixParameters().get("Term").get(0);
        CategoryJSON category = new CategoryJSON(null, term, null, null, null);   
        Set<CategoryJSON> results = catOps.searchCategory(category);
        StringBuilder builder = new StringBuilder();
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
        return builder.toString();
    }

}
