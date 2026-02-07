package com.memefest.Jaxrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public abstract class Resource {
    
    protected ObjectMapper mapper = new ObjectMapper();

    protected Resource(){   
        FilterProvider provider = setFilters(getPublicViewFilters());
        //mapper.setSerializationInclusion(Include.NON_DEFAULT);
        this.mapper.setFilterProvider(provider);
    }

    private static SimpleBeanPropertyFilter userPublicViewFilter(){
      SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept(
        "posts", "Posts",
            "contacts","Contacts",
            "firstName","FirstName",
                "lastName","LastName",
                    "email", "Email",
                        "userSecurity","UserSecurity", 
                            "topicsFollowing", "TopicsFollowing", 
                                "categoriesFollowing", "CategoriesFollowing",
                                "Avatar",
                                "username",
                                "Cancel","cancel",
                                "reposts","Reposts");
        return userFilter;       
    }

    public static FilterProvider setFilters(Map<String,SimpleBeanPropertyFilter> filters){
        SimpleFilterProvider provider = new SimpleFilterProvider();
        for (Entry<String,SimpleBeanPropertyFilter> iterable: filters.entrySet()) {
            provider.addFilter(iterable.getKey(), iterable.getValue());
        }
        return provider;
    }


    private static SimpleBeanPropertyFilter eventPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "posts", "Posts",
                "clips","Clips",
                    "posters","Posters",
                        "postedBy",
                        "categories","Categories",
                        "canceledCategories, CanceledCategories",
                        "CanceledImages", "canceledImages",
                        "isCanceled", "IsCanceled",
                        "canceledClips", "CanceledClips");
        return filter;
    }

    private static SimpleBeanPropertyFilter topicPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "Posts", "posts", "FollowedBy", "followedBy"
        );
        return filter;
    }

    private static SimpleBeanPropertyFilter topicExtendedViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "canceledCategories","CanceledCategories",
            "cancelFollowedBy", "CancelFollowedBy"
        );
        return filter;
    }

    private static SimpleBeanPropertyFilter categoryPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
           "Topics", "topics",
            "followedBy", "FollowedBy",
            "cancelFollowedBy","cancelFollowedBy"
         );
        return filter;
    }

    private static SimpleBeanPropertyFilter postPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "downvotes", "Downvotes",
            "Categories", "categories",
            "canceledCategories", "CanceledCategories"
        );
        return filter;
    }

    private static SimpleBeanPropertyFilter commentPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "downvotes", "Downvotes",
            "Categories", "categories",
            "canceledCategories", "CanceledCategories",
            "postId","PostId",
            "id", "Id",
            "body", "Body",
            "isLiked",
            "isDownvoted",
            "Images",
            "Videos","videos",
            "taggedUsers",
            "images"
        );
        return filter;
    }

    private static SimpleBeanPropertyFilter postWithReplyView(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "downvotes", "Downvotes",
            "Categories", "categories",
            "canceledCategories", "CanceledCategories"
        );
        return filter;
    }


    private static SimpleBeanPropertyFilter postExtendedViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "downvotes", "Downvotes",
            "canceledCategories", "CanceledCategories",
            "categories"
        );
        return filter;
    }

    private static SimpleBeanPropertyFilter commentExtendedViewFilter(){
    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "downvotes", "Downvotes",
            "canceledCategories", "CanceledCategories",
            "categories",
            "postId", "PostId"
    );
    return filter;
    }

    private static SimpleBeanPropertyFilter categoryExtendedViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancelFollowedBy","cancelFollowedBy"
        );
        return filter; 
    }

    protected void categoryExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("CategoryView", categoryExtendedViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    protected void postWithReplyExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("PostWithReplyView", postWithReplyView());
        publicFilters.put("PostView", postOwnersViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    protected void eventExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("EventView", eventExtendedViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    private static SimpleBeanPropertyFilter eventExtendedViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
                                    "postedBy",
                        "categories","Categories",
                        "canceledCategories, CanceledCategories",
                        "CanceledImages", "canceledImages",
                        "isCanceled", "IsCanceled",
                        "canceledClips", "CanceledClips");
        return filter;
    }

    protected void postOwnersView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("PostView", postOwnersViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    protected void postExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("PostView", postExtendedViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }


    private static SimpleBeanPropertyFilter userOwnersViewFilter(){
      SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept(
        "userSecurity","UserSecurity", 
        "topicsFollowing", "TopicsFollowing", 
        "categoriesFollowing", "CategoriesFollowing",
        "Avatar",
        "username",
        "Cancel","cancel",
        "isFollowed");
        return userFilter;
    }  

    protected void userOwnersView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("UserView", userOwnersViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    protected void topicExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("TopicView", topicExtendedViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    private static SimpleBeanPropertyFilter postOwnersViewFilter(){
         SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "cancel","Cancel",
            "Categories", "categories",
            "canceledCategories","CanceledCategories",
            "isDownvoted", 
            "isLiked"
         );
         return filter;
    }

    protected static SimpleBeanPropertyFilter sponsorPublicViewFilter(){
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(
            "image", "email"
        );
        return filter;
    } 

    protected void sponsorExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("SponsorView", sponsorPublicViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }

    protected void commentExtendedView(){
        Map<String, SimpleBeanPropertyFilter> publicFilters = getPublicViewFilters();
        publicFilters.put("CommentView", commentExtendedViewFilter());
        FilterProvider provider = setFilters(publicFilters);
        mapper.setFilterProvider(provider);
    }    

    protected static Map<String,SimpleBeanPropertyFilter> getPublicViewFilters(){
        Map<String, SimpleBeanPropertyFilter> filters= new HashMap<String, SimpleBeanPropertyFilter>();
        filters.put("TopicView",topicPublicViewFilter());
        filters.put("EventView",eventPublicViewFilter());
        filters.put("CategoryView", categoryPublicViewFilter());
        filters.put("UserView", userPublicViewFilter());
        filters.put("CommentView", commentPublicViewFilter());
        filters.put("PostView", postPublicViewFilter());
        filters.put("SponsorView", sponsorPublicViewFilter());
        return filters;
    }



}
