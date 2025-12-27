package com.sales.cachemanager.services;


import com.sales.cachemanager.RedisConstants;
import com.sales.entities.User;
import com.sales.helpers.Logger;
import com.sales.helpers.SafeLogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheService {

    private final CacheManager cacheManager;
    private final Logger safeLog = SafeLogHelper.getInstance();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(UserCacheService.class);

    public User getCacheUser(String slug){
        Cache cache = cacheManager.getCache(RedisConstants.USER_REDIS_CACHE_NAME);
        safeLog.info(logger,"Getting user from redis : {}",slug);
        return cache == null ?  null : cache.get(slug,User.class);
    }


    @Cacheable(value = RedisConstants.USER_REDIS_CACHE_NAME, key="#user.slug")
    public User getOrWriteCacheUser(User user){
        return user;
    }

    @CachePut(value = RedisConstants.USER_REDIS_CACHE_NAME, key = "#user.slug")
    public User saveCacheUser(User user){
        safeLog.info(logger,"Saving a user in cache : {}",user.getSlug());
        return user;
    }

    @CacheEvict(value = RedisConstants.USER_REDIS_CACHE_NAME, key = "#slug")
    public void deleteCacheUser(String slug){
        safeLog.info(logger,"User delete from redis : {}",slug);
    }


}
