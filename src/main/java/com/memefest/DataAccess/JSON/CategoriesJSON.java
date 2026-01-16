package com.memefest.DataAccess.JSON;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriesJSON{

    @JsonProperty("categories")
    private Set<CategoryJSON> categories;
    
    @JsonCreator
    public CategoriesJSON(@JsonProperty("categories") Set<CategoryJSON> categories) {
        this.categories = categories;
    }

    @JsonProperty("categories")
    public Set<CategoryJSON> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<CategoryJSON> categories) {
        this.categories = categories;
    }
}

