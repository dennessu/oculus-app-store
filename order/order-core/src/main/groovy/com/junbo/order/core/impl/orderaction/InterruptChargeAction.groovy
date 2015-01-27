package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.spec.error.AppErrors
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
        LOGGER.info('name=Order_Billing_Failed')
        throw AppErrors.INSTANCE.billingChargeFailed().exception()
    }
}
