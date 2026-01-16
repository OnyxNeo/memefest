package com.memefest.Services.Impl;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.memefest.DataAccess.Image;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.Services.ImageOperations;
import com.memefest.Services.S3AccessOperations;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import jakarta.transaction.TransactionScoped;

@Stateless(name =  "ImageService")
public class ImageService implements ImageOperations{
    
    private EntityManagerFactory factory;

    @EJB
    private S3AccessOperations s3Ops;

    @TransactionScoped
    private EntityManager entityManager;

    @PostConstruct
    public void init(){
        String databaseName = "Memefest";
        String serverName = "CHHUMBUCKET";
        String instanceName = "MSSQLSERVER";
        String username = "Neutron";
        String password = "ScoobyDoo24";
        String encrypt = "false";
        int portNumber = 1433;
        boolean trustServerCertificate = true;


        String dataSourceName = "DataSource/ImageService";
        String unitName = "ImageServicePersistenceUnit";  
        
        SQLServerDataSource ssDataSource = new SQLServerDataSource();
        ssDataSource.setDatabaseName(databaseName);
        ssDataSource.setTrustServerCertificate(trustServerCertificate);
        ssDataSource.setServerName(serverName);
        ssDataSource.setInstanceName(instanceName);
        ssDataSource.setUser(username);
        ssDataSource.setPassword(password);
        ssDataSource.setPortNumber(portNumber);
        ssDataSource.setEncrypt(encrypt);
    
        try{
            Context context = new InitialContext();   
            try {

                context.rebind(dataSourceName, (DataSource) ssDataSource);
            }catch (NamingException e) {
                try {
                    context.bind(dataSourceName,(DataSource) ssDataSource);
                //ssDataSource = (DataSource) context.lookup("DataSource/Memefest");
                } catch (NamingException ec) {
                    throw new RuntimeException(ec);
                }
            }
        }catch(NamingException ex){
            throw new RuntimeException(ex);
        }
            Map<String, Object> memeProps = new HashMap<>();
            memeProps.put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.JTA.name());
            memeProps.put(PersistenceUnitProperties.TARGET_SERVER, TargetServer.None);
            memeProps.put(PersistenceUnitProperties.JDBC_USER, username);
            memeProps.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
            //memeProps.put(PersistenceUnitProperties.CONNECTION_POOL_JTA_DATA_SOURCE, "DataSource/Memefest");
            memeProps.put(PersistenceUnitProperties.JTA_DATASOURCE, dataSourceName);
            memeProps.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS, unitName);
            memeProps.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            PersistenceProvider provider = new PersistenceProvider();

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
        //PersistenceProvider provider = new PersistenceProvider();
        //persistenceUnit.setExcludeUnlistedClasses(false);
        //persistenceUnit.getPersistenceUnitInfo().
        this.factory = provider.createContainerEntityManagerFactory(unit.getPersistenceUnitInfo(), memeProps);
        //EntityManagerFactoryWrapper wrapper = new EntityManagerFactoryWrapper(factory

        this.entityManager = factory.createEntityManager();
            //entityManager.joinTransaction();
      
    }

    @PreDestroy
    public void destroy(){
        factory.close();
        entityManager.close();
    }  

    //throw a custom exception to show object was not created
    public void createImage(ImageJSON image){  
        Image imageEntity = new Image();
        imageEntity.setImg_Path(image.getImgPath());
        imageEntity.setImg_Title(image.getImgTitle());
        entityManager.persist(imageEntity);
    }

    //throw a custom exception to show object was not created
    public void editImage(ImageJSON image){
        try{
            Image imageEntity = getImageEntity(image);
            if(image.getImgPath() != null && !image.getImgPath().equalsIgnoreCase(imageEntity.getImg_Path()))
                imageEntity.setImg_Path(image.getImgPath());
            if(image.getImgId() != null){
                imageEntity.setImg_Id(image.getImgId());
            }
            if(imageEntity.getImg_Title() != null && !image.getImgTitle().equalsIgnoreCase(imageEntity.getImg_Title()))
                imageEntity.setImg_Title(image.getImgTitle());
            entityManager.merge(imageEntity);
            removeImage(image);
        }
        catch(NoResultException ex){
            createImage(image);
        }
    }

    public void removeImage(ImageJSON image){
        if(!image.isCanceled())
            return;
        try{
            Image imageEntity = getImageEntity(image);
            entityManager.remove(imageEntity);
        }
        catch(NoResultException ex){
            return;
        }
    } 

    public Image getImageEntity(ImageJSON image) throws NoResultException{
        if (image.getImgTitle() == null && image.getImgId() == null)
            throw new NoResultException("Image not found"); 
        Image imageEntity = null;
        try{
            if(image.getImgId() != null )
            imageEntity = entityManager.find(Image.class, image.getImgId());
        }
        catch(NoResultException ex){
            Query query = entityManager.createNamedQuery("Image.getImgeByTitle", Image.class);
            query.setParameter("title", "%" + image.getImgTitle() + "%");
            imageEntity = (Image) query.getSingleResult();
            return imageEntity;
        }
        throw new NoResultException("Image not found");
    }

    public ImageJSON getImageInfo(ImageJSON image){
        try{
            Image imageEntity = getImageEntity(image);
            ImageJSON imageJSON = new ImageJSON(imageEntity.getImg_Id(), 
                                    imageEntity.getImg_Path(), imageEntity.getImg_Title());
            return imageJSON;
        }
        catch(NoResultException ex){
            return null;
        }        
    } 

}
