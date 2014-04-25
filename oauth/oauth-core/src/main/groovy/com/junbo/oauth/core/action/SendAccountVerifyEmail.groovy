/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.email.spec.model.Email
import com.junbo.email.spec.resource.EmailResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

import javax.ws.rs.core.UriBuilder

/**
 * SendAccountVerifyEmail.
 */
@CompileStatic
class SendAccountVerifyEmail implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendAccountVerifyEmail)
    private static final String EMAIL_SOURCE = 'SilkCloud'
    private static final String EMAIL_ACTION = 'accountEmailVerification'
    private static final String EMAIL_VERIFY_PATH = 'oauth2/verify-email'

    private EmailResource emailResource

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def userPii = contextWrapper.userPii

        Assert.notNull(userPii, 'userPii is null')

        EmailVerifyCode code = new EmailVerifyCode(
                userId: userPii.userId.value,
                email: userPii.emails.values().first().value
        )

        emailVerifyCodeRepository.save(code)

        def request = (ContainerRequest) contextWrapper.request
        UriBuilder uriBuilder = UriBuilder.fromUri(request.baseUri)
        uriBuilder.path(EMAIL_VERIFY_PATH)

        uriBuilder.queryParam(OAuthParameters.CODE, code.code)

        Email email = new Email(
                userId: userPii.userId,
                source: EMAIL_SOURCE,
                action: EMAIL_ACTION,
                // TODO: temporal locale hardcode
                locale: 'en_US',
                recipients: [userPii.emails.values().first().value].asList(),
                replacements: ['verifyUri': uriBuilder.build().toString()]
        )

        emailResource.postEmail(email).recover { Throwable e ->
            LOGGER.error('Error sending email to the user', e)
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingEmail().error())
            return Promise.pure(null)
        }.then { Email emailSent ->
            // Return success no matter the email has been successfully sent.
            return Promise.pure('success')
        }
    }
}
