package com.memefest.Jaxrs;

import java.util.ListIterator;
import jakarta.annotation.security.RolesAllowed;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;

@Path("Search")
public class SearchResource extends Resource {
    

    @RolesAllowed({"User", "Admin"})
    @Path("{Segment}")
    @GET
    public Response search(@PathParam("Segment") PathSegment segment){

        ListIterator<String> categs= segment.getMatrixParameters().get("Category").listIterator();
        StringBuilder builder = new StringBuilder("[");
        
        while (categs.hasNext()) {
            if(categs.hasPrevious())
                builder.append(",");
            if(categs.next().equalsIgnoreCase("Category")){
                builder.append("Categories:[");
                SearchCategorySubResource categSub = new SearchCategorySubResource();
                builder.append(categSub.searchItem(segment));
                builder.append("]");
            }
            else if(categs.next().equalsIgnoreCase("Topic")){
                builder.append("Topics:[");
                SearchTopicSubResource topicSub = new SearchTopicSubResource();
                builder.append(topicSub.searchItem(segment));
                builder.append("]");
            }
            else if(categs.next().equalsIgnoreCase("Event")){
                builder.append("Events:[");
                SearchEventSubResource eventSub = new SearchEventSubResource();
                builder.append(eventSub.searchItem(segment));
                builder.append("]");
            }
            else if(categs.next().equalsIgnoreCase("Post")){
                builder.append("Posts:[");
                SearchPostSubResource postSub = new SearchPostSubResource();
                builder.append(postSub.searchItem(segment));
                builder.append("]");
            }
            else if(categs.next().equalsIgnoreCase("User")){
                builder.append("Users:[");
                SearchUserSubResource userSub = new SearchUserSubResource();
                builder.append(userSub.searchItem(segment));
                builder.append("]");
            }
            else{
                SearchAllSubResource searchAll = new SearchAllSubResource();
                return Response.ok().entity(searchAll.searchItem(segment)).build();

            }   
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }



}
