/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.UserId
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
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
    private static final String EMAIL_ACTION = 'EmailVerification'
    private static final String EMAIL_VERIFY_PATH = 'oauth2/verify-email'

    private EmailResource emailResource

    private EmailTemplateResource emailTemplateResource

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def user = contextWrapper.user

        def email = parameterMap.getFirst(OAuthParameters.EMAIL)

        Assert.notNull(user, 'user is null')

        EmailVerifyCode code = new EmailVerifyCode(
                userId: (user.id as UserId).value,
                email: email
        )

        emailVerifyCodeRepository.save(code)

        def request = (ContainerRequest) contextWrapper.request
        UriBuilder uriBuilder = UriBuilder.fromUri(request.baseUri)
        uriBuilder.path(EMAIL_VERIFY_PATH)

        uriBuilder.queryParam(OAuthParameters.CODE, code.code)

        QueryParam queryParam = new QueryParam(
                source: EMAIL_SOURCE,
                action: EMAIL_ACTION,
                // TODO: remove the hard coded locale
                locale: 'en_US'
        )

        // TODO: cache the email template for each locale.
        emailTemplateResource.getEmailTemplates(queryParam).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { EmailTemplate template ->
            if (template == null) {
                LOGGER.warn('Failed to get the email template, skip the email send')
                return Promise.pure(new ActionResult('success'))
            }

            Email emailToSend = new Email(
                    userId: user.id as UserId,
                    templateId: template.id as EmailTemplateId,
                    recipients: [email].asList(),
                    replacements: [
                            'name': user.username,
                            'verify_link': uriBuilder.build().toString()
                    ]
            )

            emailResource.postEmail(emailToSend).recover { Throwable e ->
                LOGGER.error('Error sending email to the user', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingEmail().error())
                return Promise.pure(null)
            }.then { Email emailSent ->
                if (emailSent == null) {
                    LOGGER.warn('Failed to send the email, skip the email send')
                }
                // Return success no matter the email has been successfully sent.
                return Promise.pure(new ActionResult('success'))
            }
        }

    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the email service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
        }
    }
}
