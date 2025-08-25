package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.EventJSON;
import com.memefest.DataAccess.JSON.EventPostJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.RepostJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.TopicPostJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.PostOperations;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Post")
@RequestScoped
public class PostResource extends Resource{
    
    @Inject
    private PostOperations postOps;

    @Context
    private SecurityContext context;

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response postOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    @Path("/{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response postIdOptions(){
        return Response.ok().build();
    }

    @PUT
    //@RolesAllowed({"User","Admin"})
    @Consumes("application/json")
    @Produces("application/json")
    public Response setPost(String postEntity)throws JsonProcessingException{
        PostJSON post = mapper.readValue(postEntity, PostJSON.class);
        post.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editPost(post);
        post = postOps.getPostInfo(post);
        return Response.ok().entity(post).build();    
    
    }

    @GET
    @Path("/{Id: \\d+}")
    //@RolesAllowed({"User", "Admin"})
    @Produces("application/json")
    public Response getPost(@PathParam("Id") int Id)throws JsonProcessingException{
        PostJSON post = postOps.getPostInfo(new PostJSON(Id, null, null, 0, 0, null, null, null));            
        return Response.ok().entity(mapper.writeValueAsString(post)).build();
    }

    @DELETE
    @Path("/{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response removePost(@PathParam("Id") int Id
                                //,@Context SecurityContext context
                                ){
        postOps.removePost(new PostJSON(Id, null,
                     null, 0, 0,
                      new UserJSON(context.getUserPrincipal().getName()),
                       null, null));
        return Response.ok().build();
    }

    @OPTIONS
    @Path("/Search")
    //@RolesAllowed({"User","Admin"})
    public Response postCommentOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("/Search")
    //@RolesAllowed({"User","Admin"})
    @Produces("application/json")
    @Consumes("application/json")
    public Response getPost(@MatrixParam("Comment") String comment,
                                @MatrixParam("Username") String username){
        UserJSON user = new UserJSON(username);
        PostJSON post = new PostJSON(0, comment, null,
             0, 0, user, null, null);
        Set<PostJSON> results = postOps.searchPost(post);
        StringBuilder builder = new StringBuilder("[");
        for (PostJSON postJSON : results) {
            try {
                builder.append(mapper.writeValueAsString(postJSON));
                builder.append(",");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @GET
    @Path("/PostReplies/{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    @Produces("application/json")
    public Response getPostReplies(@PathParam("Id") int Id)throws JsonProcessingException{
        PostWithReplyJSON post = postOps.getPostWithReplyInfo(new PostWithReplyJSON(Id, null, null, 0, 0, null, null, null, null));
        return Response.ok().entity(mapper.writeValueAsString(post)).build();   
    }

    @DELETE
    @Path("/PostReplies")
    //@RolesAllowed({"User", "Admin"})
    @Consumes("application/json")
    public Response removePostReplies(String postEntity 
                            //,@Context SecurityContext context
                            )throws JsonProcessingException{
        PostWithReplyJSON post = mapper.readValue(postEntity, PostWithReplyJSON.class);
        post.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removePostWithReply(post);
        return Response.ok().build();
    }

    @OPTIONS
    @Path("/PostReplies/{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response postRepliesOptions(){
        return Response.ok().build();
    }

    @PUT
    @Path("/PostReplies")
    //@RolesAllowed({"User", "Admin"})
    @Produces("application/json")
    @Consumes("application/json")
    public Response editPostReplies(String postEntity) throws JsonProcessingException{
        if(postEntity == null)
            return Response.noContent().build();
        else{
            PostWithReplyJSON post = mapper.readValue(postEntity, PostWithReplyJSON.class);
            for (PostJSON reply : post.getPosts()) {
                reply.setUser(new UserJSON(context.getUserPrincipal().getName()));
            }
            postOps.editPost(post);
            Set<PostJSON> editedPosts = post.getPosts();
            PostJSON basicInfo = postOps.getPostInfo(post); 
            post = new PostWithReplyJSON(0, basicInfo.getComment(), 
                            basicInfo.getCreated(), basicInfo.getUpvotes()
                            , basicInfo.getDownvotes(), 
                            basicInfo.getUser(), null, null, null);
            post.setPosts(editedPosts);
            return Response.ok().entity(mapper.writeValueAsString(post)).build();
        }
    }

    @OPTIONS
    @Path("/Event/{PostId: \\d+}")
    //@RolesAllowed({"User", "Admin"})
    public Response eventPostIdOptions(){
        return Response.ok().build();
    }

//fix
    @GET
    @Path("/Event/{PostId: \\d+}")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response getEventPosts(@PathParam("EventId") int eventId){
        Set<EventPostJSON> eventPosts = postOps.getEventPostsByEvent(new EventJSON(eventId, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<EventPostJSON> iterator = eventPosts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @DELETE
    @Path("/Event")
    //@RolesAllowed("application/json")
    public Response removeEventPost(String eventPostEntity) throws JsonProcessingException{
        EventPostJSON eventPost = mapper.readValue(eventPostEntity, EventPostJSON.class);
        eventPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removeEventPost(eventPost);
        return Response.ok().build();
    }

    @PUT
    @Path("/Topic")
    @Consumes("application/json")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response setTopicPost(String topicPostEntity) throws JsonProcessingException{
        if(topicPostEntity == null){
            return Response.noContent().build();
        }
        TopicPostJSON topicPost = mapper.readValue(topicPostEntity, TopicPostJSON.class);
        topicPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editTopicPost(topicPost);
        topicPost = postOps.getTopicPostInfo(topicPost);
        return Response.ok().entity(mapper.writeValueAsString(topicPost)).build();
    }

    @DELETE
    @Path("/Topic")
    @Consumes("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response removeTopicPost(String topicEntity)throws JsonProcessingException{
        TopicPostJSON topic = mapper.readValue(topicEntity, TopicPostJSON.class);
        topic.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removeTopicPost(topic);
        return Response.ok().build();
    }
    
    @OPTIONS
    @Path("/Topic")
    //@RolesAllowed({"User","Admin"})
    public Response topicPostOptions(){
        return Response.ok().build();
    }
    

    @OPTIONS
    @Path("/Topic/{PostId: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response topicPostIdOptions(){
        return Response.ok().build();
    }

//fix
    @GET
    @Path("/Topic/{PostId: \\d+}")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response getTopicPosts(@PathParam("TopicId") int topicId){
        Set<TopicPostJSON> topicPosts = postOps.getTopicPostsByTopic(new TopicJSON(topicId, null, null, null, null, null));
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<TopicPostJSON> iterator = topicPosts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext())
            try{
                String entity = mapper.writeValueAsString(iterator.next());
                if(iterator.hasPrevious())
                    builder.append(",");
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();   
    }



    @PUT
    @Path("/Event")
    @Consumes("application/json")
    @Produces("application/json")
    //@RolesAllowed({"User","Admin"})
    public Response setEventPost(String eventPostEntity)throws JsonProcessingException{
        if(eventPostEntity == null){
            return Response.noContent().build();
        }
        EventPostJSON eventPost = mapper.readValue(eventPostEntity, EventPostJSON.class);
        eventPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editEventPost(eventPost);
        eventPost = postOps.getEventPostInfo(eventPost);
        return Response.ok().entity(mapper.writeValueAsString(eventPost)).build();
    }   

    @OPTIONS
    @Path("/Event")
    //@RolesAllowed({"User","Admin"})
    public Response eventPostOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("/User/Repost")
    //@RolesAllowed({"User","Admin"})
    public Response getReposts(@Context SecurityContext context)throws JsonProcessingException{
        Set<RepostJSON> reposts = postOps.getRepostsByUser(
                    new UserJSON(context.getUserPrincipal().getName()));
        StringBuilder builder = new StringBuilder("[");
        for (RepostJSON repostJSON : reposts) {
            builder.append(mapper.writeValueAsString(repostJSON));
            builder.append(",");
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build(); 
    }

    @OPTIONS
    @Path("/User/Repost")
    //@RolesAllowed({"User","Admin"})
    public Response repostOptions(){
        return Response.ok().build();
    }




/* 
    protected void publicViewConfig(){
        Map<String,SimpleBeanPropertyFilter> filters = getPublicViews();
        FilterProvider provider = setFilters(filters);
        mapper.setFilterProvider(provider);
    }

    protected void extendedViewConfig(){
        Map<String,SimpleBeanPropertyFilter> filters = getPublicViews();
        SimpleBeanPropertyFilter extFilter = SimpleBeanPropertyFilter.serializeAll();
        filters.put("PostPublicView", extFilter);
        FilterProvider provider = setFilters(filters);
        mapper.setFilterProvider(provider);
    }
*/

}
