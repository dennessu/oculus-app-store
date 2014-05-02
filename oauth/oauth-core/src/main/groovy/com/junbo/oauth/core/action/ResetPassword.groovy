package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPassword implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        return null
    }
}
