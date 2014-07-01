package com.junbo.common.cloudant.client
import com.fasterxml.jackson.annotation.JsonInclude
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.CloudantMarshaller
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
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture
/**
 * CloudantClient.
 */
@CompileStatic
class CloudantClientImpl<T extends CloudantEntity> implements CloudantClient<T>, InitializingBean {
    private Class<T> entityClass
    private JunboAsyncHttpClient asyncHttpClient

    // marshaller is used to marshall/unmarshall payload
    private CloudantMarshaller marshaller

    private String cloudantUser
    private String cloudantPassword
    private String cloudantDBUri
    private String dbNamePrefix
    private String dbName
    private String fullDbName

    @Required
    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
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

    @Required
    void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass
    }

    String getFullDbName() {
        return fullDbName
    }

    @Override
    Promise<T> cloudantPost(T entity) {
        return executeRequest(HttpMethod.POST, '', [:], entity).then({ Response response ->
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
            entity.cloudantId = cloudantResponse.id
            entity.cloudantRev = cloudantResponse.revision

            return Promise.pure(entity)
        })
    }

    @Override
    Promise<T> cloudantGet(String id) {
        return executeRequest(HttpMethod.GET, id, [:], null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                return Promise.pure(null)
            }

            if (response.responseBody == null) {
                return Promise.pure(null)
            }

            return Promise.pure((T) unmarshall(response.responseBody, entityClass))
        })
    }

    @Override
    Promise<T> cloudantPut(T entity) {
        // force update cloudantId
        entity.setCloudantId(entity.getId().toString())
        return executeRequest(HttpMethod.PUT, entity.cloudantId, [:], entity).then({ Response response ->
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
            Assert.isTrue(entity.cloudantId.equals(cloudantResponse.id))

            entity.cloudantRev = cloudantResponse.revision

            return Promise.pure(entity)
        })
    }

    @Override
    Promise<Void> cloudantDelete(T entity) {
        if (entity != null) {
            return executeRequest(HttpMethod.DELETE, entity.cloudantId, ['rev': entity.cloudantRev], null).then({ Response response ->
                if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
                    CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
                    throw new CloudantException("Failed to delete object from Cloudant, error: $cloudantError.error," +
                            " reason: $cloudantError.reason")
                }
                return Promise.pure(null);
            })
        }
        return Promise.pure(null);
    }

    @Override
    Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending) {

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

        return executeRequest(HttpMethod.GET, '_all_docs', query, null).then ({ Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)

                throw new CloudantException("Failed to execute get all operation, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            def cloudantSearchResult = unmarshall(response.responseBody,
                    CloudantQueryResult, CloudantQueryResult.AllResultEntity, Object.class)

            List<T> list = new ArrayList<>()
            return Promise.each(cloudantSearchResult.rows) { CloudantQueryResult.ResultObject result ->
                if (result.id.startsWith(DEFAULT_DESIGN_ID_PREFIX)) {
                    return Promise.pure(null)
                }
                return cloudantGet(result.id).then { T temp ->
                    if (temp != null) {
                        list.add(temp)
                    }
                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(list)
            }
        })
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (dbNamePrefix != null) {
            fullDbName = dbNamePrefix + dbName;
        } else {
            fullDbName = dbName;
        }
    }

    @Override
    Promise<CloudantQueryResult> queryView(String viewName, String key, String startKey, String endKey,
                                                           Integer limit, Integer skip, boolean descending,
                                                           boolean includeDocs) {
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

        return executeRequest(HttpMethod.GET, Utils.combineUrl(VIEW_PATH, viewName), query, null).then({ Response response ->

            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    Promise<CloudantQueryResult> search(String searchName, String queryString, Integer limit, String bookmark,
                                                        boolean includeDocs) {
        def searchRequest = new SearchRequest(
                query: queryString,
                limit: limit,
                bookmark: bookmark,
                include_docs: includeDocs
        )

        return executeRequest(HttpMethod.POST, Utils.combineUrl(SEARCH_PATH, searchName), [:], searchRequest).then({ Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to query the view, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            return Promise.pure(unmarshall(response.responseBody, CloudantQueryResult, String, entityClass))
        })
    }

    private Promise<Response> executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        return executeRequest(method, path, queryParams, body, true)
    }

    private Promise<Response> executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body, boolean useDbPath) {
        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantDBUri)
        if (useDbPath) {
            uriBuilder.path(fullDbName)
        }
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        requestBuilder.setBodyEncoding("UTF-8");

        if (!StringUtils.isEmpty(cloudantUser)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(cloudantUser).setPassword(cloudantPassword)
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

    private JunboAsyncHttpClient.BoundRequestBuilder getRequestBuilder(HttpMethod method, String uri) {
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
        if (str == null) return null
        return marshaller.unmarshall(str, cls)
    }

    protected <T> T unmarshall(String str, Class<T> cls, Class<?>... parameterClass) {
        if (str == null) return null
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
