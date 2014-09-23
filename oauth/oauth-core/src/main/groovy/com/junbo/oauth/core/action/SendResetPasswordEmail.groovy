package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.UserService
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Created by haomin on 14-6-13.
 */
@CompileStatic
class SendResetPasswordEmail implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendResetPasswordEmail)

    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def user = contextWrapper.user
        def loginState = contextWrapper.loginState

        Assert.notNull(user, 'user is null')
        Assert.notNull(loginState, 'loginState is null')
        user.id = new UserId(loginState.userId)

        return userService.sendResetPassword((UserId)(user.id), contextWrapper).recover { Throwable e ->
            handleException(e, contextWrapper)
            // Return success no matter the email has been sent or not
            return Promise.pure(new ActionResult('success'))
        }.then {
            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error when sending reset password email', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingEmail().error())
        }
    }
}
