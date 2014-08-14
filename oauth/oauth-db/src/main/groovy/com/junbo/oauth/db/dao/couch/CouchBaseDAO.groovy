/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch
import com.junbo.common.cloudant.client.CloudantGlobalUri
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.async.JunboAsyncHttpClient.BoundRequestBuilder
import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.BaseDAO
import com.junbo.oauth.db.entity.BaseEntity
import com.junbo.oauth.db.exception.DBConnectException
import com.junbo.oauth.db.exception.DBException
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required

//import junit.framework.Assert
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
import java.lang.reflect.ParameterizedType
/**
 * CouchBaseDAO.
 */
@CompileStatic
abstract class CouchBaseDAO<T extends BaseEntity> implements InitializingBean, BaseDAO<T, String> {
    protected static final String VIEW_PATH = '/_design/views/_view/'
    protected final Class<T> entityClass
    protected JunboAsyncHttpClient asyncHttpClient
    protected String couchDBUri
    protected String couchDBUser
    protected String couchDBPassword
    protected String dbName
    protected String dbNamePrefix
    protected String fullDbName

    @Required
    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Required
    void setCouchDBUri(String couchDBUri) {
        CloudantGlobalUri uri = new CloudantGlobalUri(couchDBUri)
        this.couchDBUri = uri.currentDcUri.value
        this.couchDBUser = uri.currentDcUri.username
        this.couchDBPassword = uri.currentDcUri.password
    }

    void setCouchDBUser(String couchDBUser) {
        this.couchDBUser = couchDBUser
    }

    void setCouchDBPassword(String couchDBPassword) {
        this.couchDBPassword = couchDBPassword
    }

    @Required
    void setDbName(String dbName) {
        this.dbName = dbName
    }

    void setDbNamePrefix(String dbNamePrefix) {
        this.dbNamePrefix = dbNamePrefix
    }

    protected CouchBaseDAO() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
    }

    @Override
    T save(T entity) {
        def response = executeRequest(HttpMethod.POST, '', [:], entity)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(response.responseBody, CouchError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new DBUpdateConflictException("Failed to save object to CouchDB, error: $couchError.error," +
                        " reason: $couchError.reason")
            }

            throw new DBException("Failed to save object to CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        def couchResponse = JsonMarshaller.unmarshall(response.responseBody, CouchResponse)

        //Assert.assertTrue(couchResponse.ok)
        entity.id = couchResponse.id
        entity.revision = couchResponse.revision

        return entity
    }

    @Override
    T get(String id) {
        def response = executeRequest(HttpMethod.GET, id, [:], null)

        if (response.statusCode != HttpStatus.OK.value()) {
            return null
        }

        if (response.responseBody == null) {
            return null
        }

        T entity = (T) JsonMarshaller.unmarshall(response.responseBody, entityClass)
        return entity.deleted ? null : entity
    }

    protected List<T> getAll() {
        def response = executeRequest(HttpMethod.GET, '_all_docs', [:], null)

        if (response.statusCode != HttpStatus.OK.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(response.responseBody, CouchError)

            throw new DBException("Failed to execute get all operation, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        def couchSearchResult = (CouchSearchResult) JsonMarshaller.unmarshall(response.responseBody, CouchSearchResult,
                CouchSearchResult.AllResultEntity)

        return couchSearchResult.rows.collect { CouchSearchResult.ResultObject result ->
            return get(result.id)
        }
    }

    @Override
    T update(T entity) {
        def response = executeRequest(HttpMethod.PUT, entity.id, [:], entity)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(response.responseBody, CouchError)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new DBUpdateConflictException("Failed to update object to CouchDB, error: $couchError.error," +
                        " reason: $couchError.reason")
            }

            throw new DBException("Failed to update object to CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        def couchResponse = JsonMarshaller.unmarshall(response.responseBody, CouchResponse)

        //Assert.assertTrue(couchResponse.ok)
        entity.revision = couchResponse.revision

        return entity
    }

    @Override
    void delete(T entity) {
        def response = executeRequest(HttpMethod.DELETE, entity.id, ['rev': entity.revision], null)

        if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(response.responseBody, CouchError)
            throw new DBException("Failed to delete object from CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (dbNamePrefix != null) {
            fullDbName = dbNamePrefix + dbName;
        } else {
            fullDbName = dbName;
        }
    }

    protected CouchSearchResult internalQueryView(String viewName, String key) {
        def query = ['include_docs': 'true']

        if (key != null) {
            query.put('key', "\"$key\"")
        }

        def response = executeRequest(HttpMethod.GET, VIEW_PATH + viewName, query, null)

        if (response.statusCode != HttpStatus.OK.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(response.responseBody, CouchError)
            throw new DBException("Failed to query the view $viewName, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        return (CouchSearchResult) JsonMarshaller.unmarshall(response.responseBody, CouchSearchResult, String, entityClass)
    }

    protected List<T> queryView(String viewName, String key) {
        CouchSearchResult searchResult = internalQueryView(viewName, key)
        if (searchResult.rows != null) {
            return searchResult.rows.collect { CouchSearchResult.ResultObject result ->
                return (T)result.doc
            }
        }

        return []
    }

    protected Response executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        UriBuilder uriBuilder = UriBuilder.fromUri(couchDBUri)
        uriBuilder.path(fullDbName)
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

        if (!StringUtils.isEmpty(couchDBUser)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(couchDBUser).setPassword(couchDBPassword)
                    .setUsePreemptiveAuth(true).setScheme(Realm.AuthScheme.BASIC).build();
            requestBuilder.setRealm(realm);
        }

        if (body != null) {
            requestBuilder.setBody(JsonMarshaller.marshall(body))
        }

        queryParams.each { String key, String value ->
            requestBuilder.addQueryParameter(key, value)
        }

        if (method == HttpMethod.PUT || method == HttpMethod.POST) {
            requestBuilder.addHeader('Content-Type', 'application/json')
        }

        try {
            Response response = requestBuilder.execute().get()
            String responseBody = response.responseBody // always consume the response body.

            return response
        } catch (IOException e) {
            throw new DBConnectException('Exception happened while executing request to couch DB', e)
        }
    }

    protected BoundRequestBuilder getRequestBuilder(HttpMethod method, String uri) {
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
