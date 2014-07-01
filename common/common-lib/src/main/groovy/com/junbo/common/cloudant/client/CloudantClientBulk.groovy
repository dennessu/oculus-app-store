package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantClientBase
import com.junbo.common.cloudant.CloudantEntity
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
class CloudantClientBulk<T extends CloudantEntity> implements CloudantClient<T> {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientBulk)

    static class BulkData {
        Map<String, String> cloudantCache = new HashMap<>()
        Map<String, Map<String, CloudantEntity>> cloudantBulk = new HashMap<>()
    }

    private static final ThreadLocal<BulkData> threadLocalBulkData = new ThreadLocal<>()
    private static final ThreadLocal<Callback> threadLocalCallback = new ThreadLocal<>()

    private static boolean parallelCommit = ConfigServiceManager.instance().getConfigValue("common.cloudant.bulk.parallelCommit") == "true"

    public static interface Callback {
        void onQueryView(CloudantQueryResult results, String fullDbName, String dbName, String viewName, String key, String startKey, String endKey)
        void onSearch(CloudantQueryResult results, String fullDbName, String dbName, String searchName, String queryString)
    }

    private static String uniqueDbName
    private static CloudantClientImpl commitImpl

    private CloudantClientImpl<T> impl

    CloudantClientBulk(CloudantClientImpl<T> impl) {
        this.impl = impl
        if (commitImpl == null) {
            CloudantClientBulk.commitImpl = impl
        }
    }

    void setUniqueDbName(String uniqueDbName) {
        this.uniqueDbName = uniqueDbName
    }

    static void setCallback(Callback callback) {
        threadLocalCallback.set(callback)
    }

    static BulkData getBulkDataReadonly() {
        BulkData data = threadLocalBulkData.get()
        if (data == null) {
            return new BulkData()
        }
        return data
    }

    static Map<String, CloudantEntity> getBulkReadonly(String fullDbName) {
        BulkData data = threadLocalBulkData.get()
        if (data == null) {
            return new HashMap<>()
        }
        Map<String, CloudantEntity> result = data.cloudantBulk.get(fullDbName)
        if (result == null) {
            return new HashMap<>()
        }
        return result
    }

    static void clearBulkData() {
        threadLocalBulkData.remove()
    }

    @Override
    Promise<T> cloudantPost(T entity) {
        if (entity.id == null) {
            // must be cloudant based id, assign a string is okay
            return CloudantIdGenerator.nextId().then { String id ->
                entity.cloudantId = id
                return Promise.pure(null)
            }.then {
                addCreated(entity)
                return Promise.pure(entity)
            }
        } else {
            addCreated(entity)
            return Promise.pure(entity)
        }
    }

    @Override
    Promise<T> cloudantGet(String id) {
        T result = (T)getCached(id)
        if (result == null) {
            return impl.cloudantGet(id).then { T resp ->
                addCached(resp)
                return Promise.pure(resp)
            }
        }
        return Promise.pure(result)
    }

    @Override
    Promise<T> cloudantPut(T entity) {
        addChanged(entity)
        return Promise.pure(entity)
    }

    @Override
    Promise<Void> cloudantDelete(T entity) {
        if (entity == null) {
            return Promise.pure(null)
        }
        delete(entity.cloudantId)
        return impl.cloudantDelete(entity)
    }

    @Override
    Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending) {
        return impl.cloudantGetAll(limit, skip, descending)
    }

    @Override
    Promise<CloudantQueryResult> queryView(String viewName, String key, String startKey, String endKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        return impl.queryView(viewName, key, startKey, endKey, limit, skip, descending, includeDocs).then { CloudantQueryResult results ->
            Callback callback = threadLocalCallback.get()
            if (callback != null) {
                callback.onQueryView(results, impl.fullDbName, impl.dbName, viewName, key, startKey, endKey)
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
    Promise<CloudantQueryResult> search(String searchName, String queryString, Integer limit, String bookmark, boolean includeDocs) {
        return impl.search(searchName, queryString, limit, bookmark, includeDocs).then { CloudantQueryResult results ->
            Callback callback = threadLocalCallback.get()
            if (callback != null) {
                callback.onSearch(results, impl.fullDbName, impl.dbName, searchName, queryString)
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

        Closure<Promise> commitToDb = { String dbName ->
            CloudantBulkDocs bulkDocs = new CloudantBulkDocs()
            bulkDocs.docs = (CloudantEntity[])cloudantBulk.get(dbName).values().toArray(new CloudantEntity[0])

            if (bulkDocs.docs.length > 0) {
                logger.info("Committing {} docs to {}", bulkDocs.docs.length, dbName)
                return commitImpl.executeRequest(HttpMethod.POST, dbName + "/_bulk_docs", null, bulkDocs, false).then { Response response ->
                    cloudantBulk.get(dbName).clear()
                    if ((int) (response.statusCode / 100) != 2) {
                        CloudantError cloudantError = commitImpl.unmarshall(response.responseBody, CloudantError)
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
        if (cloudantBulk.containsKey(uniqueDbName)) {
            return commitToDb.call(uniqueDbName).syncThen {
                cloudantBulk.remove(uniqueDbName)
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

    private Map<String, CloudantEntity> getBulk(String dbName) {
        Map<String, CloudantEntity> result = getBulkData().cloudantBulk.get(dbName)
        if (result == null) {
            result = new HashMap<>()
            getBulkData().cloudantBulk.put(dbName, result)
        }
        return result
    }

    private Map<String, String> getCache() {
        return getBulkData().cloudantCache;
    }

    private void addCreated(CloudantEntity entity) {
        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def bulk = getBulk(impl.fullDbName)
        if (bulk.containsKey(entity.cloudantId)) {
            throw new CloudantUpdateConflictException("Failed to save object to CloudantDB, id conflict in ${impl.fullDbName} for ${entity.cloudantId}")
        }

        def value = impl.marshall(entity)
        bulk.put(entity.cloudantId, (T)impl.unmarshall(value, impl.entityClass))
        getCache().put(entity.cloudantId, value)
    }

    private void addChanged(CloudantEntity entity) {
        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def value = impl.marshall(entity)
        getBulk(impl.fullDbName).put(entity.cloudantId, (T)impl.unmarshall(value, impl.entityClass))
        getCache().put(entity.cloudantId, value)
    }

    private void addCached(CloudantEntity entity) {
        if (entity == null) {
            return
        }

        // update cloudantId
        entity.setCloudantId(entity.getId().toString());

        def value = impl.marshall(entity)
        getCache().put(entity.cloudantId, value)
    }

    private void delete(String id) {
        getBulk(impl.fullDbName).remove(id)
        getCache().remove(id)
    }

    private T getCached(String id) {
        return (T)impl.unmarshall(getCache().get(id), impl.entityClass)
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
