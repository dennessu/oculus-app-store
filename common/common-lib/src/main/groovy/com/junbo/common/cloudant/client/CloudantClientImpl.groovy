package com.junbo.common.cloudant.client
import com.fasterxml.jackson.annotation.JsonInclude
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantResponse
import com.junbo.common.util.Utils
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

/**
 * CloudantClientImpl.
 */
@CompileStatic
class CloudantClientImpl implements CloudantClient {
    private static final String VIEW_PATH   = '/_design/views/_view/'
    private static final String SEARCH_PATH = '/_design/views/_search/'
    private static final String DEFAULT_DESIGN_ID_PREFIX = '_design/'
    private static final String HIGH_KEY_POSTFIX = '\ufff0'

    private static CloudantClientImpl singleton = new CloudantClientImpl()

    public static CloudantClientImpl instance() {
        return singleton
    }

    private static JunboAsyncHttpClient asyncHttpClient = JunboAsyncHttpClient.instance()
    private static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        return executeRequest(dbUri, HttpMethod.POST, '', [:], entity).then({ Response response ->
            if (response.statusCode != HttpStatus.CREATED.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

                if (response.statusCode == HttpStatus.CONFLICT.value()) {
                    throw new CloudantUpdateConflictException(
                            "Failed to save object to CloudantDB, error: $cloudantError.error," +
                                    " reason: $cloudantError.reason")
                }

                throw new CloudantException("Failed to save object to CloudantDB, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            def cloudantResponse = marshaller.unmarshall(response.responseBody, CloudantResponse)

            Assert.isTrue(cloudantResponse.ok)
            entity.cloudantId = cloudantResponse.id
            entity.cloudantRev = cloudantResponse.revision

            return Promise.pure(entity)
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        return executeRequest(dbUri, HttpMethod.GET, id, [:], null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                return Promise.pure(null)
            }

            if (response.responseBody == null) {
                return Promise.pure(null)
            }

            return Promise.pure(marshaller.unmarshall(response.responseBody, entityClass))
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        return executeRequest(dbUri, HttpMethod.PUT, entity.cloudantId, [:], entity).then({ Response response ->
            if (response.statusCode != HttpStatus.CREATED.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

                if (response.statusCode == HttpStatus.CONFLICT.value()) {
                    throw new CloudantUpdateConflictException(
                            "Failed to update object to Cloudant, error: $cloudantError.error," +
                                    " reason: $cloudantError.reason")
                }

                throw new CloudantException("Failed to update object to Cloudant, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            def cloudantResponse = marshaller.unmarshall(response.responseBody, CloudantResponse)

            Assert.isTrue(cloudantResponse.ok)
            Assert.isTrue(entity.cloudantId.equals(cloudantResponse.id))

            entity.cloudantRev = cloudantResponse.revision

            return Promise.pure(entity)
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity) {
        if (entity != null) {
            // force update cloudantId
            entity.setCloudantId(entity.getId().toString())
            return executeRequest(dbUri, HttpMethod.DELETE, entity.cloudantId, ['rev': entity.cloudantRev], null).then({ Response response ->
                if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
                    CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                    throw new CloudantException("Failed to delete object from Cloudant, error: $cloudantError.error," +
                            " reason: $cloudantError.reason")
                }
                return Promise.pure(null);
            })
        }
        return Promise.pure(null);
    }

    @Override
    def <T extends CloudantEntity> Promise<List<T>> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending) {
        def query = [:]

        if (limit != null) {
            query.put('limit', limit.toString())
        }
        if (skip != null) {
            query.put('skip', skip.toString())
        }
        if (descending) {
            query.put('descending', 'true')
        }
        query.put('include_docs', 'true')

        return executeRequest(dbUri, HttpMethod.GET, '_all_docs', query, null).then ({ Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

                throw new CloudantException("Failed to execute get all operation, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            def cloudantSearchResult = (CloudantQueryResult)marshaller.unmarshall(response.responseBody, CloudantQueryResult, CloudantQueryResult.AllResultEntity, entityClass)

            List<T> list = new ArrayList<>()
            return Promise.each(cloudantSearchResult.rows) { CloudantQueryResult.ResultObject result ->
                if (result.id.startsWith(DEFAULT_DESIGN_ID_PREFIX)) {
                    return Promise.pure(null)
                }
                list.add((T)(result.doc))
                return Promise.pure(null)
            }.then {
                return Promise.pure(list)
            }
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName,
                                                                          Object[] startKey, Object[] endKey, boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        def query = [:]
        if (limit != null) {
            query.put('limit', limit.toString())
        }
        if ((startKey != null && !descending) || (endKey != null && descending)) {
            query.put('startkey', descending ? buildEndKey(endKey, withHighKey) : buildStartKey(startKey))
        }
        if ((endKey != null && !descending) || (startKey != null && descending)) {
            query.put('endkey', descending ? buildStartKey(startKey) : buildEndKey(endKey, withHighKey))
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

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String key,
                                                                          Integer limit, Integer skip, boolean descending, boolean includeDocs) {
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
        if (includeDocs) {
            query.put('include_docs', includeDocs.toString())
        }

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String startKey,
                                                                          String endKey, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
        def query = [:]
        if ((startKey != null && !descending) || (endKey != null && descending)) {
            query.put('startkey', descending ? "\"$endKey\"" : "\"$startKey\"")
        }
        if ((endKey != null && !descending) || (startKey != null && descending)) {
            query.put('endkey', descending ? "\"$startKey\"" : "\"$endKey\"")
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

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName, String queryString, Integer limit, String bookmark, boolean includeDocs) {
        def searchRequest = new SearchRequest(
                query: queryString,
                limit: limit,
                bookmark: bookmark,
                include_docs: includeDocs
        )

        return executeRequest(dbUri, HttpMethod.POST, Utils.combineUrl(SEARCH_PATH, searchName), [:], searchRequest).then({ Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    Promise<Response> executeRequest(CloudantDbUri dbUri, HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        UriBuilder uriBuilder = UriBuilder.fromUri(dbUri.cloudantUri.value)
        if (dbUri.fullDbName != null) {
            uriBuilder.path(dbUri.fullDbName)
        }
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        requestBuilder.setBodyEncoding("UTF-8");

        if (!StringUtils.isEmpty(dbUri.cloudantUri.username)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(dbUri.cloudantUri.username).setPassword(dbUri.cloudantUri.password)
                    .setUsePreemptiveAuth(true).setScheme(Realm.AuthScheme.BASIC).build();
            requestBuilder.setRealm(realm);
        }

        if (body != null) {
            def payload = marshall(body)
            requestBuilder.setBody(payload)
        }

        queryParams?.each { String key, String value ->
            requestBuilder.addQueryParameter(key, value)
        }

        if (method == HttpMethod.PUT || method == HttpMethod.POST) {
            requestBuilder.addHeader('Content-Type', 'application/json')
        }

        try {
            return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable e ->
                throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
            }
        } catch (IOException e) {
            throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
        }
    }

    private static String buildStartKey(Object[] keys) {
        if (keys == null) {
            return null
        }

        Object obj = keys.find { Object key ->
            return key == null
        }

        if (obj != null) {
            throw new IllegalStateException('startKey cannot contain null value')
        }

        def result = []

        keys.each { Object key ->
            if (key instanceof String) {
                result.add('\"' + key + '\"')
            } else {
                result.add(key.toString())
            }
        }

        return '[' + result.join(',') + ']'
    }

    private static String buildEndKey(Object[] keys, boolean withHighKey) {
        if (keys == null) {
            return null
        }

        Object obj = keys.find { Object key ->
            return key == null
        }

        if (obj != null) {
            throw new IllegalStateException('endKey cannot contain null value')
        }

        def result = []

        keys.each { Object key ->
            if (key instanceof String) {
                result.add('\"' + key + '\"')
            } else {
                result.add(key.toString())
            }
        }

        if (withHighKey) {
            result.add('\"' + HIGH_KEY_POSTFIX + '\"')
        }

        return '[' + result.join(',') + ']'
    }

    private static JunboAsyncHttpClient.BoundRequestBuilder getRequestBuilder(HttpMethod method, String uri) {
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

    private static String marshall(Object obj) {
        return marshaller.marshall(obj)
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class SearchRequest {
        String query
        Integer limit
        String bookmark
        Boolean include_docs
    }
}
