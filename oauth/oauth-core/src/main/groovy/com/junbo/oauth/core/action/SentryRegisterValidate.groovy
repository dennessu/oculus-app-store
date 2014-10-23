package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.clientproxy.facebook.sentry.SentryFacade
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import com.junbo.store.spec.model.external.sentry.SentryCategory
import com.junbo.store.spec.model.external.sentry.SentryFieldConstant
import com.junbo.store.spec.model.external.sentry.SentryResponse
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/23/14.
 */
@CompileStatic
class SentryRegisterValidate implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SentryRegisterValidate)
    private SentryFacade sentryFacade

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        def textMap = [:]
        textMap[SentryFieldConstant.EMAIL.value] = parameterMap.getFirst(OAuthParameters.EMAIL)?.toString()
        textMap[SentryFieldConstant.USERNAME.value] = parameterMap.getFirst(OAuthParameters.USERNAME)?.toString()
        textMap[SentryFieldConstant.NAME.value] = parameterMap.getFirst(OAuthParameters.FIRST_NAME)?.toString() +
                ' ' + parameterMap.getFirst(OAuthParameters.LAST_NAME)?.toString()

        return sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_REGISTRATION_CREATE.value,
                textMap)
        ).recover { Throwable throwable ->
            LOGGER.error("CreateUser_Web:  Call sentry error, Ignore")
            return Promise.pure()
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                contextWrapper.errors.add(AppErrors.INSTANCE.sentryBlockAccess().error())
                contextWrapper.sentrySucceed = false
                return Promise.pure(new ActionResult('error'))
            }

            contextWrapper.sentrySucceed = true
            return Promise.pure(new ActionResult('success'))
        }
    }

    @Required
    void setSentryFacade(SentryFacade sentryFacade) {
        this.sentryFacade = sentryFacade
    }
}
