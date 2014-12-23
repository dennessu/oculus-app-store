/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * ValidateClient
 */
@CompileStatic
class ValidateClient implements Action {
    private static final String INTERNAL_HEADER_NAME = 'oculus-internal'
    private ClientRepository clientRepository
    private boolean enableInternalCheck

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setEnableInternalCheck(boolean enableInternalCheck) {
        this.enableInternalCheck = enableInternalCheck
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        if (!StringUtils.hasText(clientId)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('client_id').exception()
        }

        Client client = clientRepository.getClient(clientId)
        if (client == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_id', clientId).exception()
        }

        if (enableInternalCheck && Boolean.TRUE.equals(client.internal)) {
            def headerMap = contextWrapper.headerMap
            String internal = headerMap.getFirst(INTERNAL_HEADER_NAME)
            if (!Boolean.TRUE.equals(Boolean.parseBoolean(internal))) {
                throw AppCommonErrors.INSTANCE
                        .forbiddenWithMessage('This client is for internal use only').exception()
            }
        }

        contextWrapper.client = client

        return Promise.pure(null)
    }
}
