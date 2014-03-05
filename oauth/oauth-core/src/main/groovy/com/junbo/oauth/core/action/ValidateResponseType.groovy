/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.ResponseType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidateResponseType implements Action {

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        String responseTypeParam = parameterMap.getFirst(OAuthParameters.RESPONSE_TYPE)

        Set<ResponseType> responseTypeSet = []

        if (StringUtils.hasText(responseTypeParam)) {
            String[] responseTypes = responseTypeParam.split(' ')
            responseTypes.each { String responseTypeStr ->
                if (ResponseType.isValid(responseTypeStr)) {
                    ResponseType responseType = ResponseType.valueOf(responseTypeStr.toUpperCase())
                    if (appClient.allowedResponseTypes.contains(responseType)) {
                        responseTypeSet.add(responseType)
                    } else {
                        throw AppExceptions.INSTANCE.invalidResponseType(responseTypeParam).exception()
                    }
                } else {
                    throw AppExceptions.INSTANCE.invalidResponseType(responseTypeParam).exception()
                }
            }
        } else {
            throw AppExceptions.INSTANCE.missingResponseType().exception()
        }

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.responseTypes = responseTypeSet

        return true
    }
}
