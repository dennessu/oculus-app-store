package com.junbo.common.cloudant

import com.fasterxml.jackson.core.JsonProcessingException
import com.junbo.common.cloudant.client.CloudantClientBulk
import com.junbo.common.cloudant.client.CloudantClientImpl
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required

import java.lang.reflect.ParameterizedType

/**
 * CloudantClient.
 */
@CompileStatic
abstract class CloudantClientBase<T extends CloudantEntity> implements InitializingBean {

    private static final ThreadLocal<Boolean> useBulk = new ThreadLocal<>()

    // Set whether use bulk cloudant to commit changes. The value is reset at the end of the request automatically.
    public static boolean setUseBulk(Boolean value) {
        useBulk.set(value)
    }

    private com.junbo.common.cloudant.client.CloudantClient getEffective() {
        Boolean flag = useBulk.get()
        if (flag == null || flag == false) {
            return impl
        }
        return bulk
    }

    private CloudantClientImpl<T> impl = new CloudantClientImpl<>()
    private CloudantClientBulk<T> bulk = new CloudantClientBulk<>(impl)

    @Required
    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
        impl.asyncHttpClient = asyncHttpClient
    }

    @Required
    void setMarshaller(CloudantMarshaller marshaller) {
        impl.marshaller = marshaller
    }

    @Required
    void setCloudantUser(String cloudantUser) {
        impl.cloudantUser = cloudantUser
    }

    @Required
    void setCloudantPassword(String cloudantPassword) {
        impl.cloudantPassword = cloudantPassword
    }

    @Required
    void setCloudantDBUri(String cloudantDBUri) {
        impl.cloudantDBUri = cloudantDBUri
    }

    void setDbNamePrefix(String dbNamePrefix) {
        impl.dbNamePrefix = dbNamePrefix
    }

    @Required
    void setDbName(String dbName) {
        impl.dbName = dbName
    }

    protected CloudantClientBase() {
        impl.entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
    }

    @Override
    void afterPropertiesSet() throws Exception {
        impl.afterPropertiesSet();
    }

    @Override
    Promise<T> cloudantPost(T entity) {
        return getEffective().cloudantPost(entity)
    }

    @Override
    Promise<T> cloudantGet(String id) {
        return getEffective().cloudantGet(id)
    }

    @Override
    Promise<T> cloudantPut(T entity) {
        return getEffective().cloudantPut(entity)
    }

    @Override
    Promise<Void> cloudantDelete(String id) {
        return getEffective().cloudantDelete(id)
    }

    @Override
    Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending) {
        return getEffective().cloudantGetAll(limit, skip, descending)
    }

    Promise<CloudantQueryResult> queryView(String viewName, String key, Integer limit, Integer skip,
                                           boolean descending, boolean includeDocs) {
        return getEffective().queryView(viewName, key, null, null, limit, skip, descending, includeDocs)
    }

    Promise<List<T>> queryView(String viewName, String key, Integer limit, Integer skip,
                               boolean descending) {
        return getEffective().queryView(viewName, key, null, null, limit, skip, descending, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) result.doc
                }
            }

            return []
        }
    }

    Promise<List<T>> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    Promise<CloudantQueryResult> queryView(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, null, null, false, includeDocs)
    }

    Promise<List<T>> queryView(String viewName, String startKey, String endKey, Integer limit, Integer skip,
                               boolean descending) {
        return getEffective().queryView(viewName, null, startKey, endKey, limit, skip, descending, true).syncThen { CloudantQueryResult searchResult ->
            if (searchResult.rows != null) {
                return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                    return (T) (result.doc)
                }
            }

            return []
        }
    }

    Promise<CloudantSearchResult<T>> search(String searchName, String queryString, Integer limit, String bookmark) {
        return getEffective().search(searchName, queryString, limit, bookmark, true).syncThen { CloudantQueryResult searchResult ->
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
        return getEffective().search(searchName, queryString, limit, bookmark, includeDocs)
    }

    String marshall(Object object) throws JsonProcessingException {
        return impl.marshaller.marshall(object)
    }

    public <T> T unmarshall(String string, Class<T> clazz) throws IOException {
        return impl.marshaller.unmarshall(string, clazz)
    }

    public <T> T unmarshall(String string, Class<?> parametrized, Class<?>... parameterClass) throws IOException {
        return impl.marshaller.unmarshall(string, parametrized, parameterClass)
    }
}
