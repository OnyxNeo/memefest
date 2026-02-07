package com.memefest.Jaxrs;

import java.time.LocalDateTime;
import java.util.HashSet;
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
import com.memefest.Services.UserOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("Post")
@RequestScoped
@PermitAll
public class PostResource extends Resource{
    
    @Inject
    private PostOperations postOps;

    @Inject
    private UserOperations userOps;

    @Context
    private SecurityContext context;

    @OPTIONS
    //@RolesAllowed({"User","Admin"})
    public Response postOptions(){
        return Response.ok().build();
    }

    
    @GET
    @Produces("application/json")
    @RolesAllowed({"User", "Admin"})
    public Response getPosts() throws JsonProcessingException{
        Set<PostJSON> results = postOps.getAllPosts();
        StringBuilder builder = new StringBuilder("[");
        ListIterator<PostJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()){
            try{
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON post = iterator.next();
                post.setLiked(postOps.isLikedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
                post.setDownvoted(postOps.isDownvotedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(post);
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @OPTIONS
    @Path("{Id: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response postIdOptions(){
        return Response.ok().build();
    }

    @POST
    @RolesAllowed({"User","Admin"})
    @Consumes("application/json")
    @Produces("application/json")
    public Response newPost(String postEntity)throws JsonProcessingException{
        PostJSON post = mapper.readValue(postEntity, PostJSON.class);
        post.setCreated(LocalDateTime.now());
        post.setUser(new UserJSON(context.getUserPrincipal().getName()));
        post = postOps.editPost(post);
        UserJSON user = userOps.getUserInfo(new UserJSON(context.getUserPrincipal().getName()));
        post.setUser(user);
        return Response.ok().entity(mapper.writeValueAsString(post)).build();    
    
    }
    
    @PUT
    @RolesAllowed({"User","Admin"})
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{Id: \\d+}")
    public Response editPost(String postEntity, @PathParam("Id") int id)throws JsonProcessingException{
        PostJSON post = mapper.readValue(postEntity, PostJSON.class);
        post.setPostId(Long.valueOf(id));
        post.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editPost(post);
        post = postOps.getPostInfo(post);
        return Response.ok().entity(post).build();    
    }

    @GET
    @Path("{Id: \\d+}")
    @RolesAllowed({"User", "Admin"})
    @Produces("application/json")
    public Response getPost(@PathParam("Id") Long Id)throws JsonProcessingException{
        PostJSON post = postOps.getPostInfo(new PostJSON(Id, null, null, 0, 0, null, null, null, null));            
        postExtendedView();
        post.setDownvoted(postOps.isDownvotedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
        post.setLiked(postOps.isLikedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
        return Response.ok().entity(mapper.writeValueAsString(post)).build();
    }

    @DELETE
    @Path("{Id: \\d+}")
    @RolesAllowed({"User","Admin"})
    public Response removePost(@PathParam("Id") Long Id,
                                    @Context SecurityContext context
                                ){
        postOps.removePost(new PostJSON(Id, null,
                     null, 0, 0,
                      new UserJSON(context.getUserPrincipal().getName()),
                       null, null, null));
        return Response.ok().build();
    }

    @OPTIONS
    @Path("Search")
    //@RolesAllowed({"User","Admin"})
    public Response postCommentOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("Search")
    @RolesAllowed({"User","Admin"})
    @Produces("application/json")
    @Consumes("application/json")
    public Response getPost(@QueryParam("Comment") String comment,
                                @QueryParam("Username") String username)throws JsonProcessingException{
        UserJSON user = new UserJSON(username);
        PostJSON post = new PostJSON(null, comment, null,
             0, 0, user, null, null, null);
        Set<PostJSON> results = postOps.searchPost(post);
        StringBuilder builder = new StringBuilder("[");
        ListIterator<PostJSON> iterator = results.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) {
            try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON postInst = iterator.next();
                post.setDownvoted(postOps.isDownvotedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
                post.setLiked(postOps.isDownvotedByUser(post, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(postInst);
                builder.append(entity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @GET
    @Path("{Id: \\d+}/Comments")
    @RolesAllowed({"User","Admin"})
    @Produces("application/json")
    public Response getPostReplies(@PathParam("Id") Long Id)throws JsonProcessingException{
        Set<PostJSON> posts = postOps.getPostWithReplyInfo(new PostWithReplyJSON(Id, null, null, 0, 0, null, null, null, null, null));
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<PostJSON> iterator = posts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON comment = postOps.getPostInfo(iterator.next());
                comment.setDownvoted(postOps.isDownvotedByUser(comment, new UserJSON(context.getUserPrincipal().getName())));
                comment.setLiked(postOps.isDownvotedByUser(comment, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(comment);
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
    @Path("{Id: \\d+}/Comments")
    @RolesAllowed({"User", "Admin"})
    @Consumes("application/json")
    public Response removePostReplies(String postEntity, 
                            @Context SecurityContext context,
                            @PathParam("Id") int id)throws JsonProcessingException{
        PostWithReplyJSON post = mapper.readValue(postEntity, PostWithReplyJSON.class);
        post.setPostId(Long.valueOf(id));
        post.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removePostWithReply(post);
        return Response.ok().build();
    }

    @OPTIONS
    @Path("/{Id: \\d+}/Comments")
    //@RolesAllowed({"User","Admin"})
    public Response postRepliesOptions(){
        return Response.ok().build();
    }

    @POST
    @Path("{Id: \\d+}/Comments")
    @RolesAllowed({"User", "Admin"})
    @Produces("application/json")
    @Consumes("application/json")
    public Response editPostReplies(String postEntity, @PathParam("Id") int id) throws JsonProcessingException{
        if(postEntity == null)
            return Response.noContent().build();
        else{
            PostJSON comment = mapper.readValue(postEntity, PostJSON.class);
            comment.setCreated(LocalDateTime.now());
            PostWithReplyJSON post = new PostWithReplyJSON(Long.valueOf(id), null, null, 0, 
                            0, new UserJSON(context.getUserPrincipal().getName())
                    , null, null, null, null);
            Set<PostJSON> comments = new HashSet<PostJSON>();
            comments.add(comment);
            for (PostJSON reply : comments) {
                reply.setUser(new UserJSON(
                    context.getUserPrincipal().getName()));
            }
            post.setPosts(comments);
            post = postOps.editPostWithReply(post);
            return Response.ok().entity(mapper.writeValueAsString(post.getPosts().iterator().next())).build();
        }
    }

    @OPTIONS
    @Path("{Id: \\d+}/Event/{EventId: \\d+}")
    //@RolesAllowed({"User", "Admin"})
    public Response eventPostIdOptions(){
        return Response.ok().build();
    }

    @POST
    @Path("Event")
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response newEventPost(String eventPostEntity)throws JsonProcessingException{
        if(eventPostEntity == null){
            return Response.noContent().build();
        }
        EventPostJSON eventPost = mapper.readValue(eventPostEntity, EventPostJSON.class);
        eventPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editEventPost(eventPost);
        eventPost = postOps.getEventPostInfo(eventPost);
        return Response.ok().entity(mapper.writeValueAsString(eventPost)).build();
    }       
    
    @PUT
    @Path("{Id: \\d+}/Event/{EventId: \\d+}")
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response setEventPost(String eventPostEntity,
                                    @PathParam("EventId") int eventId,
                                    @PathParam("Id") int postId)throws JsonProcessingException{
        if(eventPostEntity == null){
            return Response.noContent().build();
        }
        EventPostJSON eventPost = mapper.readValue(eventPostEntity, EventPostJSON.class);
        EventJSON event = eventPost.getEvent();
        event.setEventID(Long.valueOf(eventId));
        eventPost.setEvent(event);
        eventPost.setPostId(Long.valueOf(postId));
        eventPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editEventPost(eventPost);
        eventPost = postOps.getEventPostInfo(eventPost);
        eventPost.setLiked(postOps.isLikedByUser(eventPost, new UserJSON(context.getUserPrincipal().getName())));
        eventPost.setDownvoted(postOps.isDownvotedByUser(eventPost, new UserJSON(context.getUserPrincipal().getName())));
        return Response.ok().entity(mapper.writeValueAsString(eventPost)).build();
    }   

    @OPTIONS
    @Path("Event")
    //@RolesAllowed({"User","Admin"})
    public Response eventPostOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    @Path("Event/{EventId: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response eventPostByEventOptions(){
        return Response.ok().build();
    }

    
    @GET
    @Path("Event/{EventId: \\d+}")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getEventPosts(@PathParam("EventId") Long eventId){
        Set<EventPostJSON> eventPosts = postOps.getEventPostsByEvent(new EventJSON(eventId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0));
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<EventPostJSON> iterator = eventPosts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON comment = postOps.getPostInfo(iterator.next());
                comment.setLiked(postOps.isLikedByUser(comment, new UserJSON(context.getUserPrincipal().getName())));
                comment.setDownvoted(postOps.isDownvotedByUser(comment, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(comment);
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
    @Path("{PostId: \\d+}/Event/{EventId: \\d+}")
    @RolesAllowed("application/json")
    public Response removeEventPost(@PathParam("EventId") int eventId,
                                        @PathParam("PostId") int postId) throws JsonProcessingException{
        EventPostJSON eventPost = new EventPostJSON(Long.valueOf(postId), null, null, 0,
                                     eventId, null, 
                                     new EventJSON(Long.valueOf(eventId), null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0),
                                      null, null, null);
        eventPost.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removeEventPost(eventPost);
        return Response.ok().build();
    }

    @POST
    @Path("Topic")
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response newTopicPost(String topicPostEntity) throws JsonProcessingException{
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
    @Path("{Id: \\d+}/Topic/{TopicId: \\d+}")
    @RolesAllowed({"User","Admin"})
    public Response removeTopicPost(@PathParam("Id") int id, 
                                        @PathParam("TopicId") int topicId)throws JsonProcessingException{
        TopicPostJSON topic = new TopicPostJSON(Long.valueOf(id), null, null, 0, 0, null,
                                new TopicJSON(Long.valueOf(topicId), null, null, null, null,
                                     null), null, null, null);
        topic.setUser(new UserJSON(context.getUserPrincipal().getName()));
        postOps.removeTopicPost(topic);
        return Response.ok().build();
    }
    
    @OPTIONS
    @Path("Topic")
    //@RolesAllowed({"User","Admin"})
    public Response topicPostOptions(){
        return Response.ok().build();
    }
    

    @OPTIONS
    @Path("{PostId: \\d+}/Topic/{TopicId: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response topicPostIdOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    @Path("Topic/{TopicId: \\d+}")
    //@RolesAllowed({"User","Admin"})
    public Response topicPostByTopicOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("Topic/{TopicId: \\d+}")
    @Produces("application/json")
    @RolesAllowed({"User","Admin"})
    public Response getTopicPostsByTopic(@PathParam("TopicId") Long topicId){
        Set<TopicPostJSON> topicPosts = postOps.getTopicPostsByTopic(new TopicJSON(topicId, null, null, null, null, null));
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<TopicPostJSON> iterator = topicPosts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext())
            try{
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON topicPost = postOps.getPostInfo(iterator.next());  
                topicPost.setLiked(postOps.isLikedByUser(topicPost, new UserJSON(context.getUserPrincipal().getName())));
                topicPost.setDownvoted(postOps.isDownvotedByUser(topicPost, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(topicPost);
                builder.append(entity);
            }
            catch(JsonProcessingException ex){
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();   
    }







    @GET
    @Path("User/Repost")
    @RolesAllowed({"User","Admin"})
    public Response getReposts(@Context SecurityContext context)throws JsonProcessingException{
        Set<RepostJSON> reposts = postOps.getRepostsByUser(
                    new UserJSON(context.getUserPrincipal().getName()));
        StringBuilder builder = new StringBuilder("[");
        ListIterator<RepostJSON> iterator = reposts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) {
            if(iterator.nextIndex() != 0)
                builder.append(",");
            RepostJSON repost = iterator.next();
            repost.setLiked(postOps.isLikedByUser(repost, new UserJSON(context.getUserPrincipal().getName())));
            repost.setDownvoted(postOps.isDownvotedByUser(repost, new UserJSON(context.getUserPrincipal().getName())));
            String entity = mapper.writeValueAsString(repost);
            builder.append(entity);
        }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build(); 
    }

    @POST
    @Path("User/Repost")
    @RolesAllowed({"User", "Admin"})
    public Response newRepost(String repost, 
                                    @Context SecurityContext context
                                    ) throws JsonProcessingException{
        RepostJSON entity = mapper.readValue(repost, RepostJSON.class);
        entity.setOwner(new UserJSON(context.getUserPrincipal().getName()));
        postOps.editRepost(entity);
        return Response.ok().build();
    }


    @OPTIONS
    @Path("User/Repost")
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
