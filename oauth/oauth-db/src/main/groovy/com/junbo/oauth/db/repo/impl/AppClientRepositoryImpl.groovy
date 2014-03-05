/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.AppClientDAO
import com.junbo.oauth.db.entity.AppClientEntity
import com.junbo.oauth.db.repo.AppClientRepository
import com.junbo.oauth.spec.model.AppClient
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class AppClientRepositoryImpl implements AppClientRepository {
    private AppClientDAO appClientDAO

    @Required
    void setAppClientDAO(AppClientDAO appClientDAO) {
        this.appClientDAO = appClientDAO
    }

    @Override
    AppClient getAppClient(String clientId) {
        Assert.notNull(clientId)
        return wrap(appClientDAO.get(clientId))
    }

//    private static AppClientEntity unwrap(AppClient appClient) {
//        return new AppClientEntity(
//                clientId: appClient.clientId,
//                clientSecret: appClient.clientSecret,
//                defaultRedirectUri: appClient.defaultRedirectUri,
//                allowedRedirectUris: appClient.allowedRedirectUris,
//                allowedScopes: appClient.allowedScopes
//        )
//    }

    private static AppClient wrap(AppClientEntity entity) {
        return new AppClient(
                clientId: entity.clientId,
                clientSecret: entity.clientSecret,
                defaultRedirectUri: entity.defaultRedirectUri,
                allowedRedirectUris: entity.allowedRedirectUris,
                allowedScopes: entity.allowedScopes
        )
    }
}
