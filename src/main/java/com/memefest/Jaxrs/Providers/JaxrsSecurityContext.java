package com.memefest.Jaxrs.Providers;

import java.security.Principal;
import jakarta.ws.rs.core.SecurityContext;


//@Provider
public class JaxrsSecurityContext implements SecurityContext{
    
    private boolean isSecure;

    private String authScheme;

    private Principal caller;

    public JaxrsSecurityContext(Principal callerPrincipal, boolean secure, String authScheme){
        this.isSecure = secure;
        this.caller = callerPrincipal;
        this.authScheme = authScheme;
    }

    public boolean isSecure(){
            return isSecure;
    }

    public Principal getUserPrincipal(){
        return caller;
    }

    public boolean isUserInRole(String role){
        return false;
    }

    public String getAuthenticationScheme(){
        return this.authScheme;
    }

}
