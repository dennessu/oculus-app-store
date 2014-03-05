/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidatePrompt implements Action {
    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String prompt = parameterMap.getFirst(OAuthParameters.PROMPT)

        Set<String> promptSet = []

        if (StringUtils.hasText(prompt)) {
            String[] prompts = prompt.split(' ')
            boolean isValid = prompts.every {
                String promptToken -> Prompt.isValid(promptToken)
            }

            if (!isValid) {
                throw AppExceptions.INSTANCE.invalidPrompt(prompt).exception()
            }

            promptSet.addAll(prompts)
        }

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.setPrompts(promptSet.collect {
            String promptStr -> Prompt.valueOf(prompt.toUpperCase())
        }.toSet())

        return true
    }
}
