package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantClientBase
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.model.CloudantBulkDocs
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantUuids
import com.junbo.common.util.Context
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import java.util.concurrent.ConcurrentLinkedQueue
/**
 * CloudantClient.
 */
@CompileStatic
class CloudantClientBulk<T extends CloudantEntity> implements CloudantClient<T> {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientBulk)

    public static final String GENERATE_ID = '/_uuids'

    static class BulkData {
        Map<String, String> cloudantCache = new HashMap<>()
        Map<String, Map<String, CloudantEntity>> cloudantBulk = new HashMap<>()
    }

    private static final ThreadLocal<BulkData> threadLocalBulkData = new ThreadLocal<>()
    private static final ThreadLocal<Callback> threadLocalCallback = new ThreadLocal<>()
    private static final ConcurrentLinkedQueue<String> cloudantIds = new ConcurrentLinkedQueue<>()

    private static boolean parallelCommit = ConfigServiceManager.instance().getConfigValue("cloudant.bulk.parallelCommit") == "true"

    private static interface Callback {
        void onQueryView(String dbName, String viewName, String key, String startKey, String endKey)
        void onSearch(String dbName, String searchName, String queryString)
    }

    private CloudantClientImpl<T> impl

    CloudantClientBulk(CloudantClientImpl<T> impl) {
        this.impl = impl
    }

    public static void setCallback(Callback callback) {
        threadLocalCallback.set(callback)
    }

    @Override
    Promise<T> cloudantPost(T entity) {
        if (entity.id == null) {
            // must be cloudant based id, assign a string is okay
            return nextId().then { String id ->
                entity.cloudantId = id
                return Promise.pure(null)
            }.then {
                addChanged(entity)
                return Promise.pure(entity)
            }
        } else {
            addChanged(entity)
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
    Promise<Void> cloudantDelete(String id) {
        delete(id)
        return impl.cloudantDelete(id)
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
                callback.onQueryView(impl.fullDbName, viewName, key, startKey, endKey)
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
                callback.onSearch(impl.fullDbName, searchName, queryString)
            }
            if (includeDocs) {
                results.rows.each { CloudantQueryResult.ResultObject item ->
                    addCached((CloudantEntity)item.doc)
                }
            }
            return Promise.pure(results)
        }
    }

    Promise<Void> commit() {
        def cloudantBulk = getBulkData().cloudantBulk

        Closure<Promise<? extends T>> closure = { String dbName ->
            CloudantBulkDocs bulkDocs = new CloudantBulkDocs()
            bulkDocs.docs = (CloudantEntity[])cloudantBulk.get(dbName).values().toArray(new CloudantEntity[0])

            if (bulkDocs.docs.length > 0) {
                logger.info("Committing {} docs to {}", bulkDocs.docs.length, dbName)
                return impl.executeRequest(HttpMethod.POST, dbName + "/_bulk_docs", null, bulkDocs, false).then { Response response ->
                    cloudantBulk.get(dbName).clear()
                    if ((int) (response.statusCode / 100) != 2) {
                        CloudantError cloudantError = impl.unmarshall(response.responseBody, CloudantError)
                        throw new CloudantException("Failed to bulk commit, error: $cloudantError.error," +
                                " reason: $cloudantError.reason")
                    }
                    return Promise.pure(null)
                }
            }
            return Promise.pure(null)
        }

        if (parallelCommit) {
            return (Promise<Void>)Promise.all(cloudantBulk.keySet(), closure).then {
                cloudantBulk.clear()
                return Promise.pure(null)
            }
        } else {
            return (Promise<Void>) Promise.each(cloudantBulk.keySet(), closure).then {
                cloudantBulk.clear()
                return Promise.pure(null)
            }
        }
    }

    private Promise<String> nextId() {
        String result = cloudantIds.poll();
        if (result != null) {
            return Promise.pure(result);
        }

        return impl.executeRequest(HttpMethod.GET, GENERATE_ID, ["count": "100"] as Map<String, String>, null, false).then { Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = impl.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to generate IDs, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            CloudantUuids uuids = impl.unmarshall(response.responseBody, CloudantUuids)
            result = uuids.uuids.first()

            // add others to the pool
            cloudantIds.addAll(uuids.uuids.tail())

            return Promise.pure(result)
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

    private void addChanged(CloudantEntity entity) {
        def value = impl.marshall(entity)
        getBulk(impl.fullDbName).put(entity.cloudantId, (T)impl.unmarshall(value, impl.entityClass))
        getCache().put(entity.cloudantId, value)
    }

    private void addCached(CloudantEntity entity) {
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
