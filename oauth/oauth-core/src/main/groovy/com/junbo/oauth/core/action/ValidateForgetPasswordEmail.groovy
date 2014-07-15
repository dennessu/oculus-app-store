package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Created by minhao on 5/10/14.
 */
@CompileStatic
class ValidateForgetPasswordEmail implements Action {
    private UserPersonalInfoResource userPersonalInfoResource

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$', Pattern.CASE_INSENSITIVE);

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String email = parameterMap.getFirst(OAuthParameters.EMAIL)

        if (StringUtils.isEmpty(email)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingEmail().error())
        }
        else {
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidEmail(email).error())
            }
        }

        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: email)).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { Results<UserPersonalInfo> results ->

            if (results == null || results.items == null || results.items.isEmpty()) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.noAccountFound().error())
            }

            contextWrapper.forgetPasswordEmail = email
            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
        }
    }
}
