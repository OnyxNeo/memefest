package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.SubCategoryJSON;
import com.memefest.Services.CategoryOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("Category")
@PermitAll
@RequestScoped
//@RolesAllowed({"User", "Admin"})
public class CategoryResource extends Resource{
    
    @Inject
    private CategoryOperations catOps;

    @Context
    private SecurityContext context;

    @GET
    //@RolesAllowed({"User","Admin"})
    @Path("/Search")
    @Produces({"application/json"})
    public Response searchCategories(@PathParam("Name") String name){
        CategoryJSON category = new CategoryJSON(null, name, null, null, null);   
        Set<CategoryJSON> results = catOps.searchCategory(category);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<CategoryJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()){
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

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    @Path("/Search")
    public Response searchOptions(){
        return Response.ok().build();
    }

    @GET
    //@RolesAllowed({"User","Admin"})
    @Path("/{CatId: \\d+}")
    @Produces({"application/json"})
    public Response getCategory(@PathParam("CatId") Long catId) throws JsonProcessingException{
        categoryExtendedView();
        CategoryJSON catInfo = catOps.getCategoryInfo(
                                    new CategoryJSON(catId, null, 
                                null, null, null));
            return Response.ok().entity(mapper.writeValueAsString(catInfo)).build(); 
    }

    @DELETE
    @Path("/{CatId: \\d+}")
    //@RolesAllowed({"User", "Admin"})
    public Response removeCategory(@PathParam("CatId") Long catId){
        catOps.removeCategory(new CategoryJSON(catId, null, null, null, null));
        return Response.ok().build();
    }

    @PUT
    //@RolesAllowed({"User","Admin"})
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response setCategory(String cat) throws JsonProcessingException{
        CategoryJSON category= mapper.readValue(cat, CategoryJSON.class);
        category = catOps.editCategory(category);
        return Response.ok().entity(mapper.writeValueAsString(category)).build(); 
    }

    @PUT
    @Consumes("application/json")
    @Path("/SubCategory")
    //@RolesAllowed({"User","Admin"})
    public Response setParentCategories(String cat)throws JsonProcessingException{
        catOps.editSubCategory(mapper.readValue(cat, SubCategoryJSON.class));
        return Response.ok().build();
    }
/* 
    @GET
    @Produces("application/json")
    @Path("/SubCategory")
    //@RolesAllowed({"User","Admin"})
    public Response getParentCategories(SubCategoryJSON category){
       SubCategoryJSON subCat = catOps.getSubCategoryInfo(category);
       return Response.ok().entity(subCat).build();
    }
*/
    @DELETE
    @Consumes("application/json")
    @Path("/SubCategory")
    //@RolesAllowed({"User","Admin"})
    public Response removeParentCategories(String cat) throws JsonProcessingException{
        catOps.removeParentCategories(mapper.readValue(cat, SubCategoryJSON.class));
        return Response.ok().build();
    }

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    @Path("/SubCategory")
    public Response subcategoryOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    @Path("/{catId: \\d+}")
    public Response categoryIdOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response categoryOptions(){
        return Response.ok().build();
    }
}
