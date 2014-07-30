package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.cache.ehcache.EhCacheCacheManager

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeRepositoryCloudantImpl extends CloudantClient<PIType> implements PITypeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PITypeRepositoryCloudantImpl)
    private static final String IDENTITY_PI_TYPE_CACHE_NAME = 'IDENTITY_PI_TYPE'

    private CacheManager cacheManager

    @Override
    Promise<PIType> create(PIType model) {
        return cloudantPost(model).then { PIType piType ->
            Element element = new Element(piType.getId().value, piType)
            getPITypeCache().put(element)
            return Promise.pure(piType)
        }
    }

    @Override
    Promise<PIType> update(PIType model, PIType oldModel) {
        return cloudantPut(model, oldModel).then { PIType piType ->
            Element element = new Element(piType.getId().value, piType)
            getPITypeCache().put(element)
            return Promise.pure(piType)
        }
    }

    @Override
    Promise<PIType> get(PITypeId id) {
        Element element = getPITypeCache().get(id.value)

        if (element != null) {
            return Promise.pure((PIType)element.objectValue)
        }
        return cloudantGet(id.toString()).then { PIType piType ->
            element = new Element(piType.getId().value, piType)
            getPITypeCache().put(element)
            return Promise.pure(piType)
        }
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        return cloudantDelete(id.toString()).then {
            getPITypeCache().remove(id.value)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset) {
        return queryView('by_typeCode', typeCode, limit, offset, false)
    }

    @Override
    Promise<List<PIType>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    public Cache getPITypeCache() {
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid.')
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            Cache cache = cacheManager.getCache(IDENTITY_PI_TYPE_CACHE_NAME)
            if (cache == null) {
                LOGGER.error('name=Cache_Manager_Invalid. Not exist Cache name : ' + IDENTITY_PI_TYPE_CACHE_NAME)
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + IDENTITY_PI_TYPE_CACHE_NAME)
            }

            return cache
        }
    }
}
