package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-28.
 */
@CompileStatic
class ValidateExtraParameters implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        Map<String, String> extraMap = new HashMap<>()
        parameterMap.keySet().each { String key ->
            if (key.startsWith(OAuthParameters.EXTRA_PREFIX)) {
                extraMap.put(key, parameterMap.getFirst(key))
            }
        }
        contextWrapper.extraParameterMap = extraMap

        return Promise.pure(null)
    }
}
