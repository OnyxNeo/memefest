package com.memefest.Jaxrs.Providers;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Dependent
@Priority(1000)
public class AuthenticationFilter implements ContainerRequestFilter {
  @Inject
  private SecurityContext secContext;
  
  @Context
  private HttpServletRequest request;
  
  @Context
  private HttpServletResponse response;
  
  @Context
  private ResourceInfo resourceInfo;
  
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Method method = this.resourceInfo.getResourceMethod();
    if (method.isAnnotationPresent(DenyAll.class))
      throw new AuthenticationDenied("Not enough permission to access resource"); 
    RolesAllowed allowed = method.<RolesAllowed>getAnnotation(RolesAllowed.class);
    if (allowed != null)
      performAuthorization(allowed.value()); 
    if ((this.resourceInfo.getResourceClass().isAnnotationPresent(PermitAll.class) && !(method
      .isAnnotationPresent(RolesAllowed.class))) || method.isAnnotationPresent(PermitAll.class))
      return; 
    if (!isAuthenticated(requestContext))
      throw new AuthenticationDenied("Authentication required");
    requestContext.setSecurityContext(new JaxrsSecurityContext(secContext.getCallerPrincipal(), true, "BASIC/JWT"));
  }
  
  private void performAuthorization(String[] roles) {
    AuthenticationStatus status = this.secContext.authenticate(this.request, this.response, null);
    if(status != AuthenticationStatus.SUCCESS)
      throw new AuthenticationDenied("Authentication failed"); 
    for (int i = 0; i < roles.length; i++) {
      if (this.request.isUserInRole(roles[i])){
        return; 
      }
    } 
    throw new AuthenticationDenied("Not enough permission to access resource");
  }
  
  private boolean isAuthenticated(ContainerRequestContext context) {
    return (context.getSecurityContext().getUserPrincipal() != null);
  }
}
