package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.v1.model.Locale
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
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class LocaleRepositoryCloudantImpl extends CloudantClient<Locale> implements LocaleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleRepositoryCloudantImpl)
    private static final String IDENTITY_LOCALE_CACHE_NAME = 'IDENTITY_LOCALE'

    private CacheManager cacheManager

    @Override
    Promise<Locale> create(Locale model) {
        if (model.id == null) {
            model.id = new LocaleId(model.localeCode)
        }

        return cloudantPost(model).then { Locale locale ->
            Element element = new Element(locale.getId().value, locale)
            getLocaleCache().put(element)
            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Locale> update(Locale model, Locale oldModel) {
        return cloudantPut(model, oldModel).then { Locale locale ->
            Element element = new Element(locale.getId().value, locale)
            getLocaleCache().put(element)
            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Locale> get(LocaleId id) {
        Element element = getLocaleCache().get(id.value)

        if (element != null) {
            return Promise.pure((Locale)element.objectValue)
        }

        return cloudantGet(id.toString()).then { Locale locale ->
            element = new Element(locale.getId().value, locale)
            getLocaleCache().put(element)
            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        return cloudantDelete(id.toString()).then {
            getLocaleCache().remove(id.value)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<Locale>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    public Cache getLocaleCache() {
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid.')
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            Cache cache = cacheManager.getCache(IDENTITY_LOCALE_CACHE_NAME)
            if (cache == null) {
                LOGGER.error('name=Cache_Manager_Invalid. Not exist Cache name : ' + IDENTITY_LOCALE_CACHE_NAME)
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + IDENTITY_LOCALE_CACHE_NAME)
            }

            return cache
        }
    }
}
