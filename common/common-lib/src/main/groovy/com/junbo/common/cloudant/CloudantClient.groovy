package com.junbo.common.cloudant

import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantResponse
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.util.JsonMarshaller
import com.junbo.common.util.Utils
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Response
import junit.framework.Assert
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import javax.ws.rs.core.UriBuilder
import java.lang.reflect.ParameterizedType

/**
 * CloudantClient.
 */
abstract class CloudantClient<T> implements  InitializingBean {
    protected static final String VIEW_PATH = '/_design/views/_view/'
    protected final Class<T> entityClass
    protected AsyncHttpClient asyncHttpClient
    protected String cloudantDBUri
    protected String dbName

    abstract protected CloudantViews getCloudantViews()

    @Required
    void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Required
    void setCloudantDBUri(String cloudantDBUri) {
        this.cloudantDBUri = cloudantDBUri
    }

    @Required
    void setDbName(String dbName) {
        this.dbName = dbName
    }

    protected CloudantClient() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
        def id = Utils.tryObtainGetterMethod(entityClass, 'id')
        def cloudantId = Utils.tryObtainGetterMethod(entityClass, '_id')
        def cloudantRev = Utils.tryObtainGetterMethod(entityClass, '_rev')

        def resourceAge = Utils.tryObtainGetterMethod(entityClass, 'resourceAge')
        def createdTime = Utils.tryObtainGetterMethod(entityClass, 'createdTime')
        def updatedTime = Utils.tryObtainGetterMethod(entityClass, 'updatedTime')
        def createdBy = Utils.tryObtainGetterMethod(entityClass, 'createdBy')
        def updatedBy = Utils.tryObtainGetterMethod(entityClass, 'updatedBy')

        if (id == null || cloudantId == null || cloudantRev == null || resourceAge == null || createdTime == null
        || updatedTime == null || createdBy == null || updatedBy == null) {
            throw new CloudantException("Failed to init cloudant client with entityClass: $entityClass, " +
           'some of properties[id, _id, _rev, resourceAge, createdTime, updatedTime, createdBy, updatedby] not found')
        }
    }

    T cloudantPost(T entity) {
        entity._id = entity.id.toString()
        entity._rev = ''
        entity.createdTime = new Date()
        entity.createdBy = 'todo-cloudant'
        entity.resourceAge = '0'

        def response = executeRequest(HttpMethod.POST, '', [:], entity, true)
        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new CloudantUpdateConflictException(
                        "Failed to save object to CloudantDB, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            throw new CloudantException("Failed to save object to CloudantDB, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantResponse = JsonMarshaller.unmarshall(response.responseBody, CloudantResponse)

        Assert.assertTrue(cloudantResponse.ok)

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

        return (T) NoAnnotationsJsonMarshaller.unmarshall(response.responseBody, entityClass)
    }

    T cloudantPut(T entity) {
        def cloudantDoc = getCloudantDocument(entity.id.toString())
        entity._id = entity.id.toString()
        entity._rev = cloudantDoc._rev
        entity.updatedTime = new Date()
        entity.updatedBy = 'todo-cloudant'
        def originalResourceAge = entity.resourceAge
        entity.resourceAge = ((String)entity._rev).split('-')[0]

        def response = executeRequest(HttpMethod.PUT, entity.id.toString(), [:], entity, true)
        entity.resourceAge = originalResourceAge

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new CloudantUpdateConflictException(
                        "Failed to update object to Cloudant, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            throw new CloudantException("Failed to update object to Cloudant, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantResponse = JsonMarshaller.unmarshall(response.responseBody, CloudantResponse)

        Assert.assertTrue(cloudantResponse.ok)

        return entity
    }

    void cloudantDelete(Long id) {
        def cloudantDoc = getCloudantDocument(id.toString())
        def response = executeRequest(HttpMethod.DELETE, id.toString(), ['rev': cloudantDoc._rev], null)

        if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to delete object from Cloudant, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }
    }

    List<T> cloudantGetAll() {
        def response = executeRequest(HttpMethod.GET, '_all_docs', [:], null)
        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)

            throw new CloudantException("Failed to execute get all operation, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        def cloudantSearchResult = (CloudantSearchResult) JsonMarshaller.unmarshall(response.responseBody,
                CloudantSearchResult, CloudantSearchResult.AllResultEntity)

        return cloudantSearchResult.rows.collect { CloudantSearchResult.ResultObject result ->
            return cloudantGet(result.id)
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def response = executeRequest(HttpMethod.GET, '', [:], null)
        if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
            response = executeRequest(HttpMethod.PUT, '', [:], null)

            if (response.statusCode != HttpStatus.CREATED.value()) {
                CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody,
                        CloudantError)
                throw new CloudantException("Failed to create the database, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }
        }

        if (cloudantViews != null) {
            response = executeRequest(HttpMethod.GET, '_design/views', [:], cloudantViews)

            if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
                putViews(cloudantViews)
            } else if (response.statusCode == HttpStatus.OK.value()) {
                CloudantViews existingViews = JsonMarshaller.unmarshall(response.responseBody,
                        CloudantViews)
                def newView = cloudantViews.views.keySet().find { !existingViews.views.containsKey(it) }
                if (newView != null) {
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

        return (T) NoAnnotationsJsonMarshaller.unmarshall(response.responseBody, entityClass)
    }

    private void putViews(CloudantViews views) {
        def response = executeRequest(HttpMethod.PUT, '_design/views', [:], views)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to create the views in the database, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }
    }

    private CloudantSearchResult internalQueryView(String viewName, String key, Integer limit,
                                                   Integer skip, boolean descending) {
        CloudantViews.CloudantView cloudantView = cloudantViews.views[viewName]
        if (cloudantView == null) {
            throw new CloudantException("The view $viewName does not exist")
        }

        def query = [:]
        if (key != null) {
            query.put('key', "\"$key\"")
        }
        if (limit != null) {
            query.put('limit', limit)
        }
        if (skip != null) {
            query.put('skip', skip)
        }
        if (descending) {
            query.put('descending', 'true')
        }

        def response = executeRequest(HttpMethod.GET, VIEW_PATH + viewName, query, null)

        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = JsonMarshaller.unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        return (CloudantSearchResult) JsonMarshaller.unmarshall(response.responseBody,
                CloudantSearchResult, cloudantView.resultClass)
    }

    protected List<T> queryView(String viewName, String key, Integer limit, Integer skip, boolean descending) {
        CloudantSearchResult searchResult = internalQueryView(viewName, key, limit, skip, descending)
        if (searchResult.rows != null) {
            return searchResult.rows.collect { CloudantSearchResult.ResultObject result ->
                return cloudantGet(result.id)
            }
        }

        return []
    }

    protected List<T> queryView(String viewName, String key) {
        return queryView(viewName, key, null, null, false)
    }

    protected Response executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        return executeRequest(method, path, queryParams, body, false)
    }

    protected Response executeRequest(HttpMethod method, String path, Map<String, String> queryParams,
                                      Object body, boolean useNoAnnotationJackson) {
        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantDBUri)
        uriBuilder.path(dbName)
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        if (body != null && useNoAnnotationJackson) {
            def payload = NoAnnotationsJsonMarshaller.marshall(body)
            if (method == HttpMethod.POST) {
                // [hack] "_rev" need to be absent for cloudant post. JsonIgnore annotation not enabled when
                // using NoAnnotaionJsonMarshaller
                payload = payload.replaceFirst('\"_rev\"', '\"none\"')
            }
            requestBuilder.setBody(payload)
        }
        else if (body != null) {
            requestBuilder.setBody(JsonMarshaller.marshall(body))
        }

        queryParams.each { String key, String value ->
            requestBuilder.addQueryParameter(key, value)
        }

        if (method == HttpMethod.PUT || method == HttpMethod.POST) {
            requestBuilder.addHeader('Content-Type', 'application/json')
        }

        try {
            return requestBuilder.execute().get()
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
}
