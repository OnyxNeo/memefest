package com.memefest.Services.Impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.CategoryFollower;
import com.memefest.DataAccess.SubCategory;
import com.memefest.DataAccess.SubCategoryId;
import com.memefest.DataAccess.TopicCategory;
import com.memefest.DataAccess.User;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.SubCategoryJSON;
import com.memefest.DataAccess.JSON.TopicJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.Services.CategoryOperations;
import com.memefest.Services.TopicOperations;
import com.memefest.Services.UserOperations;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import jakarta.transaction.TransactionScoped;

@Stateless(name = "CategoryService")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CategoryService implements CategoryOperations{

  @TransactionScoped
  private EntityManager entityManager;

  @EJB
  private TopicOperations topicOps;

  @EJB
  private UserOperations userOperations;

  //@EJB
  //private DatasourceOps datasourceOps;

  private EntityManagerFactory factory;

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

        String dataSourceName = "DataSource/CategoryService";
        String unitName = "CategoryServicePersistenceUnit";  
        
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
            //memeProps.put(PersistenceUnitProperties.JDBC_USER, username);
            //memeProps.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
            //memeProps.put(PersistenceUnitProperties.CONNECTION_POOL_JTA_DATA_SOURCE, "DataSource/Memefest");
            memeProps.put(PersistenceUnitProperties.JTA_DATASOURCE, dataSourceName);
            //memeProps.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS, unitName);
            //memeProps.put(PersistenceUnitProperties.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
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

  //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  //throw a custom exception to show object was not created
  public void createCategory(CategoryJSON category) {
      Category newCategory = new Category();
      newCategory.setCat_Name(category.getCategoryName());
      this.entityManager.persist(newCategory) ;
  } 

  //throw a custom exception to show object was not created
  public void editCategory(CategoryJSON category){
    if (category == null)
      return; 
    try{
      Category foundCategory = getCategoryEntity(category);
      if (category.getCategoryName() != null)
        foundCategory.setCat_Name(category.getCategoryName()); 
      if (category.getCategoryId() != null)
        foundCategory.setCat_Id(category.getCategoryId());
      this.entityManager.merge(foundCategory);
    } 
    catch(NoResultException ex){
      createCategory(category);
    }
    if(category.getTopics() != null && !category.getTopics().isEmpty())
        for (TopicJSON topic : category.getTopics())
          topicOps.createTopic(topic);
    removeCategoryFollowers(category); 
    createCategoryFollowers(category);
    
  }
    
  //throw a custom exception to show object was not created
  public void createCategoryFollowers(CategoryJSON category) {
    if(category.getFollowedBy() == null || category.getFollowedBy().isEmpty())
      return;
    try{
      Category foundCategory = getCategoryEntity(category);
      Set<User> catUsers = new HashSet<User>();
      try{

        Set<CategoryFollower> catFollowers = getCategoryFollowers(category);
        catUsers = (Set<User>)catFollowers.stream().map(catFollower -> catFollower.getUser()).collect(Collectors.toSet()); 
      }
      catch(NoResultException | EJBException ex){
        return;
      }
      for (UserJSON user : category.getFollowedBy()) {
        User follower = this.userOperations.getUserEntity(user);
        if (follower == null)
          return;
        if (!catUsers.contains(follower)) {
          CategoryFollower newCatFollower = new CategoryFollower();
          newCatFollower.setCat_Id(foundCategory.getCat_Id());
          newCatFollower.setUserId(follower.getUserId());
          this.entityManager.persist(newCatFollower);
        }
      }
    }
    catch(NoResultException | EJBException ex){
      return;
    }
  }
      
  public Set<CategoryFollower> getCategoryFollowers(CategoryJSON category) throws NoResultException, EJBException{
      Category foundCategory = getCategoryEntity(category);
      Stream<CategoryFollower> query = this.entityManager.createNamedQuery("CategoryFollower.findByCategoryId", CategoryFollower.class)
                    .setParameter("categoryId", foundCategory.getCat_Id()).getResultStream();
      return (Set<CategoryFollower>)query.map(object -> (CategoryFollower)object).collect(Collectors.toSet());
  }
      
  public void removeCategoryFollowers(CategoryJSON category) {
    if(category.getCancelFollowedBy() == null || category.getCancelFollowedBy().isEmpty())
      return;
    try{
    Set<CategoryFollower> categoryFollowers = getCategoryFollowers(category);
    
    for (Iterator<UserJSON> iterator = category.getCancelFollowedBy().iterator(); iterator.hasNext(); ) {
        UserJSON user = iterator.next();
        categoryFollowers.stream().filter(candidate -> {
            if (candidate.getUserId().equals(user.getUserId()))
                return true; 
            User userEntity = this.userOperations.getUserEntity(user);
            return (userEntity == candidate.getUser());
          }).forEach(candidate -> this.entityManager.remove(candidate));
      }
    } 
    catch(NoResultException | EJBException ex){
      
    }
  }

      
  public Set<UserJSON> getCategoryFollowersInfo(CategoryJSON category) throws NoResultException, EJBException{
      Set<UserJSON> followers = getCategoryFollowers(category).stream().map(categFollowerEntity ->{
        UserJSON user = new UserJSON(categFollowerEntity.getUserId(), null, categFollowerEntity.getUser().getUsername(), 0, false,
         null, null, null, null, null, null);
        return user;
      }).collect(Collectors.toSet());
      return followers;
  }
      
  public Category getCategoryEntity(CategoryJSON category) throws NoResultException {

    Category foundCategory = null;
    if(category ==  null)
      throw new NoResultException();
    if(category.getCategoryId() != null ) {
      foundCategory = (Category)this.entityManager.find(Category.class, category.getCategoryId());
      if (foundCategory == null){
        throw new NoResultException();
      }          
    }
    else if(category.getCategoryName() != null){
        foundCategory= this.entityManager.createNamedQuery("Category.getCategoryByTitle",Category.class)
                                .setParameter(1,category.getCategoryName())
                                  .getSingleResult();
    }
    else throw new NoResultException();
    return foundCategory; 
  }
  
  public void editSubCategory(SubCategoryJSON subCategory){
    editCategory(subCategory);
    Category categoryEntity = getCategoryEntity(subCategory);
    for(CategoryJSON candidate : subCategory.getParentCategories()){
      Category candidateEntity = null;
      try{
        editCategory(candidate);
        candidateEntity = getCategoryEntity(candidate);
      }
      catch(NoResultException ex){
        continue;
      }
      try{
       getSubCategoryEntity(categoryEntity, candidateEntity);
      }
      catch(NoResultException ex){  
        createParentCategories(categoryEntity, candidateEntity);
      }
    }
  }

  public void removeParentCategories(SubCategoryJSON subCategory){
    Category categoryEntity = getCategoryEntity(subCategory); 
    for(CategoryJSON candidate : subCategory.getParentCategories()){
      try{
          Category candidateEntity = getCategoryEntity(candidate);
          SubCategory subCatEntity = getSubCategoryEntity(categoryEntity, candidateEntity);
          entityManager.remove(subCatEntity);
      } 
      catch(NoResultException ex){
        continue;
      }
    }
  }

  private void createParentCategories(Category category, Category parentCategory){  
      SubCategory subCat= new SubCategory();
      subCat.setCategory(category);
      subCat.setParent(parentCategory);
      entityManager.persist(subCat);
  }

  private SubCategory getSubCategoryEntity(Category category, Category parentCategory) throws NoResultException{
    SubCategoryId subCategoryId = new SubCategoryId();
    subCategoryId.setCat_Id(category.getCat_Id());
    subCategoryId.setParent_Id(parentCategory.getCat_Id());
    return entityManager.find(SubCategory.class, subCategoryId);
  }
      
  public SubCategory getSubCategoryEntity(CategoryJSON category, CategoryJSON parent) throws NoResultException{
    SubCategory foundCategory = null;
    if (((category != null) && category.getCategoryId() != null) || 
            ((parent != null)  && parent.getCategoryId() != null)) {
      SubCategoryId subCatId = new SubCategoryId();
      subCatId.setCat_Id(category.getCategoryId());
      subCatId.setParent_Id(parent.getCategoryId());
      foundCategory = (SubCategory)this.entityManager.find(SubCategory.class, subCatId);
      if(foundCategory != null)
        return foundCategory;
      else throw new NoResultException();
    }
    else throw new NoResultException();
  }

  public void removeCategory(CategoryJSON category) {
    if (category == null)
      return;
    try{ 
      Category foundCategory = getCategoryEntity(category);
      if (foundCategory.getTopics() != null)
        return; 
      this.entityManager.remove(foundCategory);
    }
    catch(NoResultException ex){
      return;
    } 
  }

  public SubCategoryJSON getSubCategoryInfo(SubCategoryJSON category) throws NoResultException{
    CategoryJSON categoryInfo = getCategoryInfo(category);
    Set<CategoryJSON> parentCategories = new HashSet<CategoryJSON>();
    try{
      Stream<SubCategory> subCats =  entityManager.createNamedQuery("SubCategory.getParentCategories",SubCategory.class)
          .setParameter("parentId", categoryInfo.getCategoryId()).getResultStream();
      parentCategories = subCats.map(candidate ->{
      return getCategoryInfo(new CategoryJSON(candidate.getParent().getCat_Id(), null, null, null, null));
              }).collect(Collectors.toSet());
    }
    catch(NoResultException ex){

    }
    SubCategoryJSON subCategoryJSON = new SubCategoryJSON(categoryInfo.getCategoryId(), 
                                        categoryInfo.getCategoryName(), categoryInfo.getTopics(), parentCategories, null);
    return subCategoryJSON;
  }
    
  public Set<TopicJSON> getCategoryTopics(CategoryJSON category)throws NoResultException, EJBException{
    Category catEntity = getCategoryEntity(category);
    Set<TopicJSON> topics = new HashSet<TopicJSON>();
    Stream<TopicCategory> topicCats = entityManager.createNamedQuery("TopicCategory.getByCategoryId", TopicCategory.class)
              .setParameter("categoryId", catEntity.getCat_Id()).getResultStream();
    topics = topicCats.map(candidate ->{
      return new TopicJSON(candidate.getTopic_Id() , null, null, null, null, null);
    }).collect(Collectors.toSet());
    return topics;
  }
      
  public CategoryJSON getCategoryInfo(CategoryJSON category) throws NoResultException, EJBException{
      Category categoryEntity = getCategoryEntity(category);
      Set<TopicJSON> categoryTopics = null;
      Set<UserJSON> followers = null;
      followers = getCategoryFollowersInfo(category);
      categoryTopics = getCategoryTopics(category);
      CategoryJSON categoryJson = new CategoryJSON(categoryEntity.getCat_Id(), categoryEntity.getCat_Name(), categoryTopics, followers, null);
      return categoryJson;
  }

  public Set<CategoryJSON> searchCategory(CategoryJSON category){
    List<Category> categories= null;
    if(category == null)
      categories = this.entityManager.createNamedQuery("Category.getAll", Category.class).getResultList();
    else if(category.getCategoryName() != null)
      categories = this.entityManager.createNamedQuery("Category.searchByTitle", Category.class).setParameter(1, category.getCategoryName())
                .getResultList();
      if(categories == null)
        throw new NoResultException();
      return categories.stream().map(catEntity -> {
        Set<TopicJSON> catTopics = new HashSet<TopicJSON>();
        Set<UserJSON> followers = new HashSet<UserJSON>();
        try{
          catTopics = getCategoryTopics(category);
        }
        catch(NoResultException | EJBException ex){
    
        }
        try{
          followers = getCategoryFollowersInfo(category);
        }
        catch(NoResultException | EJBException ex){

        }
        return new CategoryJSON(catEntity.getCat_Id(), catEntity.getCat_Name(), catTopics,followers, null);
      }).toList().stream().collect(Collectors.toSet());

  }

}
