package com.memefest.Security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.memefest.CacheHelper.CacheHelper;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.UserSecurityJSON;
import com.memefest.Services.UserSecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

@ApplicationScoped
public class JwtAuthIdentityStore implements IdentityStore {
  @Inject
  private CacheHelper cacheHelper;
  
  @Inject
  private UserSecurityService userSecurityService;
  
  private static ObjectMapper mapper = new ObjectMapper();
  
  static {
    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
    SimpleFilterProvider provider = new SimpleFilterProvider();
    provider.addFilter("UserView", filter);
    mapper.setFilterProvider(provider);
    //mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
  }
  
  public CredentialValidationResult validate(Credential credential) {
    CredentialValidationResult result = CredentialValidationResult.NOT_VALIDATED_RESULT;
    if (credential instanceof UsernamePasswordCredential) {
      UsernamePasswordCredential upc = (UsernamePasswordCredential)credential;
      try {
        UserSecurityJSON userSecurityJSON = this.userSecurityService.getSecurityDetails(new UserJSON(upc.getCaller()));
        if (userSecurityJSON == null)
          return result; 
        UserSecurityJSON userPass = this.userSecurityService.getUserPassword(userSecurityJSON);
        if (upc.getPassword().compareTo(userPass.getPassword())) {
          result = new CredentialValidationResult(upc.getCaller());
          cacheUserSecurityDetails(userSecurityJSON);
        } else {
          return result;
        } 
      } catch (NoResultException ex) {
        return result;
      } 
    } else if (credential instanceof JwtCredential) {
      JwtCredential jwtCredential = (JwtCredential)credential;
      UserJSON userAtrributes = verifyToken(jwtCredential.getAccessToken());
      if (userAtrributes != null)
        result = new CredentialValidationResult(userAtrributes.getUsername()); 
    } 
    return result;
  }
  
  public Set<String> getCallerGroups(CredentialValidationResult result) {
    Set<String> groupSet = new HashSet<>();
    if (result.getStatus() == CredentialValidationResult.Status.VALID) {
      groupSet.add("Users");
      UserJSON userJSON = new UserJSON(result.getCallerPrincipal().getName());
      if (this.userSecurityService.isAdmin(userJSON))
        groupSet.add("Admins"); 
    } 
    return groupSet;
  }
  
  public Set<IdentityStore.ValidationType> validationTypes() {
    Set<IdentityStore.ValidationType> validationTypes = new HashSet<>();
    validationTypes.add(IdentityStore.ValidationType.PROVIDE_GROUPS);
    validationTypes.add(IdentityStore.ValidationType.VALIDATE);
    return validationTypes;
  }
  
  public UserSecurityJSON getUserSecurityDetails(UserJSON userJSON) throws NoResultException {
    UserSecurityJSON userSecurity = this.userSecurityService.getSecurityDetails(userJSON);
    return userSecurity;
  }
  
  public UserSecurityJSON getUserPassword(UserJSON userJson) {
    UserSecurityJSON userSecurity = userJson.getUserSecurity();
    UserSecurityJSON result = this.userSecurityService.getUserPassword(userSecurity);
    return result;
  }
  
  public void setUserPassword(UserJSON userJson) {
    UserSecurityJSON userSecurity = userJson.getUserSecurity();
    this.userSecurityService.setUserPassword(userSecurity);
  }
  
  public static UserSecurityJSON createUserAccessToken(UserJSON userJSON) {
    LocalDateTime allocatedDate = LocalDateTime.now();
    LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(2);
    String token = null;
    try {
      KeyPair keyPair = CustomKeyLocator.loadFromJKS();
      String keyId = CustomKeyLocator.getThumbPrint(keyPair);
      token = ((JwtBuilder)((JwtBuilder.BuilderHeader)((JwtBuilder.BuilderHeader)
                  ((JwtBuilder.BuilderHeader)Jwts.builder().header().add("exp", expiryDate.toString()))
                  .add("iat", allocatedDate.toString())).keyId(keyId)).and())
                  .issuer("JiniceServer").expiration(Date.from(expiryDate.atZone(ZoneId.systemDefault())
                  .toInstant())).issuedAt(Date.from(allocatedDate.atZone(ZoneId.systemDefault()).toInstant()))
                  .claim("user", userJSON).encodePayload(true).json((Serializer)new JacksonSerializer<>(mapper))
                  .signWith(keyPair.getPrivate()).compact();
    } catch (KeyStoreException ex) {
      ex.printStackTrace();
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (UnrecoverableEntryException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (CertificateException ex) {
      ex.printStackTrace();
    } 
    UserSecurityJSON userSecurity = null;
    if (userJSON.getUserSecurity() != null) {
      userSecurity = userJSON.getUserSecurity();
      userSecurity.setAccessTkn(token);
    } else {
      userSecurity = new UserSecurityJSON(token, null, null, userJSON.getUserId(), userJSON.getUsername());
    } 
    return userSecurity;
  }
  
  public static UserSecurityJSON createUserRefreshTokens(UserJSON userJSON) {
    LocalDateTime allocatedDate = LocalDateTime.now();
    LocalDateTime expiryDate = LocalDateTime.now().plusDays(2L);
    KeyPair keyPair = null;
    String keyId = null;
    try {
      keyPair = CustomKeyLocator.loadFromJKS();
      keyId = CustomKeyLocator.getThumbPrint(keyPair);
    } catch (KeyStoreException ex) {
      ex.printStackTrace();
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (UnrecoverableEntryException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (CertificateException ex) {
      ex.printStackTrace();
    } 
    String token = ((JwtBuilder)((JwtBuilder.BuilderHeader)((JwtBuilder.BuilderHeader)((JwtBuilder.BuilderHeader)Jwts.builder().header().add("exp", expiryDate.toString())).add("iat", allocatedDate.toString())).keyId(keyId)).and()).issuer("JiniceServer").expiration(Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant())).issuedAt(Date.from(allocatedDate.atZone(ZoneId.systemDefault()).toInstant())).claim("user", userJSON).json((Serializer)new JacksonSerializer(mapper)).encodePayload(true).signWith(keyPair.getPrivate()).compact();
    UserSecurityJSON userSecurity = null;
    if (userJSON.getUserSecurity() != null) {
      userSecurity = userJSON.getUserSecurity();
      userSecurity.setRefreshTkn(token);
    } else {
      userSecurity = new UserSecurityJSON(null, null, token, userJSON.getUserId(), userJSON.getUsername());
    } 
    return userSecurity;
  }
  
  public void setUserSecurityDetails(UserSecurityJSON uSecurityJSON) {
    this.userSecurityService.setSecurityDetails(uSecurityJSON);
    cacheUserSecurityDetails(uSecurityJSON);
  }
  
  public void cacheUserSecurityDetails(UserSecurityJSON userSecurityJSON) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    UserJSON userJSON = userSecurityJSON.getUser();
    try {
      if (userSecurityJSON.getUser().getUsername() != null) {
        Cache<String, String> userSecurityCache = cacheManager.getCache("usernameCache", String.class, String.class);
        userSecurityCache.put(userSecurityJSON.getUser().getUsername(), mapper.writeValueAsString(userJSON));
      } else if (userJSON.getUserId() != null) {
        Cache<Long, String> userSecurityCache = cacheManager.getCache("userIdCache", Long.class, String.class);
        userSecurityCache.put(userSecurityJSON.getUser().getUserId(), mapper.writeValueAsString(userJSON));
      } 
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    } 
  }
  
  public UserSecurityJSON getCachedUserSecurityDetails(UserJSON user) {
    CacheManager cacheManager = this.cacheHelper.getCacheManager();
    try {
      if (user.getUserId() != null) {
        Cache<Long, String> userSecurityCache = cacheManager.getCache("userIdCache", Long.class, String.class);
        String cacheValue = (String)userSecurityCache.get(user.getUserId());
        if (cacheValue != null)
          return (UserSecurityJSON)mapper.readValue(cacheValue, UserSecurityJSON.class); 
      } else if (user.getUsername() != null) {
        Cache<String, String> userSecurityCache = cacheManager.getCache("usernameCache", String.class, String.class);
        String cacheValue = (String)userSecurityCache.get(user.getUsername());
        if (cacheValue != null)
          return (UserSecurityJSON)mapper.readValue(cacheValue, UserSecurityJSON.class); 
      } 
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    } 
    return null;
  }
  
  public static UserJSON getPayloadUserAttibutes(String token) {
    CustomKeyLocator keyLocator = new CustomKeyLocator();
    UserJSON user = null;
    try {
      user = (UserJSON)((Claims)Jwts.parser().json((Deserializer)new JacksonDeserializer(mapper))
      .keyLocator((Locator)keyLocator).build().parseSignedClaims(token).getPayload())
      .get("user", UserJSON.class);
    } catch (JwtException ex) {
      ex.printStackTrace();
    } 
    return user;
  }
  
  public static UserJSON verifyToken(String token) {
    CustomKeyLocator keyLocator = new CustomKeyLocator();
    UserJSON user = null;
    LinkedHashMap<String, Class<?>> objects = new LinkedHashMap<>();
    objects.put("user", UserJSON.class);
    try {
      LinkedHashMap<?, ?> userDetails = (LinkedHashMap<?, ?>)((Claims)Jwts.parser().keyLocator((Locator)keyLocator).
                            json((Deserializer)new JacksonDeserializer(mapper)).build().parseSignedClaims(token).getPayload()).get("user", LinkedHashMap.class);
      Long userId = null;
      String username = null;
      String email = null;
      String firstName = null;
      String lastName = null;
      boolean verified = false;
      int contacts = 0;
      if (userDetails.containsKey("userId") && userDetails.get("userId") != null)
        userId = Long.valueOf(((Integer)userDetails.get("userId"))); 
      if (userDetails.containsKey("userName") && userDetails.get("userName") != null)
        username = (String)userDetails.get("userName"); 
      if (userDetails.containsKey("email") && userDetails.get("email") != null)
        email = (String)userDetails.get("email"); 
      if (userDetails.containsKey("firstName") && userDetails.get("firstName") != null)
        firstName = (String)userDetails.get("firstName"); 
      if (userDetails.containsKey("lastName") && userDetails.get("lastName") != null)
        lastName = (String)userDetails.get("lastName"); 
      if (userDetails.containsKey("verified") && userDetails.get("verified") != null)
        verified = ((Boolean)userDetails.get("verified")).booleanValue(); 
      if (userDetails.containsKey("contacts") && userDetails.get("contacts") != null)
        contacts = ((Integer)userDetails.get("contacts")).intValue(); 
      user = new UserJSON(userId, email, username, contacts, verified, firstName, lastName,
       null, null, null, null);
    } catch (JwtException ex) {
      ex.printStackTrace();
    } 
    return user;
  }
}