/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.db.repo.AppClientRepository
import com.junbo.oauth.spec.model.AppClient
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * GetAppClient
 */
@CompileStatic
class GetAppClient implements Action {

    private AppClientRepository appClientRepository

    @Required
    void setAppClientRepository(AppClientRepository appClientRepository) {
        this.appClientRepository = appClientRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        if (!StringUtils.hasText(clientId)) {
            throw AppExceptions.INSTANCE.missingClientId().exception()
        }

        AppClient appClient = appClientRepository.getAppClient(clientId)

        contextWrapper.appClient = appClient

        return Promise.pure(null)
    }
}
