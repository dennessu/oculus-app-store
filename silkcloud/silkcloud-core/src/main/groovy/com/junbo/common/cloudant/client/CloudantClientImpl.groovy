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
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.CloudantId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.Utils
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.ProxyServer
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
import java.util.concurrent.ConcurrentHashMap

/**
 * CloudantClientImpl.
 */
@CompileStatic
class CloudantClientImpl implements CloudantClientInternal {
    private static final Logger logger = LoggerFactory.getLogger(CloudantClientImpl.class)

    private static final String VIEW_PATH   = '/_design/views/_view/'
    private static final String SEARCH_PATH = '/_design/views/_search/'
    private static final String DEFAULT_DESIGN_ID_PREFIX = '_design/'
    private static final String DESIGN_VIEW = '_design/views'
    private static final Integer SKIP_WARN_THRESHOLD = 1000000

    private static CloudantClientImpl singleton = new CloudantClientImpl()

    private Map<String, Boolean> hasViewMap = new ConcurrentHashMap<>()

    public static CloudantClientImpl instance() {
        return singleton
    }

    private static JunboAsyncHttpClient asyncHttpClient = JunboAsyncHttpClient.instance()
    private static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()

    private static Integer writeCount = ConfigServiceManager.instance().getConfigValueAsInt("common.cloudant.writes", null)
    private static Integer acceptedRetryCount = ConfigServiceManager.instance().getConfigValueAsInt("common.cloudant.retries", null)
    private static Integer acceptedRetryInterval = ConfigServiceManager.instance().getConfigValueAsInt("common.cloudant.retriesInterval", null)

    private static Map<String, String> getWriteParam(Map<String, String> initial = null, boolean noOverrideWrites) {
        if (initial == null && writeCount == null) {
            return Collections.emptyMap()
        }

        if (initial == null) {
            initial = [:]
        }
        if (writeCount != null && !noOverrideWrites) {
            initial.put("w", writeCount.intValue().toString())
        }
        return initial
    }

    private static ProxyServer proxyServer = resolveProxyServer();

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        if (entity.getId() != null) {
            entity.setCloudantId(entity.getId().toString())
            CloudantId.validate(entity.cloudantId)
        }

        return executeRequest(dbUri, HttpMethod.POST, '', getWriteParam(noOverrideWrites), entity).then({ Response response ->
            return checkWriteErrors("create", dbUri, entity, response).then {
                def cloudantResponse = marshaller.unmarshall(response.responseBody, CloudantResponse)

                Assert.isTrue(cloudantResponse.ok)
                entity.cloudantId = cloudantResponse.id
                entity.cloudantRev = cloudantResponse.revision

                return Promise.pure(entity)
            }
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id) {
        CloudantId.validate(id)
        return executeRequest(dbUri, HttpMethod.GET, urlEncode(id), Collections.emptyMap(), null).then({ Response response ->

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
    def <T extends CloudantEntity> Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        CloudantId.validate(entity.cloudantId)
        return executeRequest(dbUri, HttpMethod.PUT, urlEncode(entity.cloudantId), getWriteParam(noOverrideWrites), entity).then({ Response response ->
            return checkWriteErrors("update", dbUri, entity, response).then {
                def cloudantResponse = marshaller.unmarshall(response.responseBody, CloudantResponse)

                Assert.isTrue(cloudantResponse.ok)
                Assert.isTrue(entity.cloudantId.equals(cloudantResponse.id))

                entity.cloudantRev = cloudantResponse.revision

                return Promise.pure(entity)
            }
        })
    }

    @Override
    def <T extends CloudantEntity> Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites) {
        if (entity != null) {
            // force update cloudantId
            entity.setCloudantId(entity.getId().toString())
            CloudantId.validate(entity.cloudantId)
            return executeRequest(dbUri, HttpMethod.DELETE, urlEncode(entity.cloudantId), getWriteParam(['rev': entity.cloudantRev], noOverrideWrites), null).then({ Response response ->
                if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
                    // already deleted
                    return Promise.pure()
                }
                return checkWriteErrors("delete", dbUri, entity, response)
            })
        }
        return Promise.pure(null);
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, CloudantViewQueryOptions options) {
        def originalLimit = options.limit
        def query = fillQueryParams(dbUri, options)

        def hasDesignViewFuture = {
            def designViewExists = hasViewMap.get(buildGetAllKey(dbUri))
            if (designViewExists == null) {
                return queryDesignView(dbUri).then { Boolean queryDesignViewExists ->
                    designViewExists = queryDesignViewExists
                    hasViewMap.put(buildGetAllKey(dbUri), designViewExists)

                    return Promise.pure(designViewExists)
                }
            }
            return Promise.pure(designViewExists)
        }

        return hasDesignViewFuture().then { Boolean designViewExists ->
            if (designViewExists) {
                // get 1 extra element to make sure the page with specified limit can be returned
                // because the query result can contain the design doc
                query['limit'] = ((Integer)query['limit']) + 1
            }

            return executeRequest(dbUri, HttpMethod.GET, '_all_docs', query, null).then { Response response ->
                checkViewErrors("get all", dbUri, "_all_docs", response)

                def result = (CloudantQueryResult)marshaller.unmarshall(response.responseBody, CloudantQueryResult, CloudantQueryResult.AllResultEntity, JsonNode)
                def list = new ArrayList<>()
                result.rows.each { CloudantQueryResult.ResultObject row ->
                    if (row.id.startsWith(DEFAULT_DESIGN_ID_PREFIX)) {
                        return
                    }
                    // tricky: effectively converting ResultObject<String, JsonNode> to ResultObject<String, T>
                    if (options.includeDocs && row.doc != null) {
                        row.doc = marshaller.unmarshall(row.doc.toString(), entityClass)
                    }
                    if (list.size() < originalLimit) {
                        list.add(row)
                    }
                }

                result.rows = list
                if (result.totalRows != null && designViewExists) {
                    // exclude the _design row
                    result.totalRows = result.totalRows - 1
                }
                return Promise.pure(result)
            }
        }
    }

    @Override
    def <T extends CloudantEntity> Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, CloudantViewQueryOptions options) {
        def query = fillQueryParams(dbUri, options)
        query.put('reduce', 'false')

        return executeRequest(dbUri, HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->
            checkViewErrors("query view", dbUri, viewName, response)
            return Promise.pure(marshaller.unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    def Promise<Boolean> queryDesignView(CloudantDbUri dbUri) {
        def query = [:]
        query.put('include_docs', 'false')
        query.put('key', "\"$DESIGN_VIEW\"")
        return executeRequest(dbUri, HttpMethod.GET, '_all_docs', query, null).then { Response response ->
            checkViewErrors("query design view", dbUri, "_all_docs", response)

            def result = (CloudantQueryResult)marshaller.unmarshall(response.responseBody, CloudantQueryResult, CloudantQueryResult.AllResultEntity, JsonNode)
            if (CollectionUtils.isEmpty(result.rows)) {
                return Promise.pure(false)
            } else {
                return Promise.pure(true)
            }
        }
    }

    @Override
    def Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String viewName, CloudantViewQueryOptions options) {
        def query = fillQueryParams(dbUri, options);
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
        if (proxyServer != null && !(dbUri.cloudantUri.value ==~ /^https?:\/\/(localhost|127\.0\.0\.1)(:|\/|$).*/ )) {
            requestBuilder.setProxyServer(proxyServer);
        }

        if (!StringUtils.isEmpty(dbUri.cloudantUri.username)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(dbUri.cloudantUri.username).setPassword(dbUri.cloudantUri.password)
                    .setUsePreemptiveAuth(true).setScheme(Realm.AuthScheme.BASIC).build();
            requestBuilder.setRealm(realm);
            if (!StringUtils.isEmpty(dbUri.cloudantUri.account)) {
                requestBuilder.setHeader("X-Cloudant-User", dbUri.cloudantUri.account)
            }
        }

        if (body != null) {
            def payload = marshall(body)
            requestBuilder.setBody(payload)
        }

        queryParams?.each { String key, Object value ->
            requestBuilder.addQueryParameter(key, Utils.safeToString(value))
        }

        if (method == HttpMethod.PUT || method == HttpMethod.POST) {
            requestBuilder.addHeader('Content-Type', 'application/json')
        }

        try {
            return requestBuilder.execute().recover { Throwable e ->
                logger.error("Cloudant call exception", e);
                throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
            }
        } catch (IOException e) {
            logger.error("Cloudant call exception", e);
            throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
        }
    }

    private LinkedHashMap fillQueryParams(CloudantDbUri dbUri, CloudantViewQueryOptions options) {
        def query = [:]

        if (options.limit == null) {
            throw AppCommonErrors.INSTANCE.parameterInvalid("limit", "limit is required.").exception();
        }
        query.put('limit', options.limit)
        if (options.skip != null) {
            if (options.skip > SKIP_WARN_THRESHOLD) {
                logger.warn("Cloudant query to {} skip {} is too large.", dbUri, options.skip)
            }
            query.put('skip', options.skip.toString())
        }

        if (options.descending) {
            query.put('descending', 'true')

            // swap startkey and endkey
            def swapkey = options.startKey
            options.startKey = options.endKey
            options.endKey = swapkey
        }
        if (options.includeDocs) {
            query.put('include_docs', 'true')
        }
        if (options.startKey) {
            if (options.startKey == options.endKey) {
                query.put('key', serializeKey(options.startKey))
            } else {
                query.put('startkey', serializeKey(options.startKey))
                if (options.endKey) {
                    query.put('endkey', serializeKey(options.endKey))
                }
            }
        }
        return query
    }

    private static String serializeKey(Object keys) {
        if (keys == null) {
            return null
        }

        if (keys instanceof Object[]) {
            Object[] keyArray = (Object[])keys;

            Object obj = keyArray.find { Object key ->
                return key == null
            }

            if (obj != null) {
                throw new IllegalStateException('startkey or endkey cannot contain null value')
            }

            def result = []
            keys.each { Object key ->
                if (key == CloudantViewQueryOptions.HIGH_KEY) {
                    result.add("{}")
                } else {
                    result.add(getEncodeParameterString(key))
                }
            }
            return '[' + result.join(',') + ']'
        } else {
            return getEncodeParameterString(keys)
        }
    }

    private static String buildGetAllKey(CloudantDbUri dbUri) {
        return dbUri.toString() + ':' + DESIGN_VIEW
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

    private void checkViewErrors(String verb, CloudantDbUri dbUri, String viewName, Response response) {
        if (response.statusCode != HttpStatus.OK.value()) {
            CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)

            if (response.statusCode == HttpStatus.BAD_REQUEST.value()) {
                if (cloudantError?.reason == "Invalid cursor parameter supplied") {
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

    private Promise<Void> checkWriteErrors(String verb, CloudantDbUri dbUri, CloudantEntity entity, Response response) {
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
        if (response.statusCode == HttpStatus.ACCEPTED.value()) {
            // log the warning because subsequent GETs and GETs to views may fail.
            logger.warn("Call $verb $dbUri for ${entity?.cloudantId} returned 202 ACCEPTED.")

            Integer localWriteCount = writeCount
            if (entity == null || entity.cloudantId == null || writeCount == null) {
                // no retry logic
            } else {
                // wait until the read is successful

                int expectedReadResponse = HttpStatus.OK.value()
                if ("delete".equalsIgnoreCase(verb)) {
                    expectedReadResponse = HttpStatus.NOT_FOUND.value()
                }

                int retry = 0
                Closure<Promise> retryFunc = null
                retryFunc = {
                    Thread.sleep(acceptedRetryInterval)
                    logger.debug("Retrying GET for $dbUri for ${entity?.cloudantId} due to 202 Accepted response. Retry: $retry")
                    ++retry
                    if (retry > acceptedRetryCount) {
                        logger.warn("Retry count exceeded to wait for $expectedReadResponse response for $verb $dbUri for ${entity?.cloudantId} returned 202 ACCEPTED. Retry: $retry")
                        return Promise.pure(null)
                    }

                    return executeRequest(dbUri, HttpMethod.GET, urlEncode(entity.cloudantId), ["r": localWriteCount.toString()], null).then({ Response retryResp ->
                        logger.debug("Retried GET for $dbUri for ${entity?.cloudantId} due to 202 Accepted response. Current response: ${retryResp.statusCode}. Retry: $retry")
                        if (retryResp.statusCode == expectedReadResponse) {
                            logger.debug("Succeeded to wait for $expectedReadResponse response for $verb $dbUri for ${entity?.cloudantId} returned 202 ACCEPTED. Retry: $retry")
                            return Promise.pure(null)
                        } else {
                            logger.warn("Failed to wait for $expectedReadResponse response for $verb $dbUri for ${entity?.cloudantId} returned 202 ACCEPTED. Retry: $retry")
                            return retryFunc()
                        }
                    }).recover { Throwable t ->
                        logger.error("Exception in waiting for $expectedReadResponse response for $verb $dbUri for ${entity?.cloudantId} returned 202 ACCEPTED. Retry: $retry", t)
                        return retryFunc()
                    }
                }

                return retryFunc()
            }

        }

        return Promise.pure(null)
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class SearchRequest {
        String query
        String sort
        Integer limit
        String bookmark
        Boolean include_docs
    }

    private static ProxyServer resolveProxyServer() {
        ConfigService configService = ConfigServiceManager.instance();
        String proxyServer = configService.getConfigValue("cloudant.proxy");
        return Utils.parseProxyServer(proxyServer);
    }

    private static ProxyServer getProxyServer() {
        return proxyServer;
    }

    private static String getEncodeParameterString(Object raw) {
        return ObjectMapperProvider.instance().writer().writeValueAsString(raw)
    }
}
