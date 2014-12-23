package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ValidatePassword implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (StringUtils.isEmpty(password)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterRequired('password').error())
            return Promise.pure(new ActionResult('error'))
        }

        return Promise.pure(new ActionResult('success'))
    }
}
