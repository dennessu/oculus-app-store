package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.id.CloudantId
import com.junbo.common.memcached.JunboMemcachedClient
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.configuration.topo.DataCenters
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.spy.memcached.CASResponse
import net.spy.memcached.CASValue
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * CloudantClient.
 */
@CompileStatic
class CloudantClientCached implements CloudantClientInternal {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientCached)

    private static CloudantClientCached singleton = new CloudantClientCached();
    public static CloudantClientCached instance() {
        return singleton
    }

    private static CloudantClientImpl impl = CloudantClientImpl.instance()
    private static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()

    private static JunboMemcachedClient memcachedClient = JunboMemcachedClient.instance()
    private Integer expiration
    private Map<String, Integer> expirationMap
    private Integer maxEntitySize
    private boolean storeViewResults
    private String currentDc
    private boolean retryAddForSnifferDelete

    private CloudantClientCached() {
        ConfigService configService = ConfigServiceManager.instance()

        String strMaxEntitySize = configService.getConfigValue("common.cloudant.cache.maxentitysize")
        String strExpiration = configService.getConfigValue("common.cloudant.cache.expiration")
        String strExpirationMap = configService.getConfigValue("common.cloudant.cache.expiration.map")
        String strStoreViewResults = configService.getConfigValue("common.cloudant.cache.storeviewresults")
        String strRetryAddForSnifferDelete = configService.getConfigValue("common.cloudant.cache.retryAddForSnifferDelete")

        this.expiration = safeParseInt(strExpiration)
        this.expirationMap = parseExpirationMap(strExpirationMap)
        this.maxEntitySize = safeParseInt(strMaxEntitySize)
        this.storeViewResults = strStoreViewResults == "true"
        this.currentDc = DataCenters.instance().currentDataCenter()
        this.retryAddForSnifferDelete = Boolean.parseBoolean(strRetryAddForSnifferDelete)
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        if (entity.id != null) {
            // force update cloudantId
            entity.setCloudantId(entity.getId().toString())
            CloudantId.validate(entity.cloudantId)
        }

        return impl.cloudantPost(dbUri, entityClass, entity, noOverrideWrites).then { T result ->
            addCache(dbUri, entityClass, result)
            return Promise.pure(result)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        CloudantId.validate(id)
        CASValue<T> result = getCache(dbUri, entityClass, id)
        if (result == null) {
            return impl.cloudantGet(dbUri, entityClass, id).then { T resp ->
                addCache(dbUri, entityClass, resp);
                return Promise.pure(resp)
            }
        }
        return Promise.pure(result.getValue())
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        CloudantId.validate(entity.cloudantId)

        String revBeforePut = entity.cloudantRev
        return impl.cloudantPut(dbUri, entityClass, entity, noOverrideWrites).recover { Throwable ex ->
            deleteCacheOnError(dbUri, entity.cloudantId)
            throw ex
        }.then { T result ->
            updateCache(dbUri, entityClass, result, revBeforePut)
            return Promise.pure(result)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        if (entity == null) {
            return Promise.pure(null)
        }
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        CloudantId.validate(entity.cloudantId)

        return impl.cloudantDelete(dbUri, entityClass, entity, noOverrideWrites).recover { Throwable ex ->
            deleteCacheOnError(dbUri, entity.cloudantId)
            throw ex
        }.then {
            deleteCache(dbUri, entity.cloudantId)
            return Promise.pure(null)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        Promise<CloudantQueryResult> future = impl.cloudantGetAll(dbUri, entityClass, limit, skip, descending, includeDocs)
        if (includeDocs && storeViewResults) {
            future = updateCache(dbUri, entityClass, future)
        }
        return future
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String key, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        Promise<CloudantQueryResult> future = impl.queryView(dbUri, entityClass, viewName, key, limit, skip, descending, includeDocs)
        if (includeDocs && storeViewResults) {
            future = updateCache(dbUri, entityClass, future)
        }
        return future
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String startKey, String endKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        Promise<CloudantQueryResult> future = impl.queryView(dbUri, entityClass, viewName, startKey, endKey, limit, skip, descending, includeDocs)
        if (includeDocs && storeViewResults) {
            future = updateCache(dbUri, entityClass, future)
        }
        return future
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, Object[] startKey, Object[] endKey,
                                                                          boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        Promise<CloudantQueryResult> future = impl.queryView(dbUri, entityClass, viewName, startKey, endKey, withHighKey, limit, skip, descending, includeDocs)
        if (includeDocs && storeViewResults) {
            future = updateCache(dbUri, entityClass, future)
        }
        return future
    }

    @Override
    Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String key, String viewName) {
        return impl.queryViewTotal(dbUri, key, viewName)
    }

    @Override
    Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, boolean descending) {
        return impl.queryViewTotal(dbUri, viewName, startKey, endKey, withHighKey, descending)
    }

    @Override
    def <T extends CloudantEntity> Promise<Integer> queryViewCount(CloudantDbUri dbUri, Class<T> entityClass,
                             Object[] startKey, Object[] endKey, String viewName, boolean withHighKey, boolean descending, Integer limit, Integer skip) {
        return impl.queryViewCount(dbUri, entityClass, startKey, endKey, viewName, withHighKey, descending, limit, skip)
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName, String queryString, String sort, Integer limit, String bookmark, boolean includeDocs) {
        Promise<CloudantQueryResult> future = impl.search(dbUri, entityClass, searchName, queryString, sort, limit, bookmark, includeDocs)
        if (includeDocs && storeViewResults) {
            future = updateCache(dbUri, entityClass, future)
        }
        return future
    }

    def <T extends CloudantEntity> Promise<CloudantQueryResult> updateCache(CloudantDbUri dbUri, Class<T> entityClass, Promise<CloudantQueryResult> future) {
        return future.then { CloudantQueryResult result ->
            result.rows.each { CloudantQueryResult.ResultObject row ->
                addCache(dbUri, entityClass, (T)row.doc)
            }
            return Promise.pure(result)
        }
    }

    def <T extends CloudantEntity> CASValue<T> getCache(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        if (memcachedClient == null) {
            return null
        }
        try {
            CASValue<String> casValue = (CASValue<String>)memcachedClient.gets(getKey(dbUri, id))
            if (casValue == null) {
                return null
            }
            try {
                T result = (T) marshaller.unmarshall(casValue.value, entityClass)
                if (result != null && logger.isDebugEnabled()) {
                    logger.debug("Found {} rev {} from memcached.", result.cloudantId, result.cloudantRev)
                }
                if (result != null) {
                    return new CASValue<T>(casValue.getCas(), result)
                }
            } catch (Exception ex) {
                logger.warn("Error unmarshalling from memcached.", ex)
                deleteCacheOnError(dbUri, id)
            }
        } catch (Exception ex) {
            logger.warn("Error getting from memcached.", ex)
        }
        return null
    }

    def <T extends CloudantEntity> boolean checkCachable(CloudantDbUri dbUri, T entity) {
        if (memcachedClient == null || entity == null) {
            return false
        }

        // update cloudantId
        if (entity.getId() != null) {
            entity.setCloudantId(entity.getId().toString())
        }

        // check cachable
        if (entity.getCloudantId() == null || dbUri.cloudantUri.dc != currentDc) {
            // Don't cache if dc URI is not for current DC.
            return false
        }

        return true
    }

    def <T extends CloudantEntity> void addCache(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (!checkCachable(dbUri, entity)) {
            return
        }

        try {
            String value = marshaller.marshall(entity)
            if (value.length() < this.maxEntitySize) {
                def isSuccessful = memcachedClient.add(getKey(dbUri, entity.cloudantId), getExpiration(dbUri), value).get()
                if (!isSuccessful) {
                    logger.warn("Update conflict for {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
                    deleteCacheOnError(dbUri, entity.cloudantId)
                } else {
                    logger.debug("Added {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
                }
            } else {
                memcachedClient.delete(getKey(dbUri, entity.cloudantId)).get()
                logger.warn("Entity {} of type {} oversized for memcached. Entity size: {}", entity.cloudantId, entityClass, value.length())
            }
        } catch (Exception ex) {
            logger.warn("Error writing to memcached.", ex)
            deleteCacheOnError(dbUri, entity.cloudantId)
        }
    }

    def <T extends CloudantEntity> void updateCache(CloudantDbUri dbUri, Class<T> entityClass, T entity, String prevRev) {
        if (!checkCachable(dbUri, entity)) {
            return
        }

        def casValue = getCache(dbUri, entityClass, entity.getCloudantId())
        if (casValue == null) {
            addCache(dbUri, entityClass, entity);
            return;
        }

        try {
            String value = marshaller.marshall(entity)
            if (value.length() < this.maxEntitySize) {
                boolean isSuccessful = false
                if (casValue.getValue().cloudantRev == prevRev) {
                    def casResponse = memcachedClient.cas(getKey(dbUri, entity.cloudantId), casValue.getCas(), getExpiration(dbUri), value)
                    isSuccessful = (casResponse == CASResponse.OK)
                } else if (casValue.getValue().cloudantRev == entity.cloudantRev) {
                    logger.info("Entity {} rev {} already cached in memcached. Skip storing rev {}.", entity.cloudantId, casValue.value.cloudantRev, entity.cloudantRev)
                    isSuccessful = true
                }
                if (!isSuccessful) {        // Update conflict
                    if (retryAddForSnifferDelete) {
                        // Most likely it conflicted with sniffer. Sniffer will delete the entity from the cache, so give a try to add directly.
                        logger.info("Update conflict detected, trying to directly add to cache for {} rev {}", entity.cloudantId, entity.cloudantRev)
                        isSuccessful = memcachedClient.add(getKey(dbUri, entity.cloudantId), getExpiration(dbUri), value).get()
                    }

                    if (!isSuccessful) {
                        logger.warn("Update conflict for {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
                        deleteCacheOnError(dbUri, entity.cloudantId)
                    }
                } else {
                    logger.debug("Updated {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
                }
            } else {
                memcachedClient.delete(getKey(dbUri, entity.cloudantId)).get()
                logger.warn("Entity {} of type {} oversized for memcached. Entity size: {}", entity.cloudantId, entityClass, value.length())
            }
        } catch (Exception ex) {
            logger.warn("Error writing to memcached.", ex)
            deleteCacheOnError(dbUri, entity.cloudantId)
        }
    }

    void deleteCache(CloudantDbUri dbUri, String id) {
        if (memcachedClient == null) {
            return
        }
        try {
            memcachedClient.delete(getKey(dbUri, id)).get()
        } catch (Exception ex) {
            logger.warn("Error deleting from memcached.", ex)
        }
    }

    void deleteCacheOnError(CloudantDbUri dbUri, String id) {
        if (memcachedClient == null) {
            return
        }
        try {
            // async delete
            memcachedClient.delete(getKey(dbUri, id))
        } catch (Exception ex) {
            logger.warn("Error deleting key on error from memcached.", ex)
        }
    }

    private String getKey(CloudantDbUri dbUri, String id) {
        return id + ":" + dbUri.dbName
    }

    private Integer getExpiration(CloudantDbUri dbUri) {
        Integer result = expirationMap?.get(dbUri.dbName)
        if (result != null) {
            return result
        }
        return expiration
    }

    private Map<String, Integer> parseExpirationMap(String strExpirationMap) {
        if (StringUtils.isEmpty(strExpirationMap)) {
            return null
        }
        Map<String, Integer> result = new HashMap<>()
        for (String expirationMapItem : strExpirationMap.split(",")) {
            String [] dbExpiration = expirationMapItem.split(":", 2)
            if (dbExpiration.size() != 2) {
                throw new RuntimeException("Invalid expiration item: " + expirationMapItem);
            }
            try {
                result.put(dbExpiration[0].trim(), Integer.parseInt(dbExpiration[1].trim()))
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Invalid expiration item: " + expirationMapItem);
            }
        }
        return result
    }

    private static Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) return null
        return Integer.parseInt(str)
    }
}
