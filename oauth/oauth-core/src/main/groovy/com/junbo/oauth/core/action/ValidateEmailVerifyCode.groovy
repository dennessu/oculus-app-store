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
class ValidateEmailVerifyCode implements Action {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        String code = (String) context.requestScope[OAuthParameters.EMAIL_VERIFY_CODE]
        if (!tokenGenerator.isValidEmailVerifyCode(code)) {
            throw AppExceptions.INSTANCE.invalidVerificationCode().exception()
        }

        return Promise.pure(null)
    }
}
