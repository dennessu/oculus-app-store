package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
class VerifyResetPasswordCode implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyResetPasswordCode)

    private ResetPasswordCodeRepository resetPasswordCodeRepository
    private UserResource userResource

    @Required
    void setResetPasswordCodeRepository(ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.resetPasswordCodeRepository = resetPasswordCodeRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        String code = contextWrapper.resetPasswordCode

        if (StringUtils.isEmpty(code)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingResetPasswordCode().error())
            return Promise.pure(new ActionResult('error'))
        }

        ResetPasswordCode resetPasswordCode = resetPasswordCodeRepository.getAndRemove(code)

        if (resetPasswordCode == null) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.invalidVerificationCode().error())
            return Promise.pure(new ActionResult('error'))
        }

        return userResource.get(new UserId(resetPasswordCode.userId), new UserGetOptions()).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(new ActionResult('error'))
        }.then { User user ->
            if (user == null) {
                return Promise.pure(new ActionResult('error'))
            }

            contextWrapper.user = user

            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
        }
    }
}
