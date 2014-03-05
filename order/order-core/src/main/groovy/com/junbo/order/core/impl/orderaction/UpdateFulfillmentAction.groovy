package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.orderaction.context.BaseContext

/**
 * Created by LinYi on 14-2-27.
 */
class UpdateFulfillmentAction implements OrderAction<BaseContext> {
    @Override
    Promise<BaseContext> execute(BaseContext request) {
        /*
        1. update fulfillment status
        2. get balance id
        3. capture auth
        */

        return null
    }
}
