package com.memefest.Jaxrs.Providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomJacksonMapperProvider implements ContextResolver<ObjectMapper> {
  final ObjectMapper mapper;
  
  public CustomJacksonMapperProvider() {
    this.mapper = new ObjectMapper();
    this.mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    this.mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    FilterProvider provider = setFilters(getPublicViews());
    this.mapper.setFilterProvider(provider);
  }
  
  public ObjectMapper getContext(Class<?> type) {
    return this.mapper;
  }

  
    protected SimpleBeanPropertyFilter userPublicViewFilter(){
      SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept(
        "posts", "Posts",
            "contacts","Contacts",
            "firstName","FirstName",
                "lastName","LastName",
                    "email", "Email",
                        "userSecurity","UserSecurity", 
                            "topicsFollowing", "TopicsFollowing", 
                                "categoriesFollowing", "CategoriesFollowing");
        return userFilter;       
    }

    protected FilterProvider setFilters(Map<String,SimpleBeanPropertyFilter> filters){
        SimpleFilterProvider provider = new SimpleFilterProvider();
        for (Entry<String,SimpleBeanPropertyFilter> iterable: filters.entrySet()) {
            provider.addFilter(iterable.getKey(), iterable.getValue());
        }
        return provider;
    }

    protected SimpleBeanPropertyFilter eventPublicView(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "posts", "Posts",
                "clips","Clips",
                    "posters","Posters",
                        "postedBy");
    return filter;
  }

    protected SimpleBeanPropertyFilter topicPublicView(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "Posts", "posts", "FollowedBy", "followedBy"
        );
        return filter;
  }

  protected SimpleBeanPropertyFilter categoryPublicView(){
    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
        "Topics", "topics",
            "followedBy", "FollowedBy"
    );
    return filter;
  }

  protected Map<String,SimpleBeanPropertyFilter> getPublicViews(){
    Map<String, SimpleBeanPropertyFilter> filters= new HashMap<String, SimpleBeanPropertyFilter>();
    filters.put("TopicPublicView",topicPublicView());
    filters.put("EventPublicView",eventPublicView());
    filters.put("CategoryPublicView", categoryPublicView());
    filters.put("UserPublicView", userPublicViewFilter());
    return filters;
  }
}
