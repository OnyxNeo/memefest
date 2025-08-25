package com.memefest.Services.Impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;


@Stateless(name = "CategoryService")
public class CategoryService implements CategoryOperations{

  @PersistenceContext(unitName = "memeFest", type = PersistenceContextType.TRANSACTION)
  private EntityManager entityManager;

  @EJB
  private TopicOperations topicOps;

  @EJB
  private UserOperations userOperations;
  
  //throw a custom exception to show object was not created
  public void createCategory(CategoryJSON category) {
    try{
      getCategoryEntity(category); 
    }
    catch(NoResultException ex){
      Category newCategory = new Category();
      newCategory.setCat_Name(category.getCategoryName());
      this.entityManager.persist(newCategory);
    }
  } 

  //throw a custom exception to show object was not created
  public void editCategory(CategoryJSON category){
    if (category == null)
      return; 
    try{
      Category foundCategory = getCategoryEntity(category);
      if (category.getCategoryName() != null)
        foundCategory.setCat_Name(category.getCategoryName()); 
      if (category.getCategoryId() != 0)
        foundCategory.setCat_Id(category.getCategoryId());
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
                    .setParameter("categoryId", Integer.valueOf(foundCategory.getCat_Id())).getResultStream();
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
            if (candidate.getUserId() == user.getUserId())
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
        UserJSON user = new UserJSON(categFollowerEntity.getUserId(), categFollowerEntity.getUser().getUsername());
        return user;
      }).collect(Collectors.toSet());
      return followers;
  }
      
  public Category getCategoryEntity(CategoryJSON category) throws NoResultException {
    Category foundCategory = null;
    if(category ==  null)
      throw new NoResultException();
    if((category != null) && category.getCategoryId() != 0 ) {
      foundCategory = (Category)this.entityManager.find(Category.class, Integer.valueOf(category.getCategoryId()));
      if (foundCategory == null){
        throw new NoResultException();
      }          
    }
    else if(category.getCategoryName() != null){   
        foundCategory = this.entityManager.createNamedQuery("Category.getCategoryByTitle", Category.class)
                                .setParameter(1,category.getCategoryName()).getSingleResult();
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
    if (((category != null) && category.getCategoryId() != 0) || 
            ((parent != null)  && parent.getCategoryId() != 0)) {
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
    SubCategoryJSON subCategoryJSON = new SubCategoryJSON(0, null, null, null, parentCategories);
    subCategoryJSON.setFollowedBy(categoryInfo.getFollowedBy());
    subCategoryJSON.setCategoryName(categoryInfo.getCategoryName());
    subCategoryJSON.setCategoryId(categoryInfo.getCategoryId()); 
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
