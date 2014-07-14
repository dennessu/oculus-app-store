package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ValidateResetPasswordCode implements Action {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        String code = (String) context.requestScope[OAuthParameters.RESET_PASSWORD_CODE]

        if (!tokenGenerator.isValidResetPasswordCode(code)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.invalidVerificationCode().error())
            return Promise.pure(new ActionResult('error'))
        }

        contextWrapper.resetPasswordCode = code

        return Promise.pure(new ActionResult('success'))
    }
}
