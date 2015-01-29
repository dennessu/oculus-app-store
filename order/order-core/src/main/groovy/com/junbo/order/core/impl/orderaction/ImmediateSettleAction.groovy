package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
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
class ImmediateSettleAction extends BaseOrderEventAwareAction {
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        if (order.tentative) {
            throw AppErrors.INSTANCE.orderAlreadyInSettleProcess().exception()
        }
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)
        return orderInternalService.immediateSettle(order, context).then { ActionResult ar ->
            def orderActionResult = ActionUtils.getOrderActionResult(ar)
            ActionUtils.putBillingException(orderActionResult.exception, actionContext)
            ActionUtils.putBillingResult(orderActionResult.returnedEventStatus.name(), actionContext)
            return Promise.pure(ar)
        }
    }
}
