package com.memefest.Services;

import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.UserSecurityJSON;

import jakarta.ejb.Local;
@Local
public interface UserSecurityService {
  void createUser(UserJSON paramUserJSON);
  
  void setSecurityDetails(UserSecurityJSON paramUserSecurityJSON);
  
  UserSecurityJSON getSecurityDetails(UserJSON paramUserJSON);
  
  UserSecurityJSON getUserPassword(UserSecurityJSON paramUserSecurityJSON);
  
  void setUserPassword(UserSecurityJSON paramUserSecurityJSON);
  
  boolean emailExists(UserJSON paramUserJSON);
  
  UserJSON getUserDetails(UserJSON paramUserJSON);
  
  boolean isAdmin(UserJSON paramUserJSON);

  
}
