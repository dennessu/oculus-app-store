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
import com.junbo.oauth.spec.model.ResponseType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * ValidateResponseType.
 */
@CompileStatic
class ValidateResponseType implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def client = contextWrapper.client

        String responseTypeParam = parameterMap.getFirst(OAuthParameters.RESPONSE_TYPE)

        Set<ResponseType> responseTypeSet = []

        if (StringUtils.hasText(responseTypeParam)) {
            String[] responseTypes = responseTypeParam.split(' ')
            responseTypes.each { String responseTypeStr ->
                if (ResponseType.isValid(responseTypeStr)) {
                    ResponseType responseType = ResponseType.valueOf(responseTypeStr.toUpperCase())
                    if (client.responseTypes.contains(responseType)) {
                        responseTypeSet.add(responseType)
                    } else {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('response_type', responseTypeParam).exception()
                    }
                } else {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('response_type', responseTypeParam).exception()
                }
            }
        } else {
            throw AppCommonErrors.INSTANCE.fieldRequired('response_type').exception()
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.responseTypes = responseTypeSet

        return Promise.pure(null)
    }
}
