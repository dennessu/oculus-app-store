package com.junbo.common.cloudant
import com.fasterxml.jackson.core.JsonProcessingException
import com.junbo.common.cloudant.client.*
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.common.cloudant.model.CloudantUniqueItem
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

    protected static com.junbo.common.cloudant.client.CloudantClient getEffective() {
        Boolean flag = useBulk.get()
        if (flag == null || flag == false) {
            return impl
        }
        return bulk
    }

    protected static String dbNamePrefix = ConfigServiceManager.instance().getConfigValue("common.cloudant.dbNamePrefix")
    protected static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()
    protected static CloudantUniqueClient cloudantUniqueClient = CloudantUniqueClient.instance()
    protected static CloudantClientImpl impl = CloudantClientImpl.instance()
    protected static CloudantClientBulk bulk = CloudantClientBulk.instance()

    protected CloudantGlobalUri cloudantGlobalUri
    protected CloudantDbUri cloudantDbUri
    protected Class<T> entityClass
    protected String dbName

    protected CloudantClientBase() {
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

    @Override
    void afterPropertiesSet() throws Exception {
        Assert.isTrue(dbNamePrefix != null)
        this.cloudantDbUri = new CloudantDbUri(
            cloudantUri: cloudantGlobalUri.currentDcUri,
            dbName: dbName,
            fullDbName: dbNamePrefix + dbName
        )
    }

    public CloudantDbUri getDbUri(String id) {
        return cloudantDbUri
    }

    protected Promise<T> cloudantPost(T entity) {
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

    protected Promise<T> cloudantGet(String id) {
        return getEffective().cloudantGet(getDbUri(id), entityClass, id.toString())
    }

    protected Promise<T> cloudantPut(T entity) {
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

    protected Promise<Void> cloudantDelete(String id) {
        return cloudantGet(id.toString()).then { T entity ->
            return cloudantDelete(entity)
        }
    }

    protected Promise<Void> cloudantDelete(T entity) {
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

    protected Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending) {
        return getEffective().cloudantGetAll(getDbUri(null), entityClass, limit, skip, descending)
    }

    protected Promise<CloudantQueryResult> queryView(String viewName, String key, Integer limit, Integer skip,
                                           boolean descending, boolean includeDocs) {
        return getEffective().queryView(getDbUri(null), entityClass, viewName, key, limit, skip, descending, includeDocs)
    }

    protected Promise<List<T>> queryView(String viewName, String key, Integer limit, Integer skip,
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

    protected Promise<List<T>> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    protected Promise<CloudantQueryResult> queryView(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, null, null, false, includeDocs)
    }

    protected Promise<List<T>> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey,
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

    protected Promise<List<T>> queryView(String viewName, String startKey, String endKey, Integer limit, Integer skip,
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

    protected Promise<CloudantQueryResult> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        return getEffective().queryView(getDbUri(null), entityClass, viewName, startKey, endKey, withHighKey, limit, skip, descending, includeDocs)
    }

    protected Promise<CloudantSearchResult<T>> search(String searchName, String queryString, Integer limit, String bookmark) {
        return getEffective().search(getDbUri(null), entityClass, searchName, queryString, limit, bookmark, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return new CloudantSearchResult<T>(
                        results: searchResult.rows.unique {
                            CloudantQueryResult.ResultObject a, CloudantQueryResult.ResultObject b -> a.id<=>b.id
                        }.collect { CloudantQueryResult.ResultObject result ->
                            return result.doc
                        },
                        bookmark: searchResult.bookmark
                )
            }

            return new CloudantSearchResult<T>(
                    results: []
            )
        }
    }

    protected Promise<CloudantQueryResult> search(String searchName, String queryString, Integer limit, String bookmark,
                                                  boolean includeDocs) {
        return getEffective().search(getDbUri(null), entityClass, searchName, queryString, limit, bookmark, includeDocs)
    }

    //#region marshall/unmarshall
    public String marshall(Object object) throws JsonProcessingException {
        return marshaller.marshall(object)
    }

    public <T> T unmarshall(String string, Class<T> clazz) throws IOException {
        return marshaller.unmarshall(string, clazz)
    }

    public <T> T unmarshall(String string, Class<T> parametrized, Class<?>... parameterClass) throws IOException {
        return marshaller.unmarshall(string, parametrized, parameterClass)
    }
    //#endregion

    //#region unique methods
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
    //#endregion
}
