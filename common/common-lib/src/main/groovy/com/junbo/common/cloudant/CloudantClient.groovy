package com.junbo.common.cloudant

import com.fasterxml.jackson.annotation.JsonInclude
import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
import com.junbo.common.cloudant.model.*
import com.junbo.common.util.Identifiable
import com.junbo.common.util.Utils
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
import java.lang.reflect.ParameterizedType

/**
 * CloudantClient.
 */
@CompileStatic
abstract class CloudantClient<T extends CloudantEntity> implements InitializingBean {
    protected static final String VIEW_PATH = '/_design/views/_view/'
    protected static final String SEARCH_PATH = '/_design/views/_search/'

    protected final Class<T> entityClass
    protected AsyncHttpClient asyncHttpClient

    protected CloudantMarshaller marshaller

    protected String cloudantUser
    protected String cloudantPassword
    protected String cloudantDBUri
    protected String dbNamePrefix
    protected String dbName
    protected String fullDbName
    protected boolean silentPassResourceAgeCheck

    abstract protected CloudantViews getCloudantViews()

    @Required
    void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Required
    void setMarshaller(CloudantMarshaller marshaller) {
        this.marshaller = marshaller
    }

    @Required
    void setCloudantUser(String cloudantUser) {
        this.cloudantUser = cloudantUser
    }

    @Required
    void setCloudantPassword(String cloudantPassword) {
        this.cloudantPassword = cloudantPassword
    }

    @Required
    void setCloudantDBUri(String cloudantDBUri) {
        this.cloudantDBUri = Utils.filterPerDataCenterConfig(cloudantDBUri, "cloudantDBUri")
    }

    void setDbNamePrefix(String dbNamePrefix) {
        this.dbNamePrefix = dbNamePrefix
    }

    @Required
    void setDbName(String dbName) {
        this.dbName = dbName
    }

    void setSilentPassResourceAgeCheck(boolean silentPassResourceAgeCheck) {
        this.silentPassResourceAgeCheck = silentPassResourceAgeCheck
    }

    protected CloudantClient() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
        if (!Identifiable.isAssignableFrom(entityClass)) {
            throw new CloudantException(
                    "Failed to init cloudant client with entityClass: $entityClass. " +
                            "Entity class doesn't implement Identifiable<T>")
        }
    }

    T cloudantPost(T entity) {
        entity.cloudantId = ((Identifiable) entity).id.toString()
        entity.cloudantRev = null
        if (entity.resourceAge == null) {
            entity.resourceAge = 0
        }
        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.createdBy = 123L
        entity.createdTime = new Date()

        def response = executeRequest(HttpMethod.POST, '', [:], entity)
        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new CloudantUpdateConflictException(
                        "Failed to save object to CloudantDB, error: $cloudantError.error," +
                                " reason: $cloudantError.reason")
            }

            throw new CloudantException("Failed to save object to CloudantDB, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantResponse = unmarshall(response.responseBody, CloudantResponse)

        Assert.isTrue(cloudantResponse.ok)
        entity.cloudantRev = cloudantResponse.revision

        return entity
    }

    T cloudantGet(String id) {
        def response = executeRequest(HttpMethod.GET, id, [:], null)

        if (response.statusCode != HttpStatus.OK.value()) {
            return null
        }

        if (response.responseBody == null) {
            return null
        }

        return (T) unmarshall(response.responseBody, entityClass)
    }

    T cloudantPut(T entity) {
        def cloudantDoc = getCloudantDocument(((Identifiable) entity).id.toString())
        entity.cloudantId = ((Identifiable) entity).id.toString()
        entity.cloudantRev = cloudantDoc.cloudantRev

        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.createdBy = cloudantDoc.createdBy
        entity.createdTime = cloudantDoc.createdTime
        entity.updatedBy = 123L
        entity.updatedTime = new Date()

        // assume resourceAge is increased by external caller
        if (this == null && entity.resourceAge <= cloudantDoc.resourceAge) {
            // The target resource age is higher or equal than the resource age of the entity we are about to put.
            // This indicates a conflicting change or change already committed.
            // Raise conflict or silently ignore
            if (silentPassResourceAgeCheck) {
                // return latest cloudant doc
                return cloudantDoc
            }
            throw new CloudantUpdateConflictException(
                    "Failed to update object to Cloudant due to resourceAge conflict. " +
                            "id: $entity.cloudantId, resourceAge: $entity.resourceAge, dbResourceAge: $cloudantDoc.resourceAge")
        }

        def response = executeRequest(HttpMethod.PUT, ((Identifiable) entity).id.toString(), [:], entity)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new CloudantUpdateConflictException(
                        "Failed to update object to Cloudant, error: $cloudantError.error," +
                                " reason: $cloudantError.reason")
            }

            throw new CloudantException("Failed to update object to Cloudant, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantResponse = unmarshall(response.responseBody, CloudantResponse)

        Assert.isTrue(cloudantResponse.ok)
        entity.cloudantRev = cloudantResponse.revision

        return entity
    }

    void cloudantDelete(String id) {
        def cloudantDoc = getCloudantDocument(id)
        if (cloudantDoc != null) {
            def response = executeRequest(HttpMethod.DELETE, id.toString(), ['rev': cloudantDoc.cloudantRev], null)

            if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
                CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to delete object from Cloudant, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }
        }
    }

    List<T> cloudantGetAll() {
        def response = executeRequest(HttpMethod.GET, '_all_docs', [:], null)
        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)

            throw new CloudantException("Failed to execute get all operation, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantSearchResult =unmarshall(response.responseBody,
                CloudantQueryResult, CloudantQueryResult.AllResultEntity, Object.class)

        return cloudantSearchResult.rows.collect { CloudantQueryResult.ResultObject result ->
            return cloudantGet(result.id)
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {

        if (dbNamePrefix != null) {
            fullDbName = dbNamePrefix + dbName;
        } else {
            fullDbName = dbName;
        }

        def response = executeRequest(HttpMethod.GET, '', [:], null)
        if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
            response = executeRequest(HttpMethod.PUT, '', [:], null)

            if (response.statusCode != HttpStatus.CREATED.value()) {
                CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to create the database, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }
        }

        if (cloudantViews != null) {
            response = executeRequest(HttpMethod.GET, '_design/views', [:], cloudantViews)

            if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
                putViews(cloudantViews)
            } else if (response.statusCode == HttpStatus.OK.value()) {
                CloudantViews existingViews = unmarshall(response.responseBody, CloudantViews)
                def newView = cloudantViews.views.keySet().find { !existingViews.views.containsKey(it) }
                def newIndex
                if (cloudantViews.indexes != null && existingViews.indexes != null) {
                    newIndex = cloudantViews.indexes.keySet().find {
                        !existingViews.indexes.containsKey(it)
                    }
                }
                if (newView != null || newIndex != null) {
                    cloudantViews.revision = existingViews.revision
                    putViews(cloudantViews)
                }
            }
        }
    }

    private T getCloudantDocument(String id) {
        def response = executeRequest(HttpMethod.GET, id, [:], null)
        if (response.statusCode != HttpStatus.OK.value()) {
            return null
        }

        if (response.responseBody == null) {
            return null
        }

        return (T) unmarshall(response.responseBody, entityClass)
    }

    private void putViews(CloudantViews views) {
        def response = executeRequest(HttpMethod.PUT, '_design/views', [:], views)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to create the views in the database, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }
    }

    private CloudantQueryResult internalQueryView(String viewName, String key, String startKey, String endKey,
                                                  Integer limit, Integer skip, boolean descending,
                                                  boolean includeDocs) {
        CloudantViews.CloudantView cloudantView = cloudantViews.views[viewName]
        if (cloudantView == null) {
            throw new CloudantException("The view $viewName does not exist")
        }

        def query = [:]
        if (key != null) {
            query.put('key', "\"$key\"")
        }
        if (startKey != null) {
            query.put('startkey', "\"$startKey\"")
        }
        if (endKey != null) {
            query.put('endkey', "\"$endKey\"")
        }
        if (limit != null) {
            query.put('limit', limit.toString())
        }
        if (skip != null) {
            query.put('skip', skip.toString())
        }
        if (descending) {
            query.put('descending', 'true')
        }
        if (includeDocs) {
            query.put('include_docs', includeDocs.toString())
        }

        def response = executeRequest(HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null)

        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        return unmarshall(response.responseBody, CloudantQueryResult, cloudantView.resultClass, entityClass)
    }

    protected CloudantQueryResult queryView(String viewName, String key, Integer limit, Integer skip,
                                boolean descending, boolean includeDocs) {
        return internalQueryView(viewName, key, null, null, limit, skip, descending, includeDocs)
    }

    protected List<T> queryView(String viewName, String key, Integer limit, Integer skip,
                                boolean descending) {
        CloudantQueryResult searchResult = internalQueryView(viewName, key, null, null, limit, skip, descending, true)
        if (searchResult.rows != null) {
            return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                return (T)result.doc
            }
        }

        return []
    }

    protected List<T> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    protected CloudantQueryResult queryView(String viewName, String key, boolean includeDocs) {
        return queryView(viewName, key, null, null, false, includeDocs)
    }

    protected List<T> queryView(String viewName, String startKey, String endKey, Integer limit, Integer skip,
                                boolean descending, boolean includeDocs) {
        CloudantQueryResult searchResult =
                internalQueryView(viewName, null, startKey, endKey, limit, skip, descending, includeDocs)
        if (searchResult.rows != null) {
            return searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                return (T)(result.doc)
            }
        }

        return []
    }
    protected CloudantSearchResult<T> search(String searchName, String queryString, Integer limit, String bookmark) {
        CloudantQueryResult searchResult = internalSearch(searchName, queryString, limit, bookmark, true)
        if (searchResult.rows != null) {
            return new CloudantSearchResult<T>(
                    results: searchResult.rows.collect { CloudantQueryResult.ResultObject result ->
                        return result.doc
                    },
                    bookmark: searchResult.bookmark
            )
        }

        return new CloudantSearchResult<T>(
                results: []
        )
    }

    protected CloudantQueryResult search(String searchName, String queryString, Integer limit, String bookmark,
                                             boolean includeDocs) {
        return internalSearch(searchName, queryString, limit, bookmark, includeDocs)
    }

    private CloudantQueryResult internalSearch(String searchName, String queryString, Integer limit, String bookmark,
                                               boolean includeDocs) {
        CloudantViews.CloudantIndex cloudantView = cloudantViews.indexes[searchName]
        if (cloudantView == null) {
            throw new CloudantException("The index $searchName does not exist")
        }

        def searchRequest = new SearchRequest(
                query: queryString,
                limit: limit,
                bookmark: bookmark,
                include_docs: includeDocs
        )

        def response = executeRequest(HttpMethod.POST, Utils.combineUrl(SEARCH_PATH, searchName), [:], searchRequest)

        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        return unmarshall(response.responseBody, CloudantQueryResult, cloudantView.resultClass, entityClass)
    }

    protected Response executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantDBUri)
        uriBuilder.path(fullDbName)
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        if (!StringUtils.isEmpty(cloudantUser)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(cloudantUser).setPassword(cloudantPassword)
                    .setUsePreemptiveAuth(true).setScheme(Realm.AuthScheme.BASIC).build();
            requestBuilder.setRealm(realm);
        }

        if (body != null) {
            def payload = marshall(body)
            requestBuilder.setBody(payload)
        } else if (body != null) {
            requestBuilder.setBody(marshall(body))
        }

        queryParams.each { String key, String value ->
            requestBuilder.addQueryParameter(key, value)
        }

        if (method == HttpMethod.PUT || method == HttpMethod.POST) {
            requestBuilder.addHeader('Content-Type', 'application/json')
            requestBuilder.setBodyEncoding("UTF-8");
        }

        try {
            Response response = requestBuilder.execute().get()
            String responseBody = response.responseBody // always consume the response body.

            return response
        } catch (IOException e) {
            throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
        }
    }

    protected AsyncHttpClient.BoundRequestBuilder getRequestBuilder(HttpMethod method, String uri) {
        switch (method) {
            case HttpMethod.GET:
                return asyncHttpClient.prepareGet(uri)
            case HttpMethod.POST:
                return asyncHttpClient.preparePost(uri)
            case HttpMethod.PUT:
                return asyncHttpClient.preparePut(uri)
            case HttpMethod.DELETE:
                return asyncHttpClient.prepareDelete(uri)
            default:
                return asyncHttpClient.prepareGet(uri)
        }
    }

    protected String marshall(Object obj) {
        return marshaller.marshall(obj)
    }

    protected <T> T unmarshall(String str, Class<T> cls) {
        return marshaller.unmarshall(str, cls)
    }

    protected <T> T unmarshall(String str, Class<T> cls, Class<?>... parameterClass) {
        return marshaller.unmarshall(str, cls, parameterClass)
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class SearchRequest {
        String query
        Integer limit
        String bookmark
        Boolean include_docs
    }
}
