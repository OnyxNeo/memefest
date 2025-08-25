package com.memefest.Jaxrs;

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
public abstract class SearchSubResource extends Resource{
    


    @Inject
    protected CategoryOperations catOps;
    
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
    public abstract Response searchItem(@MatrixParam("Category") String name,
                                            @MatrixParam("Term") String searchTerm);
}
