package com.junbo.common.cloudant
import com.fasterxml.jackson.core.JsonProcessingException
import com.junbo.common.cloudant.client.*
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.common.cloudant.model.CloudantUniqueItem
import com.junbo.common.model.Results
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

import java.lang.reflect.ParameterizedType
/**
 * CloudantClientBase.
 */
@CompileStatic
abstract class CloudantClientBase<T extends CloudantEntity> implements InitializingBean {
    private static final ThreadLocal<Boolean> useBulk = new ThreadLocal<>()

    // Set whether use bulk cloudant to commit changes. The value is reset at the end of the request automatically.
    public static boolean setUseBulk(Boolean value) {
        useBulk.set(value)
    }

    protected CloudantClientInternal getEffective() {
        // There are two layer of overriding:
        // 1. Use per instance setting to specify whether cache is used or not.
        // 2. Use the thread static setUseBulk to specify whether bulk is used or not.
        // When bulk is used, it will ignore cache setting. Now bulk is only used in migration.
        Boolean flag = useBulk.get()
        if (flag == null || flag == false || localIgnoreBulk) {
            return internal
        }
        return bulk
    }

    protected static String dbNamePrefix = ConfigServiceManager.instance().getConfigValue("common.cloudant.dbNamePrefix")
    protected static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()
    protected static CloudantUniqueClient cloudantUniqueClient = CloudantUniqueClient.instance()
    protected static CloudantClientBulk bulk = CloudantClientBulk.instance()

    private CloudantClientInternal internal

    protected CloudantGlobalUri cloudantGlobalUri
    protected CloudantDbUri cloudantDbUri
    protected Class<T> entityClass
    protected String dbName

    private boolean enableCache
    private boolean includeDocs
    private boolean localIgnoreBulk

    public CloudantClientBase() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
    }

    @Required
    void setCloudantDbUri(String cloudantDbUri) {
        cloudantGlobalUri = new CloudantGlobalUri(cloudantDbUri)
    }

    @Required
    void setDbName(String dbName) {
        this.dbName = dbName
    }

    @Required
    void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache
    }

    @Required
    void setIncludeDocs(boolean includeDocs) {
        this.includeDocs = includeDocs
    }

    void setLocalIgnoreBulk(boolean localIgnoreBulk) {
        this.localIgnoreBulk = localIgnoreBulk
    }

    @Override
    void afterPropertiesSet() throws Exception {
        Assert.isTrue(dbNamePrefix != null)
        this.cloudantDbUri = new CloudantDbUri(
            cloudantUri: cloudantGlobalUri.currentDcUri,
            dbName: dbName,
            fullDbName: dbNamePrefix + dbName
        )
        if (enableCache) {
            internal = CloudantClientCached.instance()
        } else {
            internal = CloudantClientImpl.instance()
        }
    }

    public CloudantDbUri getDbUri(String id) {
        return cloudantDbUri
    }

    public Promise<T> cloudantPost(T entity) {
        if (entity.id != null) {
            entity.cloudantId = entity.id.toString()
        }
        if (entity instanceof CloudantUnique) {
            // create unique items first
            Promise future = Promise.pure(null)
            if (entity.id == null) {
                future = CloudantIdGenerator.nextId().then { String id ->
                    entity.cloudantId = id
                }
            }
            return future.then {
                return postUnique(entity)
            }.then {
                return getEffective().cloudantPost(getDbUri(entity.cloudantId), entityClass, entity)
            }
        }
        return getEffective().cloudantPost(getDbUri(entity.cloudantId), entityClass, entity)
    }

    public Promise<T> cloudantGet(String id) {
        return getEffective().cloudantGet(getDbUri(id), entityClass, id.toString())
    }

    public Promise<T> cloudantPut(T entity, T oldEntity) {
        if (entity.id != null) {
            entity.cloudantId = entity.id.toString()
        }
        if (entity instanceof CloudantUnique) {
            // create unique items first
            return putUnique(entity).then {
                return getEffective().cloudantPut(getDbUri(entity.cloudantId), entityClass, entity)
            }
        }
        return getEffective().cloudantPut(getDbUri(entity.cloudantId), entityClass, entity)
    }

    public Promise<Void> cloudantDelete(String id) {
        return cloudantGet(id.toString()).then { T entity ->
            return cloudantDelete(entity)
        }
    }

    public Promise<Void> cloudantDelete(T entity) {
        if (entity == null) {
            return Promise.pure(null)
        }
        if (entity.id != null) {
            entity.cloudantId = entity.id.toString()
        }
        if (entity instanceof CloudantUnique) {
            return getEffective().cloudantDelete(getDbUri(entity.cloudantId), entityClass, entity).then {
                return deleteUnique(entity)
            }
        } else {
            return getEffective().cloudantDelete(getDbUri(entity.cloudantId), entityClass, entity)
        }
    }

    public Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending) {
        def future = getEffective().cloudantGetAll(getDbUri(null), entityClass, limit, skip, descending, this.includeDocs)
        if (!this.includeDocs) {
            future = future.then { CloudantQueryResult result ->
                return fetchDocs(result)
            }
        }
        return future.syncThen { CloudantQueryResult result ->
            if (result.rows != null) {
                return result.rows.collect { CloudantQueryResult.ResultObject row ->
                    return (T) row.doc
                }
            }
            return []
        }
    }

    public Promise<Results<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        def future = getEffective().cloudantGetAll(getDbUri(null), entityClass, limit, skip, descending, includeDocs)
        if (!this.includeDocs) {
            future = future.then { CloudantQueryResult result ->
                return fetchDocs(result)
            }
        }
        Results<T> results = new Results<>()
        return future.syncThen { CloudantQueryResult result ->
            results.setTotal(result.totalRows)
            if (result.rows != null) {
                results.items = result.rows.collect { CloudantQueryResult.ResultObject row ->
                    return (T) row.doc
                }
                return results
            }
            return results
        }
    }

    public Promise<Integer> queryViewTotal(String viewName, String key) {
        return getEffective().queryViewTotal(getDbUri(null), key, viewName)
    }

    public Promise<Integer> queryViewCount(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, boolean descending,
                                           Integer limit, Integer skip) {
        return getEffective().queryViewCount(getDbUri(null), entityClass, startKey, endKey, viewName, withHighKey, descending, limit, skip)
    }

    public Promise<CloudantQueryResult> queryView(String viewName, String key, Integer limit, Integer skip,
                                           boolean descending, boolean includeDocs) {
        if (includeDocs && !this.includeDocs) {
            // pass false to implementation method and try to fetch results one by one.
            // this means to allow higher cache hit rate
            return getEffective().queryView(getDbUri(null), entityClass, viewName, key, limit, skip, descending, false).then { CloudantQueryResult result ->
                return fetchDocs(result)
            }
        }
        return getEffective().queryView(getDbUri(null), entityClass, viewName, key, limit, skip, descending, includeDocs)
    }

    public Promise<List<T>> queryView(String viewName, String key, Integer limit, Integer skip,
                               boolean descending) {
        return getEffective().queryView(getDbUri(null), entityClass, viewName, key, limit, skip, descending, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) result.doc
                }
            }

            return []
        }
    }

    public Promise<List<T>> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    public Promise<CloudantQueryResult> queryView(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, null, null, false, includeDocs)
    }

    public Promise<List<T>> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey,
                                         Integer limit, Integer skip, boolean descending) {
        return getEffective().queryView(getDbUri(null), entityClass, viewName, startKey, endKey, withHighKey,
                limit, skip, descending, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) (result.doc)
                }
            }

            return []
        }
    }

    public Promise<List<T>> queryView(String viewName, String startKey, String endKey, Integer limit, Integer skip,
                               boolean descending) {
        return getEffective().queryView(getDbUri(null), entityClass, viewName, startKey, endKey, limit, skip, descending, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) (result.doc)
                }
            }

            return []
        }
    }

    public Promise<CloudantQueryResult> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        if (includeDocs && !this.includeDocs) {
            // pass false to implementation method and try to fetch results one by one.
            // this means to allow higher cache hit rate
            return getEffective().queryView(getDbUri(null), entityClass, viewName, startKey, endKey, withHighKey, limit, skip, descending, false).then { CloudantQueryResult result ->
                return fetchDocs(result)
            }
        }
        return getEffective().queryView(getDbUri(null), entityClass, viewName, startKey, endKey, withHighKey, limit, skip, descending, includeDocs)
    }

    public Promise<CloudantSearchResult<T>> search(String searchName, String queryString, String sort, Integer limit, String bookmark) {
        return getEffective().search(getDbUri(null), entityClass, searchName, queryString, sort, limit, bookmark, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return new CloudantSearchResult<T>(
                        results: searchResult.rows.unique {
                            CloudantQueryResult.ResultObject a, CloudantQueryResult.ResultObject b -> a.id<=>b.id
                        }.collect { CloudantQueryResult.ResultObject result ->
                            return result.doc
                        },
                        bookmark: searchResult.bookmark,
                        total: searchResult.totalRows
                )
            }

            return new CloudantSearchResult<T>(
                    results: [],
                    total: 0L
            )
        }
    }

    public Promise<CloudantSearchResult<T>> search(String searchName, String queryString, Integer limit, String bookmark) {
        return search(searchName, queryString, null, limit, bookmark)
    }

    public Promise<CloudantQueryResult> search(String searchName, String queryString, Integer limit, String bookmark,
                                                  boolean includeDocs) {
        if (includeDocs && !this.includeDocs) {
            // pass false to implementation method and try to fetch results one by one.
            // this means to allow higher cache hit rate
            return getEffective().search(getDbUri(null), entityClass, searchName, queryString, null, limit, bookmark, false).then { CloudantQueryResult result ->
                return fetchDocs(result)
            }
        }
        return getEffective().search(getDbUri(null), entityClass, searchName, queryString, null, limit, bookmark, includeDocs)
    }

    private Promise<CloudantQueryResult> fetchDocs(CloudantQueryResult result) {
        if (result.rows != null) {
            return Promise.each(result.rows) { CloudantQueryResult.ResultObject row ->
                    row.doc = cloudantGet(row.id).get()
                return Promise.pure(null)
            }.then {
                return Promise.pure(result)
            }
        } else {
            return Promise.pure(result)
        }
    }

    //region sync mode

    public T cloudantPostSync(T entity) {
        return (T) cloudantPost(entity).get()
    }

    public T cloudantGetSync(String id) {
        return (T) cloudantGet(id).get()
    }

    public T cloudantPutSync(T entity, T oldEntity) {
        return (T) cloudantPut(entity, oldEntity).get()
    }

    public void cloudantDeleteSync(String id) {
        cloudantDelete(id).get()
    }

    public void cloudantDeleteSync(T entity) {
        cloudantDelete(entity).get()
    }

    public List<T> cloudantGetAllSync(Integer limit, Integer skip, boolean descending) {
        return cloudantGetAll(limit, skip, descending).get()
    }

    public CloudantQueryResult queryViewSync(String viewName, String key, Integer limit, Integer skip,
                                                     boolean descending, boolean includeDocs) {
        return queryView(viewName, key, limit, skip, descending, includeDocs).get()
    }

    public List<T> queryViewSync(String viewName, String key, Integer limit, Integer skip,
                                         boolean descending) {
        return queryView(viewName, key, limit, skip, descending).get()
    }

    public List<T> queryViewSync(String viewName, String key) {
        return queryView(viewName, key).get()
    }

    public CloudantQueryResult queryViewSync(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, includeDocs).get()
    }

    public List<T> queryViewSync(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey,
                                         Integer limit, Integer skip, boolean descending) {
        return queryView(viewName, startKey, endKey, withHighKey, limit, skip, descending).get()
    }

    public List<T> queryViewSync(String viewName, String startKey, String endKey, Integer limit, Integer skip,
                                         boolean descending) {
        return queryView(viewName, startKey, endKey, limit, skip, descending).get()
    }

    public CloudantQueryResult queryViewSync(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        return queryView(viewName, startKey, endKey, withHighKey, limit, skip, descending, includeDocs).get()
    }

    public CloudantSearchResult<T> searchSync(String searchName, String queryString, String sort, Integer limit, String bookmark) {
        return search(searchName, queryString, sort, limit, bookmark).get()
    }

    public CloudantSearchResult<T> searchSync(String searchName, String queryString, Integer limit, String bookmark) {
        return search(searchName, queryString, limit, bookmark).get()
    }

    public CloudantQueryResult searchSync(String searchName, String queryString, Integer limit, String bookmark, boolean includeDocs) {
        return search(searchName, queryString, limit, bookmark, includeDocs).get()
    }

    //endregion

    //region marshall/unmarshall
    public String marshall(Object object) throws JsonProcessingException {
        return marshaller.marshall(object)
    }

    public <T> T unmarshall(String string, Class<T> clazz) throws IOException {
        return marshaller.unmarshall(string, clazz)
    }

    public <T> T unmarshall(String string, Class<T> parametrized, Class<?>... parameterClass) throws IOException {
        return marshaller.unmarshall(string, parametrized, parameterClass)
    }
    //endregion

    //region unique methods
    private Promise<Void> postUnique(T entity) {
        CloudantUnique unique = (CloudantUnique)entity;
        return Promise.each(Arrays.asList(unique.uniqueKeys)) { String key ->
            if (key != null) {
                CloudantUniqueItem item = new CloudantUniqueItem(id: key, doc: entity.cloudantId)
                return cloudantUniqueClient.create(item)
            } else {
                return Promise.pure(null)
            }
        }
    }

    private Promise<CloudantUniqueItem> putUnique(T entity) {
        CloudantUnique unique = (CloudantUnique)entity;
        String[] uniqueKeys = unique.uniqueKeys;

        return cloudantUniqueClient.getByDoc(entity.cloudantId).then { List<CloudantUniqueItem> oldItems ->
            int i = 0;
            return Promise.each(oldItems) { CloudantUniqueItem oldItem ->
                String newKey = uniqueKeys[i++];
                CloudantUniqueItem item = new CloudantUniqueItem(id: newKey, doc: entity.cloudantId)
                if (oldItem.id == item.id) {
                    return Promise.pure(null)
                } else {
                    return cloudantUniqueClient.create(item).then {
                        return cloudantUniqueClient.delete(oldItem)
                    }.syncThen {
                        return item
                    }
                }
            }
        }
    }

    private Promise<Void> deleteUnique(T entity) {
        return cloudantUniqueClient.getByDoc(entity.cloudantId).then { List<CloudantUniqueItem> oldItems ->
            return Promise.each(oldItems) { CloudantUniqueItem oldItem ->
                return cloudantUniqueClient.delete(oldItem);
            }
        }
    }
    //endregion
}
