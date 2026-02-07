package com.memefest.CacheHelper;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import org.ehcache.CacheManager;
import org.ehcache.config.Builder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

@ApplicationScoped
public class CacheHelper {
  CacheManager cacheManager;
  
  public CacheHelper() {
    this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("usernameCache",
     (Builder)CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
      (Builder)ResourcePoolsBuilder.heap(100L)).withExpiry(ExpiryPolicyBuilder
        .timeToIdleExpiration(Duration.ofMinutes(10L))))
        .withCache("userIdCache", (Builder)CacheConfigurationBuilder
          .newCacheConfigurationBuilder(Long.class, String.class, (Builder)ResourcePoolsBuilder.heap(100L))
            .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.
              ofMinutes(10L)))).withCache("guestCache", 
              (Builder)CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
                (Builder)ResourcePoolsBuilder.heap(100L)).withExpiry(ExpiryPolicyBuilder
                  .timeToIdleExpiration(Duration.ofMinutes(10L))))
                  .withCache("notificationsCache", (Builder)CacheConfigurationBuilder
                  .newCacheConfigurationBuilder(Long.class, String.class, (Builder)ResourcePoolsBuilder.heap(100L))
                  .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.
                  ofMinutes(10L))))
                    .withCache("userPasswordResetTokenCache",
                      (Builder)CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                      (Builder)ResourcePoolsBuilder.heap(100L)).withExpiry(ExpiryPolicyBuilder
                      .timeToIdleExpiration(Duration.ofMinutes(10L)))).build();
                        this.cacheManager.init();
  }
  
  @PreDestroy
  public void shutdown() {
    this.cacheManager.close();
  }
  
  public CacheManager getCacheManager() {
    return this.cacheManager;
  }
}
