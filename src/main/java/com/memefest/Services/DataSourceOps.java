package com.memefest.Services;

import org.eclipse.persistence.jpa.rs.PersistenceContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

//@Local
public interface DataSourceOps{
    //public void setDataSource(String dataSourceName, DataSource dataSource);

    //public SQLServerDataSource getDefaultDataSource();

    public EntityManager getEntityManager(String persistenceUnitName);

    public PersistenceContext getPersistenceContext();

    public EntityManagerFactory getEntityManagerFactory();

    //public EntityManagerFactory createDefaultJNDIEntityManagerFactory(String dataSourceName, String persistenceUnitName, Map<String, Object> properties) throws NamingException;

    //public String getPassword();

    //public String getUser();
}   
