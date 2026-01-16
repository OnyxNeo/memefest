package com.memefest.Services.Impl;

import java.io.Serializable;

import jakarta.security.enterprise.credential.UsernamePasswordCredential;

public class DataSourceCredentials  extends UsernamePasswordCredential implements Serializable{

     public DataSourceCredentials(String caller, String password) {
        super("Neutron", "ScoobyDoo24");
     }

     public DataSourceCredentials() {      
         super("Neutron", "ScoobyDoo24");
        
     }
}
