package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 5/14/14.
 */
@CompileStatic
@TypeChecked
@Component('validateCancelOrderAction')
class ValidateCancelOrderAction implements Action {

    @Qualifier('orderInternalService')
    @Autowired
    OrderInternalService service

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCancelOrderAction)


    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {

        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order

        return service.checkOrderCancelable(order.id.value).then { Boolean isCancelable ->
            if (!isCancelable) {
                LOGGER.info("name=Order_Is_Not_Cancelable, orderId = {}", order.id.value)
                throw AppErrors.INSTANCE.orderNotCancelable().exception()
            }
            LOGGER.info("name=Order_Is_Cancelable, orderId = {}", order.id.value)
        }
    }
}

