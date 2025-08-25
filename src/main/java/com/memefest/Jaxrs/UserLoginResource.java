package com.memefest.Jaxrs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.memefest.CacheHelper.CacheHelper;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.UserSecurityJSON;
import com.memefest.Email.EmailSender;
import com.memefest.Security.JwtAuthIdentityStore;
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
import org.ehcache.Cache;
import org.ehcache.CacheManager;

@PermitAll
@RequestScoped
@Path("SignIn")
public class UserLoginResource{
  @Inject
  private UserSecurityService userService;
  
  @Inject
  private CacheHelper cacheHelper;
  
  @Context
  private jakarta.ws.rs.core.SecurityContext securityContext;

  private ObjectMapper mapper;

  protected UserLoginResource(){
    this.mapper = new ObjectMapper();
    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
    SimpleFilterProvider provider = new SimpleFilterProvider();
    provider.addFilter("UserPublicView", filter);
    this.mapper.setFilterProvider(provider);
    this.mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
  }


  @PermitAll
  @PUT
  @Path("Verify-email")
  @Consumes({"application/x-www-form-urlencoded"})
  public Response verifyEmail(@BeanParam UserInput input) {
    UserJSON user = this.userService.getUserDetails(new UserJSON(input.getEmail(), input.getUsername(), 0, false, input.getFirstname(), input.getLastname(), null));
    if (user != null)
      return Response.status(403).entity("Username already exists, try a different username").build(); 
    if (this.userService.emailExists(new UserJSON(input.getUsername(), input.getEmail())))
      return Response.status(403).entity("Someone registered with this email already").build(); 
    UserJSON guestDetails = new UserJSON(input.getUsername(), input.getEmail(), input.getContacts(), input.getFirstname(), input.getLastname());
    UserSecurityJSON securityDetails = JwtAuthIdentityStore.createUserAccessToken(guestDetails);
    guestDetails.setUserSecurity(securityDetails);
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
    return Response.ok().build();
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
  public Response signUp(UserSecurityJSON user) {
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
      UserSecurityJSON userSecurity = new UserSecurityJSON(null, user.getPassword(), null, userInClaims);
      userSecurity.setAccessTkn(JwtAuthIdentityStore.createUserAccessToken(userInClaims).getAccessTkn());
      userSecurity.setRefreshTkn(JwtAuthIdentityStore.createUserRefreshTokens(userInClaims).getRefreshTkn());
      userInClaims.setUserSecurity(userSecurity);
      cacheNewUserContent(userInClaims);
      this.userService.createUser(userInClaims);
      userSecurity.setPassword(null);
      return Response.ok().entity(userSecurity).build();
    } 
    return Response.status(401).entity("Bad token").build();
  }
  
  @PermitAll
  @OPTIONS
  @Path("/Sign-Up")
  public Response optionsSignUp() {
    return Response.ok().build();
  }
  
  @PermitAll
  @PUT
  @Path("Refresh")
  @Consumes({"application/json"})
  public Response refreshToken(UserJSON user) {
    if (user.getUserSecurity() == null)
      return Response.status(204).build(); 
    String refreshToken = user.getUserSecurity().getRefreshTkn();
    UserJSON refreshClaims = JwtAuthIdentityStore.verifyToken(refreshToken);
    String accessToken = user.getUserSecurity().getAccessTkn();
    UserJSON accessClaims = JwtAuthIdentityStore.verifyToken(accessToken);
    UserSecurityJSON secDetails = this.userService.getSecurityDetails(user);
    if (secDetails == null || secDetails.getRefreshTkn() != refreshToken)
      return Response.status(401).entity("Invalid refresh Tokens").build(); 
    if (refreshClaims != null && accessClaims != null) {
      UserSecurityJSON securityDetails = user.getUserSecurity();
      securityDetails = JwtAuthIdentityStore.createUserAccessToken(secDetails.getUser());
      accessClaims.setUserSecurity(securityDetails);
      securityDetails = JwtAuthIdentityStore.createUserRefreshTokens(secDetails.getUser());
      user.setUserSecurity(securityDetails);
      return Response.ok(user).build();
    } 
    return Response.status(401).entity("refresh or access tokens not recognised").build();
  }
  
  @RolesAllowed({"User", "Admin"})
  @PUT
  @Path("User/ResetPassword")
  @Consumes({"application/json"})
  public Response resetPassword(UserJSON user) {
    UserSecurityJSON securityDetails = new UserSecurityJSON(null, user.getUserSecurity().getPassword(), null, new UserJSON(0, this.securityContext.getUserPrincipal().getName()));
    user.setUserSecurity(securityDetails);
    this.userService.setUserPassword(securityDetails);
    return Response.status(200).entity(user).build();
  }
  
  @OPTIONS
  @Path("User/ResetPassword")
  public Response optionsResetPassword() {
    return Response.ok().build();
  }
  
  @PermitAll
  @PUT
  @Path("ResetPassword")
  @Consumes({"application/json"})
  public Response resetPasswordWithEmail(UserJSON user) {
    user = this.userService.getUserDetails(user);
    if (user == null)
      return Response.status(403).entity("User not found").build(); 
    if (!this.userService.emailExists(user))
      return Response.status(403).entity("Email does not exist").build(); 
    UserSecurityJSON userSecurityJSON = JwtAuthIdentityStore.createUserAccessToken(new UserJSON(user.getUsername()));
    user.setUserSecurity(userSecurityJSON);
    cacheGuestContent(user);
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
  
  @OPTIONS
  @Path("ResetPassword")
  public Response optionsResetPasswordWithEmail() {
    return Response.ok().build();
  }
  
  @RolesAllowed({"User", "Admin"})
  @Path("/login")
  @GET
  public Response login() {
    try {
      UserSecurityJSON uSecurityJSON = this.userService
                                      .getSecurityDetails(
                                        new UserJSON(this.securityContext.getUserPrincipal().getName()));
      if (uSecurityJSON != null)
        return Response.ok().entity(uSecurityJSON).build(); 
      return Response.status(401).entity("User not signed In").build();
    } catch (NoResultException ex) {
      return Response.status(401).entity("User not signed In").build();
    } 
  }
  
  private UserJSON getGuestContentFromCache(String username) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    Cache<String, String> userCache = cacheManager.getCache("guestCache", String.class, String.class);

    try {
      return (UserJSON)this.mapper.readValue((String)userCache.get(username), UserJSON.class);
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
      String userDetails = this.mapper.writeValueAsString(user);
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
}