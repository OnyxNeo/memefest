package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLoginJSON {
    
    @JsonProperty("user")
    private UserJSON user;

    @JsonProperty("accessTkn")
    private String accessTkn;

    @JsonProperty("refreshTkn")
    private String refreshTkn;

    public UserLoginJSON(@JsonProperty("user") UserJSON user, @JsonProperty("accessTkn") String accessTkn, 
                            @JsonProperty("refreshTkn") String refreshTkn){
        this.accessTkn = accessTkn;
        this.user = user;
        this.refreshTkn = refreshTkn;
                            }
}
