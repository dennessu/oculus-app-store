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
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * ValidatePrompt.
 */
@CompileStatic
class ValidatePrompt implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap

        String prompt = parameterMap.getFirst(OAuthParameters.PROMPT)

        Set<String> promptSet = []

        if (StringUtils.hasText(prompt)) {
            String[] prompts = prompt.split(' ')
            boolean isValid = prompts.every {
                String promptToken -> Prompt.isValid(promptToken)
            }

            if (!isValid) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('prompt', prompt).exception()
            }

            promptSet.addAll(prompts)
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.setPrompts(promptSet.collect {
            String promptStr -> Prompt.valueOf(prompt.toUpperCase())
        }.toSet())

        return Promise.pure(null)
    }
}
