/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.BaseDAO
import com.junbo.oauth.db.entity.BaseEntity
import com.junbo.oauth.db.exception.DBConnectException
import com.junbo.oauth.db.exception.DBException
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import junit.framework.Assert
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import javax.ws.rs.core.UriBuilder

/**
 * CouchBaseDAO.
 */
@CompileStatic
abstract class CouchBaseDAO<T extends BaseEntity> implements InitializingBean, BaseDAO<T, String> {
    protected AsyncHttpClient asyncHttpClient
    protected String couchDBUri
    protected String dbName

    @Required
    void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Required
    void setCouchDBUri(String couchDBUri) {
        this.couchDBUri = couchDBUri
    }

    @Required
    void setDbName(String dbName) {
        this.dbName = dbName
    }

    @Override
    T save(T entity) {
        def response = executeRequest(HttpMethod.POST, '', [:], entity)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(CouchError, response.responseBody)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new DBUpdateConflictException("Failed to save object to CouchDB, error: $couchError.error," +
                        " reason: $couchError.reason")
            }

            throw new DBException("Failed to save object to CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        def couchResponse = JsonMarshaller.unmarshall(CouchResponse, response.responseBody)

        Assert.assertTrue(couchResponse.ok)
        entity.id = couchResponse.id
        entity.revision = couchResponse.revision

        return entity
    }

    protected T internalGet(String id, Class<T> clazz) {
        def response = executeRequest(HttpMethod.GET, id, [:], null)

        if (response.statusCode != HttpStatus.OK.value()) {
            return null
        }

        if (response.responseBody == null) {
            return null
        }

        T entity = (T) JsonMarshaller.unmarshall(clazz, response.responseBody)
        return entity.deleted ? null : entity
    }

    @Override
    T update(T entity) {
        def response = executeRequest(HttpMethod.PUT, entity.id, [:], entity)

        if (response.statusCode != HttpStatus.CREATED.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(CouchError, response.responseBody)

            if (response.statusCode == HttpStatus.CONFLICT.value()) {
                throw new DBUpdateConflictException("Failed to update object to CouchDB, error: $couchError.error," +
                        " reason: $couchError.reason")
            }

            throw new DBException("Failed to update object to CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }

        def couchResponse = JsonMarshaller.unmarshall(CouchResponse, response.responseBody)

        Assert.assertTrue(couchResponse.ok)
        entity.revision = couchResponse.revision

        return entity
    }

    @Override
    void delete(T entity) {
        def response = executeRequest(HttpMethod.DELETE, entity.id, ['rev': entity.revision], null)

        if (response.statusCode != HttpStatus.OK.value() && response.statusCode != HttpStatus.NOT_FOUND.value()) {
            CouchError couchError = JsonMarshaller.unmarshall(CouchError, response.responseBody)
            throw new DBException("Failed to delete object from CouchDB, error: $couchError.error," +
                    " reason: $couchError.reason")
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def response = executeRequest(HttpMethod.GET, '', [:], null)
        if (response.statusCode == HttpStatus.NOT_FOUND.value()) {
            response = executeRequest(HttpMethod.PUT, '', [:], null)

            if (response.statusCode != HttpStatus.CREATED.value()) {
                CouchError couchError = JsonMarshaller.unmarshall(CouchError, response.responseBody)
                throw new DBException("Failed to create the database, error: $couchError.error," +
                        " reason: $couchError.reason")
            }
        }
    }

    protected Response executeRequest(HttpMethod method, String path, Map<String, String> queryParams, Object body) {
        UriBuilder uriBuilder = UriBuilder.fromUri(couchDBUri)
        uriBuilder.path(dbName)
        uriBuilder.path(path)

        def requestBuilder = getRequestBuilder(method, uriBuilder.toTemplate())

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
            return requestBuilder.execute().get()
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
