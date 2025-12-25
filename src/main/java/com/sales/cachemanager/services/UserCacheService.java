package com.sales.cachemanager.services;


import com.sales.cachemanager.RedisConstants;
import com.sales.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCacheService {

    @Autowired
    CacheManager cacheManager;

    public User getCacheUser(String slug){
        Cache cache = cacheManager.getCache(RedisConstants.USER_REDIS_CACHE_NAME);
        log.info("Getting user from redis : {}",slug);
        return cache == null ?  null : cache.get(slug,User.class);
    }


    @Cacheable(value = RedisConstants.USER_REDIS_CACHE_NAME, key="#user.slug")
    public User getOrWriteCacheUser(User user){
        return user;
    }

    @CachePut(value = RedisConstants.USER_REDIS_CACHE_NAME, key = "#user.slug")
    public User saveCacheUser(User user){
        log.info("Saving a user in cache : {}",user.getSlug());
        return user;
    }

    @CacheEvict(value = RedisConstants.USER_REDIS_CACHE_NAME, key = "#slug")
    public void deleteCacheUser(String slug){
        log.info("User delete from redis : {}",slug);
    }


}
