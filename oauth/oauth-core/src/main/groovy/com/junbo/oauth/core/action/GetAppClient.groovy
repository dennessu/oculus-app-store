/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.AppClientRepository
import com.junbo.oauth.spec.model.AppClient
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class GetAppClient implements Action {

    private AppClientRepository appClientRepository

    @Required
    void setAppClientRepository(AppClientRepository appClientRepository) {
        this.appClientRepository = appClientRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        if (!StringUtils.hasText(clientId)) {
            throw AppExceptions.INSTANCE.missingClientId().exception()
        }

        AppClient appClient = appClientRepository.getAppClient(clientId)

        ServiceContextUtil.setAppClient(context, appClient)

        return true
    }
}
