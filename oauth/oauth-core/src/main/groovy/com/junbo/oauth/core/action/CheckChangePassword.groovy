package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Created by haomin on 14-6-13.
 */
class CheckChangePassword implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckChangePassword)

    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def loginState = contextWrapper.loginState
        Assert.notNull(loginState, 'loginState is null')

        return userService.getUserCredential(new UserId(loginState.userId)).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { UserCredential userCredential ->
            if (userCredential != null && userCredential.changeAtNextLogin == true) {
                return Promise.pure(new ActionResult('changePasswordRequired'))
            }

            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the user service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
