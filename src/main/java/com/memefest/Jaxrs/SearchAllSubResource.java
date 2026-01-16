package com.memefest.Jaxrs;


import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.PathSegment;

@RequestScoped
public class SearchAllSubResource extends Resource{

    public String searchItem(PathSegment segment){
        StringBuilder builder = new StringBuilder("[");

        SearchCategorySubResource catSubRes = new SearchCategorySubResource();
        builder.append("Categories:");
        builder.append(catSubRes.searchItem(segment));
        builder.append(",");

        SearchEventSubResource eventSubRes = new SearchEventSubResource();
        builder.append("Events:");
        builder.append(eventSubRes.searchItem(segment));
        builder.append(",");

        SearchPostSubResource postSubRes = new SearchPostSubResource();
        builder.append("Posts:");
        builder.append(postSubRes.searchItem(segment));
        builder.append(",");

        SearchTopicSubResource topicSubRes = new SearchTopicSubResource();
        builder.append("Topics");
        builder.append(topicSubRes.searchItem(segment));
        builder.append(",");

        SearchUserSubResource userSubRes = new SearchUserSubResource();
        builder.append("Users");
        builder.append(userSubRes.searchItem(segment));
        builder.append("]");
        
        return builder.toString();
    }

}
