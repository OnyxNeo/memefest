package com.memefest.Jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.UserOperations;
import com.memefest.Services.UserSecurityService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;


@PermitAll
@RequestScoped
@Path("Admin")
//@RolesAllowed({"Admin"})
public class AdminResource extends Resource{
    
    @Inject
    private UserOperations userOps;

    @Inject
    private UserSecurityService adminOps;

    @PUT
    @RolesAllowed({"Admin"})
    @Path("User")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response editUserProfile(String userObj) throws JsonProcessingException{
        UserJSON user = mapper.readValue(userObj, UserJSON.class);
        if(adminOps.isAdmin(user)){
            return Response.notModified().build();
        }
        UserJSON userEntity = userOps.getUserInfo(user);
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

    
}
