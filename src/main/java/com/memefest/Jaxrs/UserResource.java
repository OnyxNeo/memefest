package com.memefest.Jaxrs;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.UserOperations;

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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/User")
@RequestScoped 
public class UserResource extends Resource{
    
    @Inject
    private UserOperations userOps;

    @OPTIONS
    @RolesAllowed({"Admin", "User"})
    @Path("/{username}")
    public Response getUsersByUsernameOptions(){
        return Response.ok().build();
    }

    //finish 
    @GET
    @RolesAllowed({"Admin","User"})
    @Path("/{username}")
    @Produces("application/json")
    public Response getUsersByUsername(@PathParam("username") String username,
                                @MatrixParam("start") @DefaultValue("1") String startIndex,
                                @MatrixParam("end") @DefaultValue("20") String endIndex){
        Set<UserJSON> users = userOps.getAllUsers();
        ListIterator<UserJSON> iterator = users.stream().collect(Collectors.toList()).listIterator();
        StringBuilder builder = new StringBuilder("]");
        while(iterator.hasNext())    
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

    @GET
    @RolesAllowed({"User", "Admin"})
    @Path("/{UserId: \\d+}")
    @Produces({"application/json"})
    public Response getUserInfo(@PathParam("UserId") int userId) throws JsonProcessingException{
        UserJSON userEntity = userOps.getUserInfo(new UserJSON(userId, null));
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
        return Response.ok(mapper.writeValueAsString(userEntity)).build();
    }   


    @OPTIONS
    @RolesAllowed({"Admin", "User"})
    public Response userResourceOptions(){
        return Response.ok().build();
    }

    @GET
    @RolesAllowed({"User","Admin"})
    @Produces("application/json")
    public Response getUserProfile(@Context SecurityContext context) throws JsonProcessingException{
        Map<String, SimpleBeanPropertyFilter>  view = userProfileFilter();
        this.mapper.setFilterProvider(setFilters(view));
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


    private Map<String,SimpleBeanPropertyFilter> userProfileFilter(){
        Map<String,SimpleBeanPropertyFilter> view = getPublicViews();
        view.put("UserPublicView", SimpleBeanPropertyFilter.serializeAll());
        return view;
    }
}
