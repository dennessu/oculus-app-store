/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.TosId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.model.TosGetOptions
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.identity.spec.v1.resource.UserTosAgreementResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * CreateUserTosAgreement.
 */
@CompileStatic
class CreateUserTosAgreement implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTosAgreement)
    private TosResource tosResource

    private UserTosAgreementResource userTosAgreementResource

    @Required
    void setTosResource(TosResource tosResource) {
        this.tosResource = tosResource
    }

    @Required
    void setUserTosAgreementResource(UserTosAgreementResource userTosAgreementResource) {
        this.userTosAgreementResource = userTosAgreementResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def tosIdStrs = parameterMap.get(OAuthParameters.TOS)
        def user = contextWrapper.user

        Assert.notNull(user, 'user is null')

        if (tosIdStrs == null || tosIdStrs.isEmpty()) {
            return Promise.pure(new ActionResult('success'))
        }

        for (String tosIdStr : tosIdStrs) {
            UniversalId tosId = IdUtil.fromLink(new Link(href: tosIdStr))
            if (tosId == null || !(tosId instanceof TosId)) {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterInvalid('tos').error())
                return Promise.pure(new ActionResult('error'))
            }

            try {
                Tos tos = tosResource.get(tosId as TosId, new TosGetOptions()).get()

                UserTosAgreement userTosAgreement = new UserTosAgreement(
                        userId: user.id as UserId,
                        tosId: tos.getId(),
                        agreementTime: new Date()
                )

                userTosAgreementResource.create(userTosAgreement).get()

                if (contextWrapper.tosChallenge != null) {
                    contextWrapper.tosChallenge.remove(tos.getId().value)
                }
            } catch (Throwable e) {
                handleException(e, contextWrapper)
                return Promise.pure(new ActionResult('error'))
            }
        }

        if (contextWrapper.tosChallenge != null && !contextWrapper.tosChallenge.isEmpty()) {
            contextWrapper.errors.add(AppErrors.INSTANCE.tosChallenge().error())
            return Promise.pure(new ActionResult('error'))
        }

        return Promise.pure(new ActionResult('success'))
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
