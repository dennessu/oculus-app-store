package com.junbo.common.cloudant.client
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.cloudant.model.CloudantReduceQueryResult
import com.junbo.common.cloudant.model.CloudantResponse
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.CloudantId
import com.junbo.common.util.Utils
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
/**
 * CloudantClientImpl.
 */
@CompileStatic
class CloudantClientImpl implements CloudantClientInternal {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientImpl.class)

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
        if (entity.getId() != null) {
            entity.setCloudantId(entity.getId().toString())
            CloudantId.validate(entity.cloudantId)
        }
        return executeRequest(dbUri, HttpMethod.POST, '', [:], entity).then({ Response response ->
            checkWriteErrors("create", dbUri, entity, response)

            def cloudantResponse = marshaller.unmarshall(response.responseBody, CloudantResponse)

            Assert.isTrue(cloudantResponse.ok)
            entity.cloudantId = cloudantResponse.id
            entity.cloudantRev = cloudantResponse.revision

            return Promise.pure(entity)
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        CloudantId.validate(id)
        return executeRequest(dbUri, HttpMethod.GET, urlEncode(id), [:], null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
                    return Promise.pure(null)
                }
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                logger.error("Failed to get object from CloudantDB. db: $dbUri, error: $cloudantError.error, reason: $cloudantError.reason")
                throw new CloudantException("Failed to get object from Cloudant, error: $cloudantError.error, reason: $cloudantError.reason")
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
        CloudantId.validate(entity.cloudantId)
        return executeRequest(dbUri, HttpMethod.PUT, urlEncode(entity.cloudantId), [:], entity).then({ Response response ->
            checkWriteErrors("update", dbUri, entity, response)

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
            CloudantId.validate(entity.cloudantId)
            return executeRequest(dbUri, HttpMethod.DELETE, urlEncode(entity.cloudantId), ['rev': entity.cloudantRev], null).then({ Response response ->
                checkWriteErrors("delete", dbUri, entity, response)
                return Promise.pure(null);
            })
        }
        return Promise.pure(null);
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending, boolean includeDocs) {
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
        if (includeDocs) {
            query.put('include_docs', includeDocs.toString())
        }

        return executeRequest(dbUri, HttpMethod.GET, '_all_docs', query, null).then { Response response ->
            checkViewErrors("get all", dbUri, "_all_docs", response)

            def result = (CloudantQueryResult)marshaller.unmarshall(response.responseBody, CloudantQueryResult, CloudantQueryResult.AllResultEntity, JsonNode)

            def list = new ArrayList<>()
            return Promise.each(result.rows) { CloudantQueryResult.ResultObject row ->
                if (row.id.startsWith(DEFAULT_DESIGN_ID_PREFIX)) {
                    return Promise.pure(null)
                }
                // tricky: effectively converting ResultObject<String, JsonNode> to ResultObject<String, T>
                if (includeDocs && row.doc != null) {
                    row.doc = marshaller.unmarshall(row.doc.toString(), entityClass)
                }
                list.add(row)
                return Promise.pure(null)
            }.then {
                result.rows = list
                if (result.totalRows != null) {
                    // exclude the _design row
                    result.totalRows = result.totalRows - 1
                }
                return Promise.pure(result)
            }
        }
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
            checkViewErrors("query view", dbUri, viewName, response)
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

        query.put('reduce', 'false')

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->
            checkViewErrors("query view", dbUri, viewName, response)
            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    @Override
    public Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String key, String viewName) {
        def query = [:]
        if (key != null) {
            query.put('key', "\"$key\"")
        }
        query.put('include_docs', 'false')
        query.put('reduce', 'true')

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then { Response response ->
            checkViewErrors("query view total", dbUri, viewName, response)

            CloudantReduceQueryResult result = marshaller.unmarshall(response.responseBody, CloudantReduceQueryResult)
            if (CollectionUtils.isEmpty(result.rows) || result.rows.size() != 1) {
                return Promise.pure(0)
            }

            return Promise.pure(result.rows.get(0).value)
        }
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
        query.put('reduce', 'false')

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->
            checkViewErrors("query view", dbUri, viewName, response)
            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName, String queryString, String sort, Integer limit, String bookmark, boolean includeDocs) {
        def searchRequest = new SearchRequest(
                query: queryString,
                sort: sort,
                limit: limit,
                bookmark: bookmark,
                include_docs: includeDocs
        )

        return executeRequest(dbUri, HttpMethod.POST, Utils.combineUrl(SEARCH_PATH, searchName), [:], searchRequest).then({ Response response ->
            checkViewErrors("search", dbUri, searchName, response)
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
            //requestBuilder.setHeader("X-Cloudant-User", dbUri.cloudantUri.username)
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
            return requestBuilder.execute().recover { Throwable e ->
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

    private static String urlEncode(String id) {
        return URLEncoder.encode(id, "UTF-8")
    }

    private static void checkViewErrors(String verb, CloudantDbUri dbUri, String viewName, Response response) {
        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.BAD_REQUEST.value()) {
                if (cloudantError?.reason == "Invalid bookmark parameter supplied") {
                    throw AppCommonErrors.INSTANCE.parameterInvalid("cursor").exception();
                }
                if (cloudantError?.reason == "Value for limit is too large, must not exceed 200") {
                    throw AppCommonErrors.INSTANCE.parameterInvalid("count", "Should not exceed 200").exception();
                }
            }

            logger.error("Failed to $verb for view $viewName in CloudantDB. db: $dbUri, error: $cloudantError.error, reason: $cloudantError.reason")
            throw new CloudantException("Failed to $verb for view $viewName, error: $cloudantError.error, reason: $cloudantError.reason")
        }
    }

    private static void checkWriteErrors(String verb, CloudantDbUri dbUri, CloudantEntity entity, Response response) {
        if (response.statusCode != HttpStatus.OK.value() &&
                response.statusCode != HttpStatus.CREATED.value() &&
                response.statusCode != HttpStatus.ACCEPTED.value()) {
            CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                logger.error("Failed to $verb object to CloudantDB. db: $dbUri, error: $cloudantError.error, reason: $cloudantError.reason");
                throw AppCommonErrors.INSTANCE.updateConflict(dbUri.dbName, entity.cloudantId, entity.cloudantRev).exception();
            }

            throw new CloudantException("Failed to $verb object to Cloudant, error: $cloudantError.error," +
                    " reason: $cloudantError.reason")
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class SearchRequest {
        String query
        String sort
        Integer limit
        String bookmark
        Boolean include_docs
    }
}
