package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * CheckEmailVerified.
 */
@CompileStatic
class CheckEmailVerified implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckEmailVerified)

    private UserResource userResource

    private UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def loginState = contextWrapper.loginState
        Assert.notNull(loginState, 'loginState is null')

        return userResource.get(new UserId(loginState.userId), new UserGetOptions()).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { User user ->
            if (user == null) {
                return Promise.pure(new ActionResult('error'))
            }

            contextWrapper.user = user

            UserPersonalInfoLink defaultEmail = user.emails.find { UserPersonalInfoLink link ->
                link.isDefault
            }

            Assert.notNull(defaultEmail, 'defaultEmail is null')

            return Promise.pure(new ActionResult('next', ['defaultEmail': defaultEmail] as Map<String, Object>))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            UserPersonalInfoLink defaultEmail = result.data['defaultEmail'] as UserPersonalInfoLink

            return userPersonalInfoResource.get(defaultEmail.value, new UserPersonalInfoGetOptions())
                    .recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserPersonalInfo personalInfo ->
                if (personalInfo == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                // the pii data has been validated if lastValidateTime is not null
                if (personalInfo.lastValidateTime != null) {
                    return Promise.pure(new ActionResult('success'))
                }

                def email = ObjectMapperProvider.instance().treeToValue(personalInfo.value, Email)
                contextWrapper.userDefaultEmail = email.info

                return Promise.pure(new ActionResult('emailVerifyRequired'))
            }
        }
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
