package com.memefest.Services.Impl;

import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.config.PersistenceUnit;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.Server;

import com.memefest.Services.DataSourceOps;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

 
/* 
@DataSourceDefinition(
  name = "java:app/jdbc/memefest/MemefestBackendApi",
  url = "jdbc:sqlserver://;servername=CHHUMBUCKET/JOMBA;DatabaseName=Memefest;trustServerCertificate=true;encrypt=false",
  className = "com.microsoft.sqlserver.jdbc.SQLServerDataSource",
  portNumber = 1433
)
*/
@Singleton(name = "DatasourceService")
@Startup
//@ApplicationScoped
public class DatasourceService implements DataSourceOps
//implements DataSourceOps
    {
    
    private String databaseName;
    private String serverName;
    private String instanceName;
    protected String username;
    protected String password;
    private int portNumber;
    private String encrypt;
    private boolean trustServerCertificate;

    @jakarta.persistence.PersistenceUnit(unitName = "PostServicePersistenceUnit")
    private EntityManagerFactory factory;

    private PersistenceProvider provider;
    private PersistenceUnit unit;
    Map<String, Object> memeProps;
    PersistenceFactoryBase pf = new PersistenceFactoryBase();
    PersistenceContext pc;


    
    @PostConstruct
    public void initialize(){        
        databaseName = "Memefest";
        serverName = "localhost";
        instanceName = "CHHUMBUCKET";
        username = "Neutron";
        password = "ScoobyDoo24";
        encrypt = "false";
        portNumber = 1433;
        trustServerCertificate = true;
        
        String dataSourceName = "jdbc/DataSource/PostService";
        String unitName = "PostServicePersistenceUnit";  
        SQLServerDataSource ssDataSource = new SQLServerDataSource();
        ssDataSource.setDatabaseName(databaseName);
        //ssDataSource.setURL("jdbc:sqlserver://;servername=localhost;DatabaseName=Memefest;trustServerCertificate=true;encrypt=false");
        ssDataSource.setTrustServerCertificate(trustServerCertificate);
        ssDataSource.setServerName(serverName);
        ssDataSource.setInstanceName(instanceName);
        ssDataSource.setUser(username);
        ssDataSource.setPassword(password);
        ssDataSource.setPortNumber(portNumber);
        //ssDataSource.setEncrypt(encrypt);
        try{
            Context context = new InitialContext();   
            try {
                //context = (Context) context.lookup("java:app");
                context.rebind(dataSourceName,ssDataSource);
            }catch (NamingException e) {
                try {
                    e.printStackTrace();
                    context.bind(dataSourceName, ssDataSource);
                    context.lookup(dataSourceName);
                } catch (NamingException ec) {
                    throw new RuntimeException(ec);
                }
            }
        }catch(NamingException ex){
            throw new RuntimeException(ex);
        }
        Map<String, Object> memeProps = new HashMap<>();
            //memeProps.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.JTA.name());
            //memeProps.put(PersistenceUnitProperties.TARGET_SERVER, TargetServer.None);
            memeProps.put(PersistenceUnitProperties.JDBC_USER, username);
            memeProps.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
            //memeProps.put(PersistenceUnitProperties.CONNECTION_POOL_JTA_DATA_SOURCE, "DataSource/Memefest");
            //memeProps.put(PersistenceUnitProperties.JTA_DATASOURCE, dataSourceName);
            //memeProps.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS, unitName);
            //memeProps.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            org.eclipse.persistence.jpa.config.PersistenceUnit unit = new PersistenceUnitImpl(unitName);
            unit.setProvider("org.eclipse.persistence.jpa.PersistenceProvider");
        //unit.setJtaDataSource("DataSource/Memefest" );

        unit.setClass("com.memefest.DataAccess.UserSecurity");
        unit.setClass("com.memefest.DataAccess.CategoryFollower");
        unit.setClass("com.memefest.DataAccess.Category");
        unit.setClass("com.memefest.DataAccess.Event");
        unit.setClass("com.memefest.DataAccess.EventCategory");
        unit.setClass("com.memefest.DataAccess.EventImage");
        unit.setClass("com.memefest.DataAccess.EventNotification");
        unit.setClass("com.memefest.DataAccess.EventPost");
        unit.setClass("com.memefest.DataAccess.EventPostNotification");
        unit.setClass("com.memefest.DataAccess.EventVideo");
        unit.setClass("com.memefest.DataAccess.FollowNotification");
        unit.setClass("com.memefest.DataAccess.Image");
        unit.setClass("com.memefest.DataAccess.Post");
        unit.setClass("com.memefest.DataAccess.PostCategory");
        unit.setClass("com.memefest.DataAccess.PostImage");
        unit.setClass("com.memefest.DataAccess.PostNotification");
        unit.setClass("com.memefest.DataAccess.PostReply");
        unit.setClass("com.memefest.DataAccess.PostVideo");
        unit.setClass("com.memefest.DataAccess.JokeOfDay");
        unit.setClass("com.memefest.DataAccess.Sponsor");
        unit.setClass("com.memefest.DataAccess.JokeOfDayPost");
        unit.setClass("com.memefest.DataAccess.PostTaggedUser");
        unit.setClass("com.memefest.DataAccess.RepostTaggedUser");
        unit.setClass("com.memefest.DataAccess.Interact");
        unit.setClass("com.memefest.DataAccess.Repost");
        unit.setClass("com.memefest.DataAccess.SubCategory");
        unit.setClass("com.memefest.DataAccess.Topic");
        unit.setClass("com.memefest.DataAccess.TopicCategory");
        unit.setClass("com.memefest.DataAccess.TopicFollower");
        unit.setClass("com.memefest.DataAccess.TopicFollowNotification");
        unit.setClass("com.memefest.DataAccess.TopicImage");
        unit.setClass("com.memefest.DataAccess.TopicPost");
        unit.setClass("com.memefest.DataAccess.TopicPostNotification");
        unit.setClass("com.memefest.DataAccess.TopicVideo");
        unit.setClass("com.memefest.DataAccess.User");
        unit.setClass("com.memefest.DataAccess.UserAdmin");
        unit.setClass("com.memefest.DataAccess.UserFollower");
        unit.setClass("com.memefest.DataAccess.Video");

        unit.setExcludeUnlistedClasses(false);
        //unit.setName("Memefest");
        unit.setTransactionType(PersistenceUnitTransactionType.JTA);     
        unit.setName(unitName);
        unit.setJtaDataSource(dataSourceName);
        //unit.setProperty(PersistenceUnitProperties.SESSION_NAME, "DefaultMemefestSession");

        DatabaseLogin databaseLogin = new DatabaseLogin();
        databaseLogin.setDatasourcePlatform(new SQLServerPlatform());
        databaseLogin.useDataSource(dataSourceName);
        databaseLogin.setPassword(password);
        databaseLogin.setUserName(username);
        databaseLogin.setEncryptedPassword(password);

        
        Project project = new Project(databaseLogin);
        SessionManager manager = SessionManager.getManager();        
        Server server = project.createServerSession();

        server.login();
        
        server.setName("ServerSession");
        manager.addSession(server);

        DatabaseSession databaseSession = new DatabaseSessionImpl(databaseLogin);
        //databaseSession.login();
        //databaseSession.set

        manager.setDefaultSession(databaseSession);
        //manager.addSession("DefaultMemefestSesson", databaseSession);
        
        
        provider = new PersistenceProvider();
        //PersistenceProvider provider = new PersistenceProvider();
        //persistenceUnit.setExcludeUnlistedClasses(false);
        //persistenceUnit.getPersistenceUnitInfo().
        this.factory = provider.createContainerEntityManagerFactory(unit.getPersistenceUnitInfo(), memeProps);
        //EntityManagerFactoryWrapper wrapper = new EntityManagerFactoryWrapper(factory
        //pf.get        
        //System.out.println(pf.getPersistenceContextNames());
        //PersistenceProviderResolver resolver = new com.memefest.Services.Impl.PersistenceProviderResolver();
        //PersistenceProviderResolverHolder.setPersistenceProviderResolver(resolver);

        //PersistenceProvider provider = new PersistenceProvider();
        //persistenceUnit.setExcludeUnlistedClasses(false);
        //persistenceUnit.getPersistenceUnitInfo().
        
          
        //EntityManagerFactoryWrapper wrapper = new EntityManagerFactoryWrapper(factory
            //entityManager.joinTransaction();
      
        /*try {
            properties.load(this.getClass().getResource("/resources/database.properties").openStream());
            this.databaseName = properties.getProperty("databaseName");
            this.serverName = properties.getProperty("serverName");
            this.instanceName =properties.getProperty("instanceName");
            this.username = properties.getProperty("username");
            this.password = properties.getProperty("password");
            this.encrypt =  properties.getProperty("encrypted");
            this.portNumber = (int) properties.get("portNumber");
            this.trustServerCertificate  = (boolean) properties.get("trustServerCertificate");

        } catch (IOException | NullPointerException e) {

        }
        */
        
        EntityManager manager2 = factory.createEntityManager();
        manager2.isOpen();
    }

    public PersistenceContext getPersistenceContext(){
        return this.pc;
    }
    
    public EntityManager getEntityManager(String unitName){
        unit.setName(unitName);
        this.factory = provider.createContainerEntityManagerFactory(unit.getPersistenceUnitInfo(), memeProps);      
        return this.factory.createEntityManager();
    }

    //@Resource(name ="memefest/MemefestBackendApi")
    public void setJNDIDataSource(DataSource dataSource, String name) throws NamingException{
        Context context = new InitialContext();
        context.rebind(name, context);
    }
 
    
    public DataSource getJNDIDataSource(String name)throws NamingException{
        Context context = new InitialContext();
        return (DataSource) context.lookup(name);
    }

    public void createJNDIDataSource(DataSource dataSource, String jndiName) throws NamingException{
        try {
            Context context = new InitialContext();
            context.bind(jndiName, dataSource);    
        } catch (NamingException ns) {
            setJNDIDataSource(dataSource, jndiName);   
        }
    }

    public void setDataSource(String dataSourceName, DataSource dataSource){
      try{
        Context context = new InitialContext();
        if(dataSource == null){
            dataSource = getDefaultDataSource();
        }
        try {
            context.rebind(dataSourceName, dataSource);
        } catch (NamingException e) {
            try {
                context.bind(dataSourceName, dataSource);
                //ssDataSource = (DataSource) context.lookup("DataSource/Memefest");
            } catch (NamingException ec) {
                throw new RuntimeException(ec);
            }
        }
        }catch(NamingException ex){
            throw new RuntimeException(ex);
        }
    }

    public String getPassword(){
        return this.password;
    }
    
    public String getUser(){
        return this.username;
    }

    public SQLServerDataSource getDefaultDataSource() {
        SQLServerDataSource ssDataSource = new SQLServerDataSource();
        ssDataSource.setDatabaseName(this.databaseName);
        ssDataSource.setTrustServerCertificate(this.trustServerCertificate);
        ssDataSource.setServerName(this.serverName);
        ssDataSource.setInstanceName(this.instanceName);
        ssDataSource.setUser(this.username);
        ssDataSource.setPassword(this.password);
        ssDataSource.setPortNumber(this.portNumber);
        ssDataSource.setEncrypt(this.encrypt);

        return (SQLServerDataSource) ssDataSource;
    }

    
    public EntityManagerFactory getEntityManagerFactory(){
        return this.factory;
    }
/* 
    public EntityManagerFactory createEntityManagerFactory(String unitName, Map<String,Object> properties) throws NamingException{
        
        persistenceUnit.setName(unitName);
        PersistenceProvider provider = new PersistenceProvider();
        
        EntityManagerFactory factory = provider.createContainerEntityManagerFactory(persistenceUnit.getPersistenceUnitInfo(), properties); 

        return factory;
    }
*/
    //@Produces 
    //@Named("EntityManagerFactory")   
    //@ApplicationScoped
    //@jakarta.persistence.PersistenceUnit(unitName = "Memefest")
    
    //@Alternative
    public EntityManagerFactory createDefaultJNDIEntityManagerFactory(String dataSourceName, 
                                String persistenceUnitName,
                                Map<String, Object> memeProps, PersistenceProvider provider) throws NamingException{
        //DataSource dataSource = getJNDIDataSource("DataSource/Memefest");
        //persistenceUnit.setName("userServiceData");
        PersistenceUnit unit = new PersistenceUnitImpl(persistenceUnitName);
        unit.setProvider("org.eclipse.persistence.jpa.PersistenceProvider");
        //unit.setJtaDataSource("DataSource/Memefest" );

        unit.setClass("com.memefest.DataAccess.UserSecurity");
        unit.setClass("com.memefest.DataAccess.CategoryFollower");
        unit.setClass("com.memefest.DataAccess.Category");
        unit.setClass("com.memefest.DataAccess.Event");
        unit.setClass("com.memefest.DataAccess.EventCategory");
        unit.setClass("com.memefest.DataAccess.EventImage");
        unit.setClass("com.memefest.DataAccess.EventNotification");
        unit.setClass("com.memefest.DataAccess.EventPost");
        unit.setClass("com.memefest.DataAccess.EventPostNotification");
        unit.setClass("com.memefest.DataAccess.EventVideo");
        unit.setClass("com.memefest.DataAccess.FollowNotification");
        unit.setClass("com.memefest.DataAccess.Image");
        unit.setClass("com.memefest.DataAccess.Post");
        unit.setClass("com.memefest.DataAccess.PostCategory");
        unit.setClass("com.memefest.DataAccess.PostImage");
        unit.setClass("com.memefest.DataAccess.PostNotification");
        unit.setClass("com.memefest.DataAccess.PostReply");
        unit.setClass("com.memefest.DataAccess.PostVideo");
        unit.setClass("com.memefest.DataAccess.JokeOfDay");
        unit.setClass("com.memefest.DataAccess.Sponsor");
        unit.setClass("com.memefest.DataAccess.JokeOfDayPost");
        unit.setClass("com.memefest.DataAccess.PostTaggedUser");
        unit.setClass("com.memefest.DataAccess.RepostTaggedUser");
        unit.setClass("com.memefest.DataAccess.Interact");
        unit.setClass("com.memefest.DataAccess.Repost");
        unit.setClass("com.memefest.DataAccess.SubCategory");
        unit.setClass("com.memefest.DataAccess.Topic");
        unit.setClass("com.memefest.DataAccess.TopicCategory");
        unit.setClass("com.memefest.DataAccess.TopicFollower");
        unit.setClass("com.memefest.DataAccess.TopicFollowNotification");
        unit.setClass("com.memefest.DataAccess.TopicImage");
        unit.setClass("com.memefest.DataAccess.TopicPost");
        unit.setClass("com.memefest.DataAccess.TopicPostNotification");
        unit.setClass("com.memefest.DataAccess.TopicVideo");
        unit.setClass("com.memefest.DataAccess.User");
        unit.setClass("com.memefest.DataAccess.UserAdmin");
        unit.setClass("com.memefest.DataAccess.UserFollower");
        unit.setClass("com.memefest.DataAccess.Video");

        unit.setExcludeUnlistedClasses(false);
        //unit.setName("Memefest");
        //unit.setTransactionType(PersistenceUnitTransactionType.JTA);     
        unit.setName(persistenceUnitName);
        unit.setJtaDataSource(dataSourceName);
        //PersistenceProvider provider = new PersistenceProvider();
        //persistenceUnit.setExcludeUnlistedClasses(false);
        //persistenceUnit.getPersistenceUnitInfo().
        EntityManagerFactory factory = provider.createContainerEntityManagerFactory(unit.getPersistenceUnitInfo(), memeProps);
        //EntityManagerFactoryWrapper wrapper = new EntityManagerFactoryWrapper(factory);
        return factory;
    }

/* 
    @SessionScoped
    @Named("UserInfo")
    @Produces
    @Alternative
    public DataSourceCredentials getSecurityInfo(){
        DataSourceCredentials credentials = new DataSourceCredentials(username, password);
        return credentials;
    }
    */


    // use cdi and eclipselink rs to manage persistence context creation instead of ejb
    /* 
    public PersistenceContextFactory createPersistenceContextFactory(){

        PersistenceContextFactory factory = new PersistenceContextFactory() {
            public void closePersistenceContext(String name){

            }

            public void close(){

            }

            public org.eclipse.persistence.jpa.rs.PersistenceContext get(String context, URI uri, String version, Map<String,Object> properties){

            }



        };

        factory.
    }
    */
}