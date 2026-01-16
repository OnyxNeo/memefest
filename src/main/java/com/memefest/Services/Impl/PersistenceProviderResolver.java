package com.memefest.Services.Impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.spi.PersistenceProvider;

public class PersistenceProviderResolver implements jakarta.persistence.spi.PersistenceProviderResolver{    

    org.eclipse.persistence.jpa.PersistenceProvider provider = new org.eclipse.persistence.jpa.PersistenceProvider();
    List<PersistenceProvider> list =new ArrayList<PersistenceProvider>();
    
    PersistenceProviderResolver(){
        list.add(provider);
    }

    public void clearCachedProviders(){

    }

    public List<PersistenceProvider> getPersistenceProviders(){
        return list;
    }
}
