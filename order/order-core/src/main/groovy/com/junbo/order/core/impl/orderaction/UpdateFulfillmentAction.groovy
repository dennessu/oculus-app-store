package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult

/**
 * Created by LinYi on 14-2-27.
 */
class UpdateFulfillmentAction implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        /*
      1. update fulfillment status
      2. get balance id
      3. capture auth
      */
        return null
    }
}
