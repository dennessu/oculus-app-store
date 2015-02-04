package com.junbo.order.jobs

import com.junbo.order.spec.model.OrderPendingAction
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 2015/2/3.
 */
@CompileStatic
interface OrderPendingActionProcessor {

    boolean processPendingAction(OrderPendingAction orderPendingAction);

}
