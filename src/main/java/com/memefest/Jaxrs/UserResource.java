package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.DownvoteResponse;
import com.memefest.DataAccess.JSON.LikeResponse;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.PostOperations;
import com.memefest.Services.UserOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("User")
@RequestScoped
@PermitAll
public class UserResource extends Resource{
    
    @Inject
    protected UserOperations userOps;

    @Inject
    protected PostOperations postOps;

    @OPTIONS
    @RolesAllowed({"Admin", "User"})
    @Path("/{username}")
    public Response getUsersByUsernameOptions(){
        return Response.ok().build();
    }

    @GET
    @RolesAllowed({"Admin","User"})
    @Path("/{username}")
    @Produces("application/json")
    public Response getUsersByUsername(@PathParam("username") String username,
                                        @Context SecurityContext context,
                                @MatrixParam("start") @DefaultValue("1") String startIndex,
                                @MatrixParam("end") @DefaultValue("20") String endIndex){
        Set<UserJSON> users = userOps.getAllUsers();
        ListIterator<UserJSON> iterator = users.stream().collect(Collectors.toList()).listIterator();
        StringBuilder builder = new StringBuilder("]");
        while(iterator.hasNext())    
            try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                UserJSON userInst = iterator.next();
                userInst.setFollowed(userOps.isFollowedByUser(userInst, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(userInst);
                builder.append(entity);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }

    @GET
    @RolesAllowed({"User", "Admin"})
    @Path("/{UserId: \\d+}")
    @Produces({"application/json"})
    public Response getUserInfo(@Context SecurityContext context,
                                @PathParam("UserId") Long userId) throws JsonProcessingException{
        UserJSON userEntity = userOps.getUserInfo(new UserJSON(userId, null, null, 0, false,
                             null, null, null, null, null, null));
        userEntity.setFollowed(userOps.isFollowedByUser(userEntity, new UserJSON(context.getUserPrincipal().getName())));
        return Response.ok().entity(mapper.writeValueAsString(userEntity)).build();
    } 

    @OPTIONS
    @RolesAllowed({"User","Admin"})
    @Path("/{UserId: \\d+}")
    public Response getUserInfoOptions(){
        return Response.ok().build();
    }
    
    @PUT
    @RolesAllowed({"User"})
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response editUserProfile(@Context SecurityContext secContext, String userObj) throws JsonProcessingException{
        UserJSON user = mapper.readValue(userObj, UserJSON.class);
        UserJSON userEntity = userOps.getUserInfo(new UserJSON(secContext.getUserPrincipal().getName()));
        if(user.getEmail() != null)
            userEntity.setEmail(user.getEmail());
        if(user.getContacts() != 0)
            userEntity.setContacts(user.getContacts());
        if(user.getFirstName() != null)
            userEntity.setFirstName(user.getFirstName());
        if(user.getLastName() != null)
            userEntity.setLastName(user.getLastName());
        if(user.getUsername() != null)
            userEntity.setUsername(user.getUsername());
        userOps.editUser(userEntity);
        super.userOwnersView();
        return Response.ok(mapper.writeValueAsString(userEntity)).build();
    }

/* 
    @PUT
    @RolesAllowed({"User"})
    @Path("")
*/
    @GET
    @RolesAllowed({"User"})
    @PermitAll
    @Produces("application/json")
    @Path("Posts")
    public Response getPosts(
            @Context SecurityContext secContext
        ){

        UserJSON user = userOps.getUserInfo(new UserJSON(secContext.getUserPrincipal().getName()));
        Set<PostJSON> posts = userOps.getUserPosts(user);
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<PostJSON> iterator = posts.stream().collect(Collectors.toList()).listIterator();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostJSON comment = postOps.getPostInfo(iterator.next());
                comment.setLiked(postOps.isLikedByUser(comment, new UserJSON(secContext.getUserPrincipal().getName())));
                comment.setDownvoted(postOps.isDownvotedByUser(comment, new UserJSON(secContext.getUserPrincipal().getName())));
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

    @OPTIONS
    @PermitAll
    @Path("Posts")
    public Response userPostOptions(){
        return Response.ok().build();
    }    

    @GET
    @RolesAllowed({"User"})
    @Produces("application/json")
    @Path("Comments")
    public Response getComments(@Context SecurityContext secContext) throws JsonProcessingException{
        UserJSON user = userOps.getUserInfo(new UserJSON(secContext.getUserPrincipal().getName()));
        Set<PostWithReplyJSON> comments  = userOps.getComments(user);
        StringBuilder builder = new StringBuilder( "[");
        ListIterator<PostWithReplyJSON> iterator = comments.stream().collect(Collectors.toList()).listIterator();
        postWithReplyExtendedView();
        while(iterator.hasNext()) 
           try {
                if(iterator.nextIndex() != 0)
                    builder.append(",");
                PostWithReplyJSON comment = iterator.next();
                postOwnersView();
                comment.setLiked(postOps.isLikedByUser(comment, new UserJSON(secContext.getUserPrincipal().getName())));
                comment.setDownvoted(postOps.isDownvotedByUser(comment, new UserJSON(secContext.getUserPrincipal().getName())));
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

    @OPTIONS
    @Path("Comments")
    public Response userCommentsOptions(){
        return Response.ok().build();
    }


    @OPTIONS
    @PermitAll
    public Response userResourceOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    @PermitAll
    @Path("Like/{PostId}")
    public Response postLikeOptions(){
        return Response.ok().build();
    }

    @Path("Like/{PostId}")
    @RolesAllowed({"User"})
    @POST
    public Response likePost(@PathParam("PostId") int postId, @Context SecurityContext secContext) throws JsonProcessingException{
       PostJSON postJSON = new PostJSON(Long.valueOf(postId), null, null, 0, 0,
                            null, null, null, null);
        postJSON = postOps.getPostInfo(postJSON);
        postOps.togglePostUpvote(postJSON,  new UserJSON(secContext.getUserPrincipal().getName()));
        postJSON = postOps.getPostInfo(postJSON);
        LikeResponse likeResponse = new LikeResponse(postJSON.getUpvotes(), 
                                        postOps.isLikedByUser(postJSON,
                                             new UserJSON(secContext.getUserPrincipal().getName())));
        return Response.ok().entity(mapper.writeValueAsString(likeResponse)).build();
    }

    @Path("Follow/{UserId}")
    @RolesAllowed({"User"})
    @POST
    public Response follow(@PathParam("UserId") int  userId, @Context SecurityContext secContext){
        userOps.addFollower(new UserJSON(Long.valueOf(userId), null, null, 0, false, 
                                null, null, null, null, null, null), 
                            new UserJSON(secContext.getUserPrincipal().getName()));
        return Response.ok().build();
    }

    @OPTIONS
    @PermitAll
    @Path("ThumbsDown/{PostId}}")
    public Response postDislikeOptions(){
        return Response.ok().build();
    }

    @Path("ThumbsDown/{PostId}}")
    @RolesAllowed("User")
    @POST
    public Response dislikePost(@PathParam("PostId") int postId, @Context SecurityContext secContext)throws JsonProcessingException{
       PostJSON postJSON = new PostJSON(Long.valueOf(postId), null, null, 0, 0,
                            null, null, null, null);
        postJSON = postOps.getPostInfo(postJSON);
        postJSON.setDownvotes(postJSON.getDownvotes() + 1);
        postOps.editPost(postJSON);
        postJSON = postOps.getPostInfo(postJSON);
        DownvoteResponse dResponse = new DownvoteResponse(0, 
                                        postOps.isDownvotedByUser(
                                            postJSON,new UserJSON(secContext.getUserPrincipal().getName())));
        return Response.ok().entity(mapper.writeValueAsString(dResponse)).build();
    }

    @GET
    @RolesAllowed({"User","Admin"})
    @Produces("application/json")
    public Response getUserProfile(@Context SecurityContext context) throws JsonProcessingException{    
        userOwnersView();
        UserJSON userInfo = userOps.getUserInfo(new UserJSON(
                                context.getUserPrincipal().getName())); 
        return Response.ok().entity(mapper.writeValueAsString(userInfo)).build();
    }

    @GET
    @RolesAllowed({"User"})
    @Path("/Followers")
    @Produces({"application/json"})
    public Response getUserFollowers(@Context SecurityContext context){
        Set<UserJSON> followers = userOps.getFollowers(new UserJSON(context.getUserPrincipal().getName()));
        ListIterator<UserJSON> iterator = followers.stream().collect(Collectors.toList()).listIterator();
        StringBuilder builder = new StringBuilder("[");
        while(iterator.hasNext())
            try {
                if(iterator.hasPrevious())
                    builder.append(",");
                UserJSON userEntity = iterator.next();
                userEntity.setFollowed(userOps.isFollowedByUser(userEntity, new UserJSON(context.getUserPrincipal().getName())));
                String entity = mapper.writeValueAsString(userEntity);
                builder.append(entity);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                continue;
            }
        builder.append("]");
        return Response.ok().entity(builder.toString()).build();
    }


    @PUT
    @RolesAllowed({"User"})
    @Path("/Followers")
    @Consumes({"application/json"})
    public Response addFollowers(@Context SecurityContext secContext,
                                            String userFollowerEntity) throws JsonProcessingException{
        UserJSON userProfile  = new UserJSON(secContext.getUserPrincipal().getName());
        UserJSON userJSON = mapper.readValue(userFollowerEntity, UserJSON.class);
        userOps.addFollower(userProfile, userJSON);    
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({"User"})
    @Path("/Followers")
    @Consumes({"application/json"})
    public Response removeFollowers(@Context SecurityContext secContext,
                                        String userFollowerEntity)throws JsonProcessingException{
        UserJSON userProfile = new UserJSON(secContext.getUserPrincipal().getName());
        UserJSON userJSON = mapper.readValue(userFollowerEntity, UserJSON.class);
        userOps.removeFollower(userProfile, userJSON);
        return Response.ok().build();
    }
    
    @OPTIONS
    @RolesAllowed({"User"})
    @Path("/Followers")
    public Response userFollowerOptions(){
        return Response.ok().build();
    }

    @OPTIONS
    @RolesAllowed({"User"})
    @Path("/Following")
    public Response userFollowingOptions(){
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({"User"})
    @Path("/Following")
    @Consumes("application/json")
    public Response removeFollowing(@Context SecurityContext secContext,
                                        String userFollowingEntity) throws JsonProcessingException{
        UserJSON userProfile = new UserJSON(secContext.getUserPrincipal().getName());
        UserJSON userJSON = mapper.readValue(userFollowingEntity, UserJSON.class);
        userOps.removeFollower(userJSON, userProfile);
        return Response.ok().build();
    }

    @GET
    @RolesAllowed({"User", "Admin"})
    @Path("/Following")
    @Produces({"application/json"})
    public Response getFollowing(@Context SecurityContext secContext){
        UserJSON userProfile = new UserJSON(secContext.getUserPrincipal().getName());
        Set<UserJSON> userFolling  = userOps.getFollowing(userProfile);
        for (UserJSON userJSON : userFolling) {
            userJSON.setFollowed(true);
        }
        return Response.ok().entity(userFolling).build();
    }

    @PUT
    @RolesAllowed({"User"})
    @Path("/Following")
    @Produces({"applicarion/json"})
    @Consumes({"application/json"})
    public Response addFollowing(@Context SecurityContext secContext,
                                            String userFollowingEntity) throws JsonProcessingException{
        UserJSON userProfile  = new UserJSON(secContext.getUserPrincipal().getName());
        UserJSON userJSON = mapper.readValue(userFollowingEntity, UserJSON.class);
        userOps.addFollower(userJSON, userProfile);    
        return Response.ok().build();
    }
}
