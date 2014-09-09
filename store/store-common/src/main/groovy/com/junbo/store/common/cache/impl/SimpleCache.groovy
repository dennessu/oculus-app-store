package com.junbo.store.common.cache.impl

import com.junbo.store.common.cache.Cache
import groovy.transform.CompileStatic
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.cache.ehcache.EhCacheCacheManager

/**
 * The SimpleCache class.
 */
@CompileStatic
class SimpleCache<K, V> implements Cache<K, V>, InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleCache)

    private String cacheName

    private CacheManager cacheManager

    private boolean cacheEnabled = true

    private net.sf.ehcache.Cache cache

    void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled
    }

    @Required
    void setCacheName(String cacheName) {
        this.cacheName = cacheName
    }

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    void put(K key, V value) {
        if (!cacheEnabled) {
            return
        }
        cache.put(new Element(key, value))
    }

    @Override
    V get(K key) {
        if (!cacheEnabled) {
            return null
        }
        return cache.get(key)?.getObjectValue() as V
    }

    @Override
    String getCacheName() {
        return cacheName
    }

    @Override
    void evictAll() {
        cache.removeAll();
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert cacheManager != null, 'cacheManager not set'
        if (cacheManager.status != Status.STATUS_ALIVE) {
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            net.sf.ehcache.Cache cache = cacheManager.getCache(cacheName)
            if (cache == null) {
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + cacheName)
            }
            this.cache = cache
        }
        LOGGER.info('name=Store_Item_Cache_Initialized, enabled={} ', cacheEnabled)
    }
}
