package com.memefest.Jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.memefest.CacheHelper.CacheHelper;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.UserLoginJSON;
import com.memefest.DataAccess.JSON.UserSecurityJSON;
import com.memefest.Email.EmailSender;
import com.memefest.Security.JwtAuthIdentityStore;
import com.memefest.Services.UserOperations;
import com.memefest.Services.UserSecurityService;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.ehcache.Cache;
import org.ehcache.CacheManager;

@PermitAll
@RequestScoped
@Path("SignIn")
public class UserLoginResource extends Resource{
  
  @Inject
  private UserSecurityService userService;
  
  @Inject 
  private UserOperations userOps;

  @Inject
  private CacheHelper cacheHelper;
  
  @Context
  private jakarta.ws.rs.core.SecurityContext securityContext;


  @PermitAll
  @PUT
  @Path("Verify-email")
  @Consumes({"application/x-www-form-urlencoded"})
  public Response verifyEmail(@BeanParam UserInput input) throws JsonProcessingException{
    UserJSON user = this.userService.getUserDetails(new UserJSON(null, input.getEmail(), input.getUsername(), 0, false, input.getFirstname(), input.getLastname(), 
      null, null, null, null));
    if (user != null)
      return Response.status(403).entity("Username already exists, try a different username").build(); 
    if (this.userService.emailExists(new UserJSON(input.getUsername(), input.getEmail())))
      return Response.status(403).entity("Someone registered with this email already").build(); 
    UserJSON guestDetails = new UserJSON(input.getUsername(), input.getEmail(), input.getContacts(), input.getFirstname(), input.getLastname());
    UserSecurityJSON securityDetails = JwtAuthIdentityStore.createUserAccessToken(guestDetails);
    guestDetails.setUserSecurity(securityDetails);
    userOwnersView();
    cacheGuestContent(guestDetails);
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html>");
    sb.append("<html lang=\"en\">");
    sb.append("<head>");
    sb.append("<meta charset=\"UTF-8\">");
    sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
    sb.append("<title>Set Up Password</title>");
    sb.append("</head>");
    sb.append("<body style=\"text-align: center; padding: 50px; font-family: Arial, sans-serif;\">");
    sb.append("<a");
    sb.append(" ");
    sb.append("href='http://localhost:5173/password-setup?token=" + securityDetails.getAccessTkn() + "'");
    sb.append(" ");
    sb.append("style=\"");
    sb.append("display: inline-block;");
    sb.append(" ");
    sb.append("padding: 12px 24px;");
    sb.append(" ");
    sb.append("background-color: #4CAF50;");
    sb.append(" ");
    sb.append("color: white;");
    sb.append(" ");
    sb.append("text-decoration: none;");
    sb.append(" ");
    sb.append("font-size: 16px;");
    sb.append(" ");
    sb.append("font-weight: bold;");
    sb.append(" ");
    sb.append("border-radius: 8px;");
    sb.append(" ");
    sb.append("border: none;");
    sb.append(" ");
    sb.append("box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);");
    sb.append(" ");
    sb.append("transition: transform 0.2s, box-shadow 0.2s;");
    sb.append("\"");
    sb.append(" ");
    sb.append("onmouseover=\"this.style.boxShadow='0 6px 8px rgba(0, 0, 0, 0.2)'\"");
    sb.append(" ");
    sb.append("onmouseout=\"this.style.boxShadow='0 4px 6px rgba(0, 0, 0, 0.1)'\"");
    sb.append(" ");
    sb.append("onmousedown=\"this.style.transform='scale(0.95)'\"");
    sb.append(" ");
    sb.append("onmouseup=\"this.style.transform='scale(1)'\"");
    sb.append(" ");
    sb.append(">");
    sb.append("Set Up Password");
    sb.append("</a>");
    sb.append("</body>");
    sb.append("</html>");
    EmailSender.sendPlainTextEmail("hyperforbics@gmail.com", input.getEmail(), "Jinice Email verification", sb.toString(), false);
    //return Response.ok(mapper.writeValueAsString(guestDetails)).build();
    return Response.ok(mapper.writeValueAsString(sb.toString())).build();
  }
  
  @PermitAll
  @OPTIONS
  @Path("Verify-email")
  public Response options() {
    return Response.ok().build();
  }
  
  @PermitAll
  @PUT
  @Path("Sign-Up")
  @Consumes({"application/json"})
  public Response signUp(String userSecurityJson) throws JsonProcessingException{
    UserSecurityJSON user = mapper.readValue(userSecurityJson,UserSecurityJSON.class);
    if (user.getPassword() == null)
      return Response.status(204).entity("Null password").build(); 
    UserJSON userInClaims = null;
    try {
      userInClaims = JwtAuthIdentityStore.verifyToken(user.getAccessTkn());
    } catch (JwtException ex) {
      return Response.status(401).entity("Bad or expired token").build();
    } 
    if (userInClaims != null) {
      userInClaims = getGuestContentFromCache(userInClaims.getUsername());
      UserSecurityJSON userSecurity = new UserSecurityJSON(null, user.getPassword(), null, userInClaims.getUserId(), userInClaims.getUsername());
      userSecurity.setAccessTkn(JwtAuthIdentityStore.createUserAccessToken(userInClaims).getAccessTkn());
      userSecurity.setRefreshTkn(JwtAuthIdentityStore.createUserRefreshTokens(userInClaims).getRefreshTkn());
      userInClaims.setUserSecurity(userSecurity);
      cacheNewUserContent(userInClaims);
      this.userOps.editUser(userInClaims);
      userSecurity.setPassword(null);
      return Response.ok().entity(mapper.writeValueAsString(userInClaims)).build();
    }  
    return Response.status(401).entity("Bad token").build();
  }
  
  @PermitAll
  @OPTIONS
  @Path("Sign-Up")
  public Response optionsSignUp() {
    return Response.ok().build();
  }
  
  @PermitAll
  @PUT
  @Path("Refresh/Access")
  @Consumes({"application/json"})
  public Response refreshAccessToken(String userJson) throws JsonProcessingException
  //, JwtException
  {
    UserSecurityJSON refreshWrapper = mapper.readValue(userJson, UserSecurityJSON.class);
    if (refreshWrapper == null || refreshWrapper.getRefreshTkn() == null)
      return Response.status(204).build(); 
    String refreshToken = refreshWrapper.getRefreshTkn();
    UserJSON refreshClaims = JwtAuthIdentityStore.verifyToken(refreshToken);
    UserSecurityJSON secDetails = this.userService.getSecurityDetails(refreshClaims);
    if (secDetails == null || secDetails.getRefreshTkn() != refreshToken)
      return Response.status(401).entity("Invalid refresh Token").build(); 
    if (refreshClaims != null) {
      UserSecurityJSON securityDetails = new UserSecurityJSON();
      securityDetails = JwtAuthIdentityStore.createUserAccessToken(secDetails.getUser());
      securityDetails.setRefreshTkn(refreshToken);
      refreshClaims.setUserSecurity(securityDetails);
      userService.setSecurityDetails(securityDetails);
      return Response.ok(mapper.writeValueAsString(securityDetails)).build();
    } 
    return Response.status(401).entity("refresh or access tokens not recognised").build();
  }

  @PermitAll
  @OPTIONS
  @Path("Refresh/Access")
  public Response optionRefreshAccessTknOptions() {
    return Response.ok().build();
  }
  

  @RolesAllowed({"User","Admin"})
  @GET
  @Path("Refresh/All")
  @Consumes({"application/json"})
  public Response refreshAll(@Context jakarta.ws.rs.core.SecurityContext context) throws JsonProcessingException{
    UserJSON user = userService.getUserDetails(new UserJSON(context.getUserPrincipal().getName()));
    if (user.getUserSecurity() == null)
      return Response.status(204).build(); 
    String refreshToken = user.getUserSecurity().getRefreshTkn();;
    UserSecurityJSON secDetails = this.userService.getSecurityDetails(user);
    if (secDetails == null || secDetails.getRefreshTkn() != refreshToken)
      return Response.status(401).entity("Invalid refresh Tokens").build(); 
      UserSecurityJSON securityDetails = new UserSecurityJSON();
      securityDetails = JwtAuthIdentityStore.createUserAccessToken(secDetails.getUser());
      securityDetails = JwtAuthIdentityStore.createUserRefreshTokens(secDetails.getUser());
      user.setUserSecurity(securityDetails);
      return Response.ok(mapper.writeValueAsString(securityDetails)).build();
  }

  @PermitAll
  @OPTIONS
  @Path("Refresh/All")
  public Response optionRefreshAallTknsOptions() {
    return Response.ok().build();
  }

  @PermitAll
  @OPTIONS
  @Path("Logout")
  public Response optionsLogout() {
    return Response.ok().build();
  }  

  
  @GET
  @Path("Logout")
  public Response logout() throws JsonProcessingException{
    UserJSON user = new UserJSON(this.securityContext.getUserPrincipal().getName());
    UserSecurityJSON userSec = new UserSecurityJSON();
    userSec.setUser(user);
    userService.setSecurityDetails(userSec);
    return Response.ok().build();
  }


  @RolesAllowed({"User","Admin"})
  @PUT
  @Consumes("application/json")
  @Path("ChangePassword")
  public Response changePassword(String userSecurityJSON, @Context SecurityContext context) throws JsonProcessingException{
    UserSecurityJSON userSecurity = mapper.readValue(userSecurityJSON, UserSecurityJSON.class);
    UserJSON user = userService.getUserDetails(new UserJSON(context.getUserPrincipal().getName()));
    UserSecurityJSON tkns = JwtAuthIdentityStore.createUserRefreshTokens(user);
    tkns.setPassword(userSecurity.getPassword()); 
    user.setUserSecurity(tkns);
    tkns.setUser(user);
    userService.setSecurityDetails(tkns);
    userService.setUserPassword(tkns);
    tkns.setPassword(null);
    tkns.setUser(null);
    return Response.ok().entity(tkns).build();
  }

  @PermitAll
  @OPTIONS
  @Path("ChangePassword")
  public Response ChangePasswordOptions() {
    return Response.ok().build();
  }

  @PermitAll
  @PUT
  @Path("ResetPassword")
  @Consumes({"application/json"})
  public Response resetPassword(String userJSON) throws JsonProcessingException{
    UserJSON user = mapper.readValue(userJSON, UserJSON.class);
    if((user.getEmail() != null &&  userService.emailExists(user)) || user.getUsername()!= null){
      user = userService.getUserDetails(user);
      UserSecurityJSON secDetails = JwtAuthIdentityStore.createUserAccessToken(user);
      user.setUserSecurity(secDetails);
      //cacheUserPasswordReset(user);
      StringBuilder sb = new StringBuilder();
      sb.append("<!DOCTYPE html>");
      sb.append("<html lang=\"en\">");
      sb.append("<head>");
      sb.append("<meta charset=\"UTF-8\">");
      sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
      sb.append("<title>Reset Password</title>");
      sb.append("</head>");
      sb.append("<body style=\"text-align: center; padding: 50px; font-family: Arial, sans-serif;\">");
      sb.append("<a");
      sb.append(" ");
      sb.append("href='http://localhost:5173/password-reset?token=" + secDetails.getAccessTkn() + "'");
      sb.append(" ");
      sb.append("style=\"");
      sb.append("display: inline-block;");
      sb.append(" ");
      sb.append("padding: 12px 24px;");
      sb.append(" ");
      sb.append("background-color: #4CAF50;");
      sb.append(" ");
      sb.append("color: white;");
      sb.append(" ");
      sb.append("text-decoration: none;");
      sb.append(" ");
      sb.append("font-size: 16px;");
      sb.append(" ");
      sb.append("font-weight: bold;");
      sb.append(" ");
      sb.append("border-radius: 8px;");
      sb.append(" ");
      sb.append("border: none;");
      sb.append(" ");
      sb.append("box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);");
      sb.append(" ");
      sb.append("transition: transform 0.2s, box-shadow 0.2s;");
      sb.append("\"");
      sb.append(" ");
      sb.append("onmouseover=\"this.style.boxShadow='0 6px 8px rgba(0, 0, 0, 0.2)'\"");
      sb.append(" ");
      sb.append("onmouseout=\"this.style.boxShadow='0 4px 6px rgba(0, 0, 0, 0.1)'\"");
      sb.append(" ");
      sb.append("onmousedown=\"this.style.transform='scale(0.95)'\"");
      sb.append(" ");
      sb.append("onmouseup=\"this.style.transform='scale(1)'\"");
      sb.append(" ");
      sb.append(">");
      sb.append("Set Up Password");
      sb.append("</a>");
      sb.append("</body>");
      sb.append("</html>");
      EmailSender.sendPlainTextEmail("hyperforbics@gmail.com", user.getEmail(), "Jinice Email verification", sb.toString(), false);
      return Response.ok().build();
    }
    return Response.status(403).build();
  }

  @PermitAll
  @OPTIONS
  @Path("ResetPassword")
  public Response optionResetPasswordOptions() {
    return Response.ok().build();
  }

  @PUT
  @PermitAll
  @Path("Verify/ResetPassword")
  public Response verifyPasswordReset(String userSecurityJSON) throws JsonProcessingException
  //, JwtException
  {
    UserSecurityJSON secDetails = mapper.readValue(userSecurityJSON, UserSecurityJSON.class);
    UserJSON userInClaims = JwtAuthIdentityStore.verifyToken(secDetails.getAccessTkn());
    if(userInClaims == null)
      return Response.status(401).build();
    UserJSON userInfo = userService.getUserDetails(userInClaims);
    UserSecurityJSON tkns = JwtAuthIdentityStore.createUserRefreshTokens(userInfo);
    userService.setSecurityDetails(tkns);
    tkns.setPassword(secDetails.getPassword());
    userService.setUserPassword(tkns);
    secDetails.setPassword(null);
    secDetails.setUser(null);
    return Response.status(200).entity(secDetails).build();

  }

  
  @OPTIONS
  @Path("Verify/ResetPassword")
  public Response optionsVerifyPassword() {
    return Response.ok().build();
  }
  /* 
  @PermitAll
  @PUT
  @Path("ResetPassword")
  @Consumes({"application/json"})
  public Response resetPasswordWithEmail(String userJson) throws JsonProcessingException{
    UserJSON user = mapper.readValue(userJson, UserJSON.class);
    user = this.userService.getUserDetails(user);
    if (user == null)
      return Response.status(403).entity("User not found").build(); 
    if (!this.userService.emailExists(user))
      return Response.status(403).entity("Email does not exist").build(); 
    UserSecurityJSON userSecurityJSON = JwtAuthIdentityStore.createUserAccessToken(new UserJSON(user.getUsername()));
    user.setUserSecurity(userSecurityJSON);
    //cacheGuestContent(user);
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<body>");
    sb.append("<h4>Click on the link to Join our community</h4>");
    sb.append("<a href='http://localhost:5173/resetPassword/" + userSecurityJSON.getAccessTkn() + "'>Reset Password</a>");
    sb.append("</body>");
    sb.append("</html>");
    EmailSender.sendPlainTextEmail("hyperforbics@gmail.com", user.getEmail(), "Jinice Password Reset", sb
        .toString(), true);
    return Response.ok().build();
  }
*/

  
private UserSecurityJSON rotateAccesToken(UserJSON user){
    UserSecurityJSON secDetails = userService.getSecurityDetails(user);
    try{
      UserJSON userInClaims = null;
      if(secDetails.getRefreshTkn() != null){
        userInClaims = JwtAuthIdentityStore.verifyToken(secDetails.getRefreshTkn());
        if(userInClaims != null){  
          String refreshTkn = secDetails.getRefreshTkn();
          secDetails = JwtAuthIdentityStore.createUserAccessToken(user);
          secDetails.setRefreshTkn(refreshTkn);
        }
        else{
          secDetails = JwtAuthIdentityStore.createUserRefreshTokens(user);
        }
        user.setUserSecurity(secDetails);
        return secDetails;      
      }
      else
        throw new JwtException("Expired Access Token for "+ user.getUsername());
    }
    catch(JwtException ex){
      secDetails = JwtAuthIdentityStore.createUserRefreshTokens(user);;
      userService.setSecurityDetails(secDetails);
      return secDetails;
    }
  }

  @RolesAllowed({"User", "Admin"})
  @Path("/login")
  @GET
  //@PermitAll
  public Response login(@Context jakarta.ws.rs.core.SecurityContext context) throws JsonProcessingException{
    try {
      FilterProvider provider = Resource.setFilters(getPublicViewFilters());
      //mapper.setSerializationInclusion(Include.NON_DEFAULT);
      this.mapper.setFilterProvider(provider);
      UserJSON user = userService.getUserDetails(new UserJSON(context.getUserPrincipal().getName()));
      UserSecurityJSON secDetails = rotateAccesToken(user);
      user = JwtAuthIdentityStore.verifyToken(secDetails.getAccessTkn());
      UserLoginJSON userDetails = new UserLoginJSON(user, secDetails.getAccessTkn(), secDetails.getRefreshTkn());
      return Response.ok().entity(mapper.writeValueAsString(userDetails)).build();
    } catch (NoResultException ex) {
      return Response.status(401).entity("User not signed In").build();
    } 
  }
  
  private UserJSON getGuestContentFromCache(String username) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    Cache<String, String> userCache = cacheManager.getCache("guestCache", String.class, String.class);
    try {
      return (UserJSON)mapper.readValue((String)userCache.get(username), UserJSON.class);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
      return null;
    } 
  }
  
  @PermitAll
  @OPTIONS
  @Path("/login")
  public Response loginOptions() {
    return Response.ok().build();
  }
  
  private void cacheGuestContent(UserJSON user) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    Cache<String, String> userCache = cacheManager.getCache("guestCache", String.class, String.class);
    try {
      String userDetails = mapper.writeValueAsString(user);
      if (userDetails == null)
        throw new IllegalStateException("user is null"); 
      userCache.put(user.getUsername(), userDetails);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    } 
  }
  
  private void cacheNewUserContent(UserJSON user) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    Cache<String, String> userCache = cacheManager.getCache("usernameCache", String.class, String.class);
    try {
      userCache.put(user.getUsername(), this.mapper.writeValueAsString(user));
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    } 
  }


  private void cacheUserPasswordReset(UserJSON user) {
      CacheManager cacheManager = this.cacheHelper.getCacheManager();
      Cache<Long, String> userCache = cacheManager.getCache("userPasswordResetTokenCache", Long.class, String.class);
      try {
        userCache.put(user.getUserId(), this.mapper.writeValueAsString(user));
      } catch (JsonProcessingException ex) {
        ex.printStackTrace();
      } 
  }

  private UserJSON getUserPasswordToFromCache(Long userId) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    Cache<Long, String> userCache = cacheManager.getCache("userPasswordResetTokenCache", Long.class, String.class);
    try {
      return (UserJSON)mapper.readValue((String)userCache.get(userId), UserJSON.class);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
      return null;
    } 
  }
}