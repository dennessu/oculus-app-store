package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class InterruptChargeAction implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptChargeAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def innerException = ActionUtils.getBillingException(actionContext)
        def result = ActionUtils.getBillingResult(actionContext)
        if (result == EventStatus.FAILED.name()) {
            LOGGER.info('name=Order_Billing_Declined.')
            if (innerException == null) {
                throw AppErrors.INSTANCE.billingChargeFailed().exception()
            } else {
                throw innerException.exception()
            }
        } else {
            LOGGER.error('name=Order_Billing_Unknown_Error.', innerException)
            if (innerException == null) {
                throw AppErrors.INSTANCE.billingConnectionError('no details').exception()
            } else {
                throw innerException.exception()
            }
        }
    }
}
