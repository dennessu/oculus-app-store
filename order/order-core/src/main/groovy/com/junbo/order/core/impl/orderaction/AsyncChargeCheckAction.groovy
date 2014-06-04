package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 5/26/2014.
 */
@CompileStatic
class AsyncChargeCheckAction implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        if (context?.orderServiceContext?.isAsyncCharge) {
            return Promise.pure(new ActionResult('asynccharge'))
        }
        return Promise.pure(null)
    }
}
