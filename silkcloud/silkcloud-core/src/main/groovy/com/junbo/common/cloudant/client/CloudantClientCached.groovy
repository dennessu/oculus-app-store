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
    private Integer maxEntitySize
    private boolean storeViewResults
    private String currentDc

    private CloudantClientCached() {
        ConfigService configService = ConfigServiceManager.instance()

        String strMaxEntitySize = configService.getConfigValue("common.cloudant.cache.maxentitysize")
        String strExpiration = configService.getConfigValue("common.cloudant.cache.expiration")
        String strStoreViewResults = configService.getConfigValue("common.cloudant.cache.storeviewresults")

        this.expiration = safeParseInt(strExpiration)
        this.maxEntitySize = safeParseInt(strMaxEntitySize)
        this.storeViewResults = strStoreViewResults == "true"
        this.currentDc = DataCenters.instance().currentDataCenter()
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (entity.id != null) {
            // force update cloudantId
            entity.setCloudantId(entity.getId().toString())
            CloudantId.validate(entity.cloudantId)
        }

        return impl.cloudantPost(dbUri, entityClass, entity).then { T result ->
            updateCache(dbUri, entityClass, null, result)
            return Promise.pure(result)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        CloudantId.validate(id)
        CASValue<T> result = getCache(dbUri, entityClass, id)
        if (result == null) {
            return impl.cloudantGet(dbUri, entityClass, id).then { T resp ->
                updateCache(dbUri, entityClass, null, resp)     // result must be null
                return Promise.pure(resp)
            }
        }
        return Promise.pure(result.getValue())
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        CloudantId.validate(entity.cloudantId)

        def casValue = getCache(dbUri, entityClass, entity.cloudantId)
        return impl.cloudantPut(dbUri, entityClass, entity).recover { Throwable ex ->
            deleteCacheOnError(dbUri, entity.cloudantId)
            throw ex
        }.then { T result ->
            updateCache(dbUri, entityClass, casValue, result)
            return Promise.pure(result)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (entity == null) {
            return Promise.pure(null)
        }
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        CloudantId.validate(entity.cloudantId)

        return impl.cloudantDelete(dbUri, entityClass, entity).recover { Throwable ex ->
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
                updateCache(dbUri, entityClass, null, (T)row.doc)
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
                return new CASValue<T>(casValue.getCas(), result)
            } catch (Exception ex) {
                logger.warn("Error unmarshalling from memcached.", ex)
                deleteCacheOnError(dbUri, id)
            }
        } catch (Exception ex) {
            logger.warn("Error getting from memcached.", ex)
        }
        return null
    }

    def <T extends CloudantEntity> void updateCache(CloudantDbUri dbUri, Class<T> entityClass, CASValue<T> casValue, T entity) {
        if (memcachedClient == null || entity == null) {
            return
        }

        // update cloudantId
        if (entity.getId() != null) {
            entity.setCloudantId(entity.getId().toString())
        }
        if (entity.getCloudantId() == null || dbUri.cloudantUri.dc != currentDc) {
            // Don't cache if dc URI is not for current DC.
            return
        }

        try {
            String value = marshaller.marshall(entity)
            if (value.length() < this.maxEntitySize) {
                boolean isSuccessful = false
                if (casValue == null) {
                    isSuccessful = memcachedClient.add(getKey(dbUri, entity.cloudantId), this.expiration, value).get()
                } else {
                    CASResponse casResponse = memcachedClient.cas(getKey(dbUri, entity.cloudantId), casValue.getCas(), this.expiration, value)
                    isSuccessful = (casResponse == CASResponse.OK)
                }
                if (!isSuccessful) {
                    logger.warn("Update conflict for {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
                    deleteCacheOnError(dbUri, entity.cloudantId)
                }
            } else {
                memcachedClient.delete(getKey(dbUri, entity.cloudantId)).get()
                logger.warn("Entity {} of type {} oversized for memcached. Entity size: {}", entity.cloudantId, entityClass, value.length())
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Updated {} rev {} to memcached.", entity.cloudantId, entity.cloudantRev)
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
        return id + ":" + dbUri.fullDbName + ":" + dbUri.cloudantUri.dc
    }

    private static Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) return null
        return Integer.parseInt(str)
    }
}
