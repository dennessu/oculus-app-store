package com.junbo.common.cloudant
import com.fasterxml.jackson.core.JsonProcessingException
import com.junbo.common.cloudant.client.*
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.common.cloudant.model.CloudantUniqueItem
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.Results
import com.junbo.common.shuffle.Oculus48Id
import com.junbo.common.util.Utils
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import java.lang.reflect.ParameterizedType

/**
 * CloudantClientBase.
 */
@CompileStatic
abstract class CloudantClientBase<T extends CloudantEntity> implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudantClientBase)
    private static final ThreadLocal<Boolean> useBulk = new ThreadLocal<>()
    private static final ThreadLocal<Boolean> ignoreLocalIgnoreBulk = new ThreadLocal<>()

    // Set whether use bulk cloudant to commit changes. The value is reset at the end of the request automatically.
    public static boolean setUseBulk(Boolean value) {
        useBulk.set(value)
    }

    // Set use bulk ignoring the localIgnoreBulk.
    public static boolean setStrongUseBulk(Boolean value) {
        useBulk.set(value)
        ignoreLocalIgnoreBulk.set(value)
    }

    public CloudantClientInternal getEffective() {
        // There are two layer of overriding:
        // 1. Use per instance setting to specify whether cache is used or not.
        // 2. Use the thread static setUseBulk to specify whether bulk is used or not.
        // When bulk is used, it will ignore cache setting. Now bulk is only used in migration.
        Boolean flag = useBulk.get()
        Boolean ignoreLocalIgnoreBulk = ignoreLocalIgnoreBulk.get() ?: false;
        if (flag == null || flag == false || (localIgnoreBulk && !ignoreLocalIgnoreBulk)) {
            return internal
        }
        return bulk
    }

    protected static String dbNamePrefix = ConfigServiceManager.instance().getConfigValue("common.cloudant.dbNamePrefix")
    protected static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()
    protected static CloudantUniqueClient cloudantUniqueClient = CloudantUniqueClient.instance()
    protected static CloudantClientBulk bulk = CloudantClientBulk.instance()

    private static Integer defaultLimit = ConfigServiceManager.instance().getConfigValueAsInt("common.cloudant.defaultLimit", 1000)
    private static Integer maxLimit = ConfigServiceManager.instance().getConfigValueAsInt("common.cloudant.maxLimit", 1000)

    private CloudantClientInternal internal

    protected CloudantGlobalUri cloudantGlobalUri
    protected CloudantDbUri cloudantDbUri
    protected Class<T> entityClass
    protected String dbName

    private boolean enableCache
    private boolean includeDocs
    private boolean localIgnoreBulk
    private boolean noOverrideWrites

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

    void setNoOverrideWrites(boolean noOverrideWrites) {
        this.noOverrideWrites = noOverrideWrites
    }

    boolean getNoOverrideWrites() {
        return noOverrideWrites
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
                return getEffective().cloudantPost(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites)
            }
        }
        return getEffective().cloudantPost(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites)
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
                return getEffective().cloudantPut(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites)
            }
        }
        return getEffective().cloudantPut(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites)
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
            return getEffective().cloudantDelete(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites).then {
                return deleteUnique(entity)
            }
        } else {
            return getEffective().cloudantDelete(getDbUri(entity.cloudantId), entityClass, entity, noOverrideWrites)
        }
    }

    public Promise<Results<T>> cloudantGetAll(CloudantViewQueryOptions options) {
        def dbUri = getDbUri(null)
        options.limit = filterLimit(dbUri, options.limit)

        CloudantViewQueryOptions internalOptions = new CloudantViewQueryOptions(options);
        internalOptions.includeDocs = this.includeDocs
        internalOptions.limit = options.limit + 1       // include 1 extra element to know next startkey
        if (options.cursor != null) {
            if (options.startKey != null) {
                LOGGER.warn("cloudantGetAll() can't be called with both startkey {} and cursor {}", options.startKey, options.cursor)
            }
            internalOptions.startKey = decodeGetAllCursor(options.cursor)
        }

        def future = getEffective().cloudantGetAll(dbUri, entityClass, internalOptions)
        if (!this.includeDocs && options.includeDocs) {
            future = future.then { CloudantQueryResult result ->
                return fetchDocs(result, options.limit)
            }
        }
        Results<T> results = new Results<>()
        results.setUsingNextCursor(true)
        return future.syncThen { CloudantQueryResult result ->
            results.setTotal(result.totalRows)
            if (!CollectionUtils.isEmpty(result.rows)) {
                int limit
                results.items = new ArrayList<>()
                if (result.rows.size() > options.limit) {
                    // has next page
                    results.setNextCursor(encodeGetAllCursor(result.rows.last().id))
                    limit = options.limit
                } else {
                    // no next page
                    results.setNextCursor(null)
                    limit = result.rows.size()
                }
                for (int i = 0; i < limit; ++i) {
                    results.items.add((T)result.rows.get(i).doc)
                }
            }
            return results
        }
    }

    public Promise<Integer> queryViewTotal(String viewName, String key) {
        def dbUri = getDbUri(null)
        return getEffective().queryViewTotal(dbUri, viewName, new CloudantViewQueryOptions(
                startKey: key,
                endKey: key,
                limit: filterLimit(dbUri, null)
        ))
    }

    public Promise<Integer> queryViewTotal(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, boolean descending) {
        def dbUri = getDbUri(null)
        return getEffective().queryViewTotal(dbUri, viewName, new CloudantViewQueryOptions(
                startKey: startKey,
                endKey: encodeHighKey(endKey, withHighKey),
                descending: descending,
                limit: filterLimit(dbUri, null)));
    }

    public Promise<CloudantQueryResult> queryView(String viewName, String key, Integer limit, Integer skip,
                                           boolean descending, boolean includeDocs) {
        def dbUri = getDbUri(null)
        if (includeDocs && !this.includeDocs) {
            // pass false to implementation method and try to fetch results one by one.
            // this means to allow higher cache hit rate
            return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                    startKey: key,
                    endKey: key,
                    limit: filterLimit(dbUri, limit),
                    skip: skip,
                    descending: descending,
                    includeDocs: false)
            ).then { CloudantQueryResult result ->
                return fetchDocs(result, limit)
            }
        }
        return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                startKey: key,
                endKey: key,
                limit: filterLimit(dbUri, limit),
                skip: skip,
                descending: descending,
                includeDocs: includeDocs))
    }

    public Promise<List<T>> queryView(String viewName, String key, Integer limit, Integer skip,
                               boolean descending) {
        def dbUri = getDbUri(null)
        return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                startKey: key,
                endKey: key,
                limit: filterLimit(dbUri, limit),
                skip: skip,
                descending: descending,
                includeDocs: true)).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) result.doc
                }
            }
            return []
        }
    }

    public Promise<Results<T>> queryViewResults(String viewName, String key, Integer limit, Integer skip, boolean descending) {
        Results<T> results = new Results<>()
        return queryView(viewName, key, limit, skip, descending).then { List<T> list ->
            results.items = list

            return queryViewTotal(viewName, key).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    public Promise<List<T>> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    public Promise<Results<T>> queryViewResults(String viewName, String key) {
        return queryViewResults(viewName, key, null, null, false)
    }

    public Promise<CloudantQueryResult> queryView(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, null, null, false, includeDocs)
    }

    public Promise<List<T>> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey,
                                         Integer limit, Integer skip, boolean descending) {
        def dbUri = getDbUri(null)
        return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                startKey: startKey,
                endKey: encodeHighKey(endKey, withHighKey),
                limit: filterLimit(dbUri, limit),
                skip: skip,
                descending: descending,
                includeDocs: true)).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) (result.doc)
                }
            }

            return []
        }
    }

    public Promise<Results<T>> queryViewResults(String viewName, Object[] startKey, Object[] endKey,
                                                boolean withHighKey, Integer limit, Integer skip, boolean descending) {
        Results<T> results = new Results<>()
        return queryView(viewName, startKey, endKey, withHighKey, limit, skip, descending).then { List<T> list ->
            results.items = list

            return queryViewTotal(viewName, startKey, endKey, withHighKey, descending).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    public Promise<List<T>> queryView(String viewName, String startKey, String endKey, Integer limit, Integer skip,
                               boolean descending) {
        def dbUri = getDbUri(null)
        return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                startKey: startKey,
                endKey: endKey,
                limit: filterLimit(dbUri, limit),
                skip: skip,
                descending: descending,
                includeDocs: true)).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) (result.doc)
                }
            }

            return []
        }
    }

    public Promise<CloudantQueryResult> queryView(String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        def dbUri = getDbUri(null)
        if (includeDocs && !this.includeDocs) {
            // pass false to implementation method and try to fetch results one by one.
            // this means to allow higher cache hit rate
            return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                    startKey: startKey,
                    endKey: encodeHighKey(endKey, withHighKey),
                    limit: filterLimit(dbUri, limit),
                    skip: skip,
                    descending: descending,
                    includeDocs: false)).then { CloudantQueryResult result ->
                return fetchDocs(result, limit)
            }
        }
        return getEffective().queryView(dbUri, entityClass, viewName, new CloudantViewQueryOptions(
                startKey: startKey,
                endKey: encodeHighKey(endKey, withHighKey),
                limit: filterLimit(dbUri, limit),
                skip: skip,
                descending: descending,
                includeDocs: includeDocs))
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
                return fetchDocs(result, limit)
            }
        }
        return getEffective().search(getDbUri(null), entityClass, searchName, queryString, null, limit, bookmark, includeDocs)
    }

    private Promise<CloudantQueryResult> fetchDocs(CloudantQueryResult result, int limit) {
        if (result.rows != null) {
            int i = 0;
            return Promise.each(result.rows) { CloudantQueryResult.ResultObject row ->
                if (i++ < limit) {
                    return cloudantGet(row.id).then { Object doc ->
                        row.doc = doc;
                        return Promise.pure()
                    }
                } else {
                    return Promise.pure()
                }
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

    public Results<T> cloudantGetAllSync(CloudantViewQueryOptions options) {
        return cloudantGetAll(options).get()
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

    private static Object[] encodeHighKey(Object[] endKey, boolean withHighKey) {
        if (!withHighKey) {
            return endKey
        }
        Object[] newEndKey = new Object[endKey.size() + 1]
        for (int i = 0; i < endKey.size(); ++i) {
            newEndKey[i] = endKey[i];
        }
        newEndKey[endKey.size()] = CloudantViewQueryOptions.HIGH_KEY
        return newEndKey
    }

    private static int filterLimit(CloudantDbUri dbUri, Integer limit) {
        if (limit == null) {
            return (int)defaultLimit
        } else {
            if (limit > maxLimit) {
                LOGGER.warn("query $dbUri limit exceeded ${maxLimit}, limited to ${maxLimit}")
                limit = maxLimit
            }
            return limit;
        }
    }

    private static final String GETALL_CURSOR_RAW_ID = "0";
    private static final String GETALL_CURSOR_O48_ID = "1";

    protected static String encodeGetAllCursor(String id) {
        if (id == null) return null;
        if (id.length() > 15) {
            // > 48 bits. treat as cloudant generated id
            return Utils.encodeBase64(GETALL_CURSOR_RAW_ID + id);
        } else {
            // simply treat as Oculus48Id if it is pure numeric
            if (StringUtils.isNumeric(id)) {
                try {
                    Long value = Long.parseLong(id)
                    return Utils.encodeBase64(GETALL_CURSOR_O48_ID + Oculus48Id.encode(value));
                } catch (NumberFormatException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                return Utils.encodeBase64(GETALL_CURSOR_RAW_ID + id);;
            }
        }
    }

    protected static String decodeGetAllCursor(String id) {
        if (id == null || id.length() == 0) return null;
        try {
            id = Utils.decodeBase64(id);
        } catch (Exception ex) {
            LOGGER.error("Invalid get all cursor: " + id, ex);
            throw AppCommonErrors.INSTANCE.fieldInvalid("cursor").exception();
        }
        if (id.startsWith(GETALL_CURSOR_RAW_ID)) {
            return id.substring(GETALL_CURSOR_RAW_ID.length());
        } else if (id.startsWith(GETALL_CURSOR_O48_ID)) {
            return Oculus48Id.decode(id.substring(GETALL_CURSOR_O48_ID.length()));
        } else {
            throw AppCommonErrors.INSTANCE.fieldInvalid("cursor").exception();
        }
    }
}
