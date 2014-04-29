package com.junbo.common.cloudant
import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
import com.junbo.common.cloudant.model.*
import com.junbo.common.util.Identifiable
import com.junbo.common.util.ResourceAge
import com.junbo.common.util.Utils
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.Assert

import javax.ws.rs.core.UriBuilder
import java.lang.reflect.ParameterizedType
/**
 * CloudantClient.
 */
@CompileStatic
abstract class CloudantClient<T extends CloudantEntity> implements InitializingBean {
    protected static final String VIEW_PATH = '/_design/views/_view/'

    protected final Class<T> entityClass
    protected AsyncHttpClient asyncHttpClient

    protected CloudantMarshaller marshaller

    protected String cloudantDBUri
    protected String dbName
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
    void setCloudantDBUri(String cloudantDBUri) {
        this.cloudantDBUri = cloudantDBUri
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
        entity.cloudantId = ((Identifiable)entity).id.toString()
        entity.cloudantRev = null
        if (entity.resourceAge == null) {
            entity.resourceAge = ResourceAge.initial()
        }
        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.createdBy = 'created-by-todo'
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

        return (T)unmarshall(response.responseBody, entityClass)
    }

    T cloudantPut(T entity) {
        def cloudantDoc = getCloudantDocument(((Identifiable)entity).id.toString())
        entity.cloudantId = ((Identifiable)entity).id.toString()
        entity.cloudantRev = cloudantDoc.cloudantRev

        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.updatedBy = 'updated-by-todo'
        entity.updatedTime = new Date()

        // assume resourceAge is increased by external caller
        if (this == null && !ResourceAge.isNewer(entity.resourceAge, cloudantDoc.resourceAge)) {
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

        def response = executeRequest(HttpMethod.PUT, ((Identifiable)entity).id.toString(), [:], entity)

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

        def cloudantSearchResult = unmarshall(response.responseBody, CloudantSearchResult, CloudantSearchResult.AllResultEntity)

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

        return (T)unmarshall(response.responseBody, entityClass)
    }

    private void putViews(CloudantViews views) {
        def response = executeRequest(HttpMethod.PUT, '_design/views', [:], views)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
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
            query.put('limit', limit.toString())
        }
        if (skip != null) {
            query.put('skip', skip.toString())
        }
        if (descending) {
            query.put('descending', 'true')
        }

        def response = executeRequest(HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null)

        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
            throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }

        return unmarshall(response.responseBody, CloudantSearchResult, cloudantView.resultClass)
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
        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantDBUri)
        uriBuilder.path(dbName)
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        if (body != null) {
            def payload = marshall(body)
            requestBuilder.setBody(payload)
        }
        else if (body != null) {
            requestBuilder.setBody(marshall(body))
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

    protected String marshall(Object obj) {
        if (obj == null || obj instanceof CloudantModel) {
            return CloudantModelMarshaller.marshall((CloudantModel)obj)
        } else if (obj instanceof CloudantEntity) {
            return marshaller.marshall((CloudantEntity)obj)
        } else {
            throw new CloudantException("Unknown class to marshall: $obj")
        }
    }

    protected <T> T unmarshall(String str, Class<T> cls) {
        if (CloudantEntity.isAssignableFrom(cls)) {
            return marshaller.unmarshall(str, cls)
        } else if (CloudantModel.isAssignableFrom(cls)) {
            return CloudantModelMarshaller.unmarshall(str, cls)
        } else {
            throw new CloudantException("Unknown class to unmarshall: $cls")
        }
    }

    protected <T> T unmarshall(String str, Class<T> cls, Class<?> parameterClass) {
        if (CloudantEntity.isAssignableFrom(cls)) {
            return marshaller.unmarshall(str, cls, parameterClass)
        } else if (CloudantModel.isAssignableFrom(cls)) {
            return CloudantModelMarshaller.unmarshall(str, cls, parameterClass)
        } else {
            throw new CloudantException("Unknown class to unmarshall: $cls")
        }
    }
}
