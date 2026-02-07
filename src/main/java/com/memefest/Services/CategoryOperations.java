package com.memefest.Services;

import java.util.Set;

import com.memefest.DataAccess.Category;
import com.memefest.DataAccess.CategoryFollower;
import com.memefest.DataAccess.SubCategory;
import com.memefest.DataAccess.JSON.CategoryJSON;
import com.memefest.DataAccess.JSON.SubCategoryJSON;
import com.memefest.DataAccess.JSON.TopicJSON;

public interface CategoryOperations {
 
    public CategoryJSON getCategoryInfo(CategoryJSON category);
    
    public Set<TopicJSON> getCategoryTopics(CategoryJSON category);

    public CategoryJSON editCategory(CategoryJSON category);

    public void createCategoryFollowers(CategoryJSON category);
    
    public void removeCategoryFollowers(CategoryJSON categoory);

    public void removeParentCategories(SubCategoryJSON subCategory);

    public void editSubCategory(SubCategoryJSON subCategory);

    //public void editMainCategory(CategoryJSON mainCategory);

    public Category getCategoryEntity(CategoryJSON category);

    //ublic MainCategory getMainCategoryEntity(CategoryJSON category);

    public SubCategory getSubCategoryEntity (CategoryJSON category, CategoryJSON parent);

    //public CategoryJSON getMainCategoryInfo(CategoryJSON category);

    public SubCategoryJSON getSubCategoryInfo(SubCategoryJSON category);

    //public MainCategory getMainCategoryFromCategory(CategoryJSON category);

    public Set<CategoryJSON> searchCategory(CategoryJSON category);

    public void removeCategory(CategoryJSON category);

    public Set<CategoryFollower> getCategoryFollowers(CategoryJSON category);



}
