/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.ning.http.client.Response
import com.ning.http.client.extra.ListenableFutureAdapter
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
/**
 * ValidateCaptcha.
 */
@CompileStatic
class ValidateCaptcha implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCaptcha)

    private boolean enabled

    private String recaptchaPublicKey

    private String recaptchaPrivateKey

    private String recaptchaVerifyEndpoint

    private JunboAsyncHttpClient asyncHttpClient

    @Required
    void setEnabled(boolean enabled) {
        this.enabled = enabled
    }

    @Required
    void setRecaptchaPublicKey(String recaptchaPublicKey) {
        this.recaptchaPublicKey = recaptchaPublicKey
    }

    @Required
    void setRecaptchaPrivateKey(String recaptchaPrivateKey) {
        this.recaptchaPrivateKey = recaptchaPrivateKey
    }

    @Required
    void setRecaptchaVerifyEndpoint(String recaptchaVerifyEndpoint) {
        this.recaptchaVerifyEndpoint = recaptchaVerifyEndpoint
    }

    @Required
    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        if (!enabled) {
            contextWrapper.captchaSucceed = true
            return Promise.pure(new ActionResult('success'))
        }

        if (!contextWrapper.captchaRequired) {
            return Promise.pure(new ActionResult('success'))
        }

        def parameterMap = contextWrapper.parameterMap

        String challenge = parameterMap.getFirst('recaptcha_challenge_field')

        if (StringUtils.isEmpty(challenge)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('recaptcha_challenge_field').error())
        }

        String response = parameterMap.getFirst('recaptcha_response_field')

        if (StringUtils.isEmpty(response)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('recaptcha_response_field').error())
        }

        if (!contextWrapper.errors.isEmpty()) {
            return Promise.pure(new ActionResult('error'))
        }

        def requestBuilder = asyncHttpClient.preparePost(recaptchaVerifyEndpoint)
        requestBuilder.addParameter('privatekey', recaptchaPrivateKey)
        requestBuilder.addParameter('challenge', challenge)
        requestBuilder.addParameter('response', response)
        requestBuilder.addParameter('remoteip', contextWrapper.remoteAddress)

        try {
            return Promise.wrap(ListenableFutureAdapter.asGuavaFuture(requestBuilder.execute())).then { Response res ->
                if (res.statusCode / 100 == 2) {
                    String[] results = res.responseBody.split('\n')
                    if (results[0] == 'true') {
                        contextWrapper.captchaSucceed = true
                        return Promise.pure(new ActionResult('success'))
                    }
                    contextWrapper.errors.add(AppErrors.INSTANCE.invalidRecaptcha(results[1]).error())
                    return Promise.pure(new ActionResult('error'))
                }

                contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingRecaptcha().error())
                return Promise.pure(new ActionResult('error'))
            }
        } catch (IOException ex) {
            LOGGER.error('Error calling the recaptcha server', ex)
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingRecaptcha().error())
            return Promise.pure(new ActionResult('error'))
        }
    }
}
