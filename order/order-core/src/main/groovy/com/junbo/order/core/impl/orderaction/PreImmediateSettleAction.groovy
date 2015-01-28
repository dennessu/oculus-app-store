package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class PreImmediateSettleAction implements Action {

    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(PreImmediateSettleAction)

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        // mark tentative = false
        orderInternalService.markSettlement(order)
        // check the billing history
        if (!CoreUtils.isSafeForImmediateSettle(order)) {
            // fail the immediate settle to prevent double billing
            // zombie/settlement job will handle the rest
            LOGGER.error("name=Order_Already_Has_BillingHistory. orderId: " + order.getId().value)
            throw AppErrors.INSTANCE.orderAlreadyInSettleProcess().exception()
        }
        return Promise.pure(null)
    }
}
