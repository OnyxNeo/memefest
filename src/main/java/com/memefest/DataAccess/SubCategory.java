package com.memefest.DataAccess;


import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
/* 
@NamedNativeQueries({
  @NamedNativeQuery(
    name = "SubCategory.getCategoryByTitle",
    query = "SELECT TOP(1) S.Cat_Id as categoryId, C.Cat_Name as categoryName,S.Parent_Id as parentId FROM SUBCATEGORY S " 
    + "RIGHT OUTER JOIN CATEGORY C ON S.Cat_Id = C.Cat_Id WHERE C.Cat_Name LIKE CONCAT('%'CONCAT(?, '%'))",
    resultSetMapping = "SubCategoryEntityMapping")
})
@SqlResultSetMappings({
  @SqlResultSetMapping(
    name = "SubCategoryEntityMapping",
    entities = {
      @EntityResult(
        entityClass = Category.class, 
        fields = {
          @FieldResult(name = "categoryId", column = "categoryId"),
          @FieldResult(name = "categoryName", column = "categoryName"), 
          @FieldResult(name = "parentId", column = "parentId")}
      )
    }
  )
})
*/
@Entity(name = "SubCategoryEntity")
@Table(name = "SUBCATEGORY")
public class SubCategory{

  @EmbeddedId
  private SubCategoryId subCategoryId = new SubCategoryId();

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Cat_Id", referencedColumnName = "Cat_Id")
  private Category category;
  
  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "Parent_Id", referencedColumnName = "Cat_Id")
  private Category parent; 

/* 
  @ManyToOne(cascade =  {CascadeType.PERSIST})
  @JoinColumn(name = "Parent_Id", referencedColumnName = "Cat_Id")
  private MainCategory mainCategory;
*/
  public void setCategory(Category category) {
    this.subCategoryId.setCat_Id(category.getCat_Id());
    this.category = category;
  }
  
  public Category getCategory() {
    return this.category;
  }

  public void setParent(Category parent){
    this.subCategoryId.setParent_Id(parent.getCat_Id());
    this.parent = parent;
  }

  public Category getParent(){
    return this.parent;
  }
  
  public Long getCat_Id(){
    return subCategoryId.getCat_Id();
  }

  public void setCat_Id(Long catId){
    this.subCategoryId.setCat_Id(catId);
  }

  public Long getTopic_Id(){
    return subCategoryId.getCat_Id();
  }

  public void setTopic_Id(Long categoryId){
    this.subCategoryId.setCat_Id(categoryId);
  }
/*
  public void setMainCategory(MainCategory mainCategory){
    this.mainCategory = mainCategory;
  }

  public MainCategory getMainCategory(){
    return this.mainCategory;
  }
  */
}
