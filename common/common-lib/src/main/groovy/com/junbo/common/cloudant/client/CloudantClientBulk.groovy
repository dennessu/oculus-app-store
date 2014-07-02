package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantClientBase
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.CloudantUniqueClient
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
import com.junbo.common.cloudant.model.CloudantBulkDocs
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.util.Context
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

/**
 * CloudantClient.
 */
@CompileStatic
class CloudantClientBulk implements CloudantClient {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientBulk)

    private static CloudantClientBulk singleton = new CloudantClientBulk();
    public static CloudantClientBulk instance() {
        return singleton
    }

    public static interface Callback {
        void onQueryView(CloudantQueryResult results, CloudantDbUri dbUri, String viewName, String key, String startKey, String endKey)
        void onSearch(CloudantQueryResult results, CloudantDbUri dbUri, String searchName, String queryString)
    }

    /**
     * Set the callback when queryView or search is called
     * @param callback the callback to inspect the data which is not yet committed.
     */
    public static void setCallback(Callback callback) {
        threadLocalCallback.set(callback)
    }

    public static class EntityWithType {
        String entity
        Class type
    }

    public static class BulkData {
        Map<String, String> cloudantCache = new HashMap<>()
        Map<CloudantDbUri, Map<String, EntityWithType>> cloudantBulk = new HashMap<>()
    }

    private static final ThreadLocal<BulkData> threadLocalBulkData = new ThreadLocal<>()
    private static final ThreadLocal<Callback> threadLocalCallback = new ThreadLocal<>()

    private static BulkData getBulkDataReadonly() {
        BulkData data = threadLocalBulkData.get()
        if (data == null) {
            return new BulkData()
        }
        return data
    }

    private static boolean parallelCommit = ConfigServiceManager.instance().getConfigValue("common.cloudant.bulk.parallelCommit") == "true"

    public static Map<String, EntityWithType> getBulkReadonly(CloudantDbUri dbUri) {
        BulkData data = threadLocalBulkData.get()
        if (data == null) {
            return new HashMap<>()
        }
        Map<String, EntityWithType> result = data.cloudantBulk.get(dbUri)
        if (result == null) {
            return new HashMap<>()
        }
        return result
    }

    public static void clearBulkData() {
        threadLocalBulkData.remove()
    }

    private static CloudantDbUri uniqueDbUri = CloudantUniqueClient.instance().getDbUri(null)
    private static CloudantClientImpl impl = CloudantClientImpl.instance()
    private static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (entity.id == null) {
            // must be cloudant based id, assign a string is okay
            return CloudantIdGenerator.nextId().then { String id ->
                entity.cloudantId = id
                return Promise.pure(null)
            }.then {
                addCreated(dbUri, entity, entityClass)
                return Promise.pure(entity)
            }
        } else {
            addCreated(dbUri, entity, entityClass)
            return Promise.pure(entity)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        T result = getCached(id, entityClass)
        if (result == null) {
            return impl.cloudantGet(dbUri, entityClass, id).then { T resp ->
                addCached(resp)
                return Promise.pure(resp)
            }
        }
        return Promise.pure(result)
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        addChanged(dbUri, entity, entityClass)
        return Promise.pure(entity)
    }

    @Override
    def <T extends CloudantEntity> Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (entity == null) {
            return Promise.pure(null)
        }
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())

        delete(dbUri, entity.cloudantId)
        return impl.cloudantDelete(dbUri, entityClass, entity)
    }

    @Override
    def <T extends CloudantEntity> Promise<List<T>> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending) {
        return impl.cloudantGetAll(dbUri, entityClass, limit, skip, descending)
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String key, String startKey, String endKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        return impl.queryView(dbUri, entityClass, viewName, key, startKey, endKey, limit, skip, descending, includeDocs).then { CloudantQueryResult results ->
            Callback callback = threadLocalCallback.get()
            if (callback != null) {
                callback.onQueryView(results, dbUri, viewName, key, startKey, endKey)
            }
            if (includeDocs) {
                results.rows.each { CloudantQueryResult.ResultObject item ->
                    addCached((CloudantEntity)item.doc)
                }
            }
            return Promise.pure(results)
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName, String queryString, Integer limit, String bookmark, boolean includeDocs) {
        return impl.search(dbUri, entityClass, searchName, queryString, limit, bookmark, includeDocs).then { CloudantQueryResult results ->
            Callback callback = threadLocalCallback.get()
            if (callback != null) {
                callback.onSearch(results, dbUri, searchName, queryString)
            }
            if (includeDocs) {
                results.rows.each { CloudantQueryResult.ResultObject item ->
                    addCached((CloudantEntity)item.doc)
                }
            }
            return Promise.pure(results)
        }
    }

    static Promise<Void> commit() {
        def cloudantBulk = getBulkDataReadonly().cloudantBulk

        Closure<Promise> commitToDb = { CloudantDbUri dbUri ->
            CloudantBulkDocs bulkDocs = new CloudantBulkDocs()
            bulkDocs.docs = (CloudantEntity[])cloudantBulk.get(dbUri).values().toArray(new CloudantEntity[0])

            if (bulkDocs.docs.length > 0) {
                logger.info("Committing {} docs to {}", bulkDocs.docs.length, dbUri)
                return impl.executeRequest(dbUri, HttpMethod.POST, "/_bulk_docs", null, bulkDocs).then { Response response ->
                    cloudantBulk.get(dbUri).clear()
                    if ((int) (response.statusCode / 100) != 2) {
                        CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                        throw new CloudantException("Failed to bulk commit, error: $cloudantError.error," +
                                " reason: $cloudantError.reason")
                    }
                    return Promise.pure(null)
                }
            }
            return Promise.pure(null)
        }

        Closure<Promise> commitAllDbs = {
            if (parallelCommit) {
                return Promise.all(cloudantBulk.keySet(), commitToDb).then {
                    cloudantBulk.clear()
                    return Promise.pure(null)
                }
            } else {
                return (Promise<Void>) Promise.each(cloudantBulk.keySet(), commitToDb).then {
                    cloudantBulk.clear()
                    return Promise.pure(null)
                }
            }
        }

        // commit to unique table first, then other tables
        if (cloudantBulk.containsKey(uniqueDbUri)) {
            return commitToDb.call(uniqueDbUri).syncThen {
                cloudantBulk.remove(uniqueDbUri)
            }.then {
                return commitAllDbs.call()
            }
        } else {
            return commitAllDbs.call()
        }
    }

    private BulkData getBulkData() {
        BulkData value = threadLocalBulkData.get();
        if (value == null) {
            value = new BulkData();
            threadLocalBulkData.set(value);
            Context.get().registerCleanupActions {
                return onRequestFinished()
            }
        }
        return value;
    }

    private Map<String, EntityWithType> getOrCreateBulk(CloudantDbUri dbUri) {
        Map<String, EntityWithType> result = getBulkData().cloudantBulk.get(dbUri)
        if (result == null) {
            result = new HashMap<>()
            getBulkData().cloudantBulk.put(dbUri, result)
        }
        return result
    }

    private Map<String, String> getCache() {
        return getBulkData().cloudantCache;
    }

    private void addCreated(CloudantDbUri dbUri, CloudantEntity entity, Class entityClass) {
        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def bulk = getOrCreateBulk(dbUri)
        if (bulk.containsKey(entity.cloudantId)) {
            throw new CloudantUpdateConflictException("Failed to save object to CloudantDB, id conflict in $dbUri for ${entity.cloudantId}")
        }

        def value = marshaller.marshall(entity)
        bulk.put(entity.cloudantId, new EntityWithType(entity: value, type: entityClass))
        getCache().put(entity.cloudantId, value)
    }

    private void addChanged(CloudantDbUri dbUri, CloudantEntity entity, Class entityClass) {
        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def bulk = getOrCreateBulk(dbUri)
        def value = marshaller.marshall(entity)
        bulk.put(entity.cloudantId, new EntityWithType(entity: value, type: entityClass))
        getCache().put(entity.cloudantId, value)
    }

    private void addCached(CloudantEntity entity) {
        if (entity == null) {
            return
        }

        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def value = marshaller.marshall(entity)
        getCache().put(entity.cloudantId, value)
    }

    private void delete(CloudantDbUri dbUri, String id) {
        getOrCreateBulk(dbUri).remove(id)
        getCache().remove(id)
    }

    private <T> T getCached(String id, Class<T> entityClass) {
        return (T)marshaller.unmarshall(getCache().get(id), entityClass)
    }

    private Promise<Void> onRequestFinished() {
        return commit().then {
            CloudantClientBase.setUseBulk(false)
            threadLocalBulkData.remove()
            threadLocalCallback.remove()
            return Promise.pure(null)
        }
    }
}
