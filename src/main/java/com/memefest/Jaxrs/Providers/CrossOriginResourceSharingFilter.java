package com.memefest.Jaxrs.Providers;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Dependent
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {
  public void filter(ContainerRequestContext creq, ContainerResponseContext cresp) {
    cresp.getHeaders().putSingle("Access-Control-Allow-Origin", "http://localhost:5173");
    cresp.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
    cresp.getHeaders().putSingle("Access-Control-Allow-Methods", "GET,DELETE, PUT, HEAD, OPTIONS");
    cresp.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, Origin, Referer, Host");
    cresp.getHeaders().putSingle("Access-Control-Max-Age", "1209600");
  }
}
