package com.memefest.Services;

import com.memefest.DataAccess.JSON.UserJSON;

public interface AdminOperations {
    
    public boolean isAdmin(UserJSON user);
}
