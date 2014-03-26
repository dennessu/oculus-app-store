package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class RatingAction implements Action {

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository


    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        if (order.honoredTime == null ) {
            order.honoredTime = new Date()
            order.orderItems.each { OrderItem oi ->
                oi.honoredTime = order.honoredTime
            }
        }
        return facadeContainer.ratingFacade.rateOrder(order).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_Rating_Error', throwable)
            // TODO parse the rating error
            throw AppErrors.INSTANCE.ratingConnectionError().exception()
        }.then { OrderRatingRequest ratingResult ->
            // todo handle rating violation
            if (ratingResult == null) {
                // TODO: log order charge action error?
                LOGGER.error('name=Rating_Result_Null')
                throw AppErrors.INSTANCE.ratingResultInvalid().exception()
            }
            CoreBuilder.fillRatingInfo(order, ratingResult)
            //  no need to log event for rating
            // Call billing to calculate tax
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT)).syncRecover {
                Throwable throwable ->
                    LOGGER.error('name=Order_Tax_Error', throwable)
                    // TODO parse the tax error
                    throw AppErrors.INSTANCE.billingConnectionError().exception()
            }.then { Balance balance ->
                if (balance == null) {
                    // TODO: log order charge action error?
                    LOGGER.info('fail to calculate tax, balance is null')
                    throw AppErrors.INSTANCE.balanceNotFound().exception()
                } else {
                    CoreBuilder.fillTaxInfo(order, balance)
                }
                return Promise.pure(null)
            }
        }
    }
}