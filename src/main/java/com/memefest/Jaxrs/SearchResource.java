package com.memefest.Jaxrs;

import com.memefest.Services.CategoryOperations;
import com.memefest.Services.TopicOperations;

import jakarta.inject.Inject;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("Search")
public class SearchResource extends Resource {
    
    public SearchSubResource search(@MatrixParam("Category") String category,@MatrixParam("Term") String searchTerm){
        if(category.equalsIgnoreCase("Category")){
            return new SearchCategorySubResource();
        }
        return new SearchCategorySubResource();
    }

}
