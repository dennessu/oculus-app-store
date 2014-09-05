package com.junbo.store.rest.resource

import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.resource.StoreConfigResource
import groovy.transform.CompileStatic
import net.sf.ehcache.CacheManager
import org.springframework.cache.ehcache.EhCacheCacheManager
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The StoreConfigResourceImpl class.
 */
@CompileStatic
@Component('defaultStoreConfigResource')
class StoreConfigResourceImpl implements StoreConfigResource {

    private CacheManager cacheManager

    @Resource(name = 'storeCacheManager')
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<Void> clearCache() {
        cacheManager.clearAll()
        return Promise.pure(null)
    }
}
