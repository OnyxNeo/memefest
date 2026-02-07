package com.memefest.Services;

import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import java.util.Set;
import com.memefest.DataAccess.User;

import jakarta.ejb.EJBException;
import jakarta.ejb.Local;

@Local
public interface UserOperations {
  
  UserJSON getUserInfo(UserJSON paramUserJSON);

  Set<UserJSON> getAllUsers();

  public Set<UserJSON> getFollowers(UserJSON user);

  public Set<UserJSON> getFollowing(UserJSON user);

  public void addFollower(UserJSON user, UserJSON follower);

  public void removeFollower(UserJSON user, UserJSON follower);
  
  public UserJSON editUser(UserJSON user);

  public User getUserEntity(UserJSON user);

  public Set<UserJSON> searchByUsername(UserJSON user);
  
  public Set<User> getUserEntities();

  public Set<PostWithReplyJSON> getComments(UserJSON user) throws EJBException;

  public Set<PostJSON> getUserPosts(UserJSON user);

  public boolean isFollowedByUser(UserJSON user, UserJSON follower);

  public void toggleFollowedBy(UserJSON user, UserJSON follower);
}
