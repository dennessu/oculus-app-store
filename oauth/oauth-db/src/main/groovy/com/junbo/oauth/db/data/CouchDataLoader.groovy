/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.data

import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.ClientDAO
import com.junbo.oauth.db.dao.ScopeDAO
import com.junbo.oauth.db.dao.couch.CouchClientDAOImpl
import com.junbo.oauth.db.dao.couch.CouchScopeDAOImpl
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.db.repo.impl.ClientRepositoryImpl
import com.junbo.oauth.db.repo.impl.ScopeRepositoryImpl
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.Scope
import com.ning.http.client.AsyncHttpClient
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils

/**
 * CouchDataLoader.
 */
@CompileStatic
class CouchDataLoader {
    private static final String DEFAULT_DB_URI = 'http://localhost:5984;dc0'
    private static final int ENTITY_TYPE_INDEX = 0
    private static final int ENTITY_KEY_INDEX = 1
    private static final int ENTITY_BODY_INDEX = 2

    private String dbUri
    private final JunboAsyncHttpClient asyncHttpClient

    private final ClientRepository clientRepository
    private final ScopeRepository scopeRepository
    //private final ApiDefinitionRepository apiDefinitionRepository


    CouchDataLoader(String dbUri) {
        this.dbUri = dbUri
        asyncHttpClient = new JunboAsyncHttpClient(new AsyncHttpClient())

        ClientDAO clientDAO = new CouchClientDAOImpl()
        clientDAO.dbName = 'client'
        clientDAO.asyncHttpClient = asyncHttpClient
        clientDAO.couchDBUri = dbUri
        clientDAO.afterPropertiesSet()

        ClientRepositoryImpl clientRepositoryImpl = new ClientRepositoryImpl()
        clientRepositoryImpl.clientDAO = clientDAO
        clientRepository = clientRepositoryImpl

        ScopeDAO scopeDAO = new CouchScopeDAOImpl()
        scopeDAO.dbName = 'scope'
        scopeDAO.asyncHttpClient = asyncHttpClient
        scopeDAO.couchDBUri = dbUri
        scopeDAO.afterPropertiesSet()

        ScopeRepositoryImpl scopeRepositoryImpl = new ScopeRepositoryImpl()
        scopeRepositoryImpl.scopeDAO = scopeDAO
        scopeRepository = scopeRepositoryImpl

//        ApiDefinitionDAO apiDefinitionDAO = new CouchApiDefinitionDAOImpl()
//        apiDefinitionDAO.dbName = 'api_definition'
//        apiDefinitionDAO.asyncHttpClient = asyncHttpClient
//        apiDefinitionDAO.couchDBUri = dbUri
//        apiDefinitionDAO.afterPropertiesSet()
//
//        ApiDefinitionRepositoryImpl apiDefinitionRepositoryImpl = new ApiDefinitionRepositoryImpl()
//        apiDefinitionRepositoryImpl.apiDefinitionDAO = apiDefinitionDAO
//        apiDefinitionRepository = apiDefinitionRepositoryImpl
    }

    static void main(String[] args) {
        String dbUri = DEFAULT_DB_URI

        if (args.length > 0) {
            dbUri = args[0]
        }

        CouchDataLoader loader = new CouchDataLoader(dbUri)

        loader.populateData()
        System.exit(0)
    }

    void populateData() {
        def inputStream = CouchDataLoader.classLoader.getResourceAsStream('data/changelog.dat')
        String content = IOUtils.toString(inputStream, 'UTF-8')

        String[] records = content.split('\n\n')

        for (String record : records) {
            String[] tokens = record.split('\t')

            //Assert.assertTrue(tokens.length == 3)

            if (tokens[ENTITY_TYPE_INDEX] == 'client') {
                populateClient(tokens[ENTITY_KEY_INDEX], tokens[ENTITY_BODY_INDEX])
            }

            if (tokens[ENTITY_TYPE_INDEX] == 'scope') {
                populateScope(tokens[ENTITY_KEY_INDEX], tokens[ENTITY_BODY_INDEX])
            }

//            if (tokens[ENTITY_TYPE_INDEX] == 'api') {
//                populateApiDefinition(tokens[ENTITY_KEY_INDEX], tokens[ENTITY_BODY_INDEX])
//            }
        }
    }

    private void populateClient(String clientId, String entity) {
        Client client = JsonMarshaller.unmarshall(entity, Client)
        Client existingClient = clientRepository.getClient(clientId)

        client.clientId = clientId
        if (existingClient != null) {
            client.revision = existingClient.revision
            clientRepository.updateClient(client)
        } else {
            clientRepository.saveClient(client)
        }
    }

    private void populateScope(String scopeName, String entity) {
        Scope scope = JsonMarshaller.unmarshall(entity, Scope)
        Scope existingScope = scopeRepository.getScope(scopeName)

        scope.name = scopeName
        if (existingScope != null) {
            scope.revision = existingScope.revision
            scopeRepository.updateScope(scope)
        } else {
            scopeRepository.saveScope(scope)
        }
    }

//    private void populateApiDefinition(String apiName, String entity) {
//        ApiDefinition api = JsonMarshaller.unmarshall(entity, ApiDefinition)
//        ApiDefinition existing = apiDefinitionRepository.getApi(apiName)
//
//        api.apiName = apiName
//        if (existing != null) {
//            api.revision = existing.revision
//            apiDefinitionRepository.updateApi(api)
//        } else {
//            apiDefinitionRepository.saveApi(api)
//        }
//    }
}
