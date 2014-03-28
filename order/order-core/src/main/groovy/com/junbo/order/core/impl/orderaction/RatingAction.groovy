package com.junbo.order.core.impl.orderaction
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.db.entity.enums.ItemType
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        if (order.honoredTime == null) {
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
            // call billing to calculate tax
            if (order.totalAmount == 0) {
                LOGGER.info('name=Skip_Calculate_Tax_Zero_Total_Amount')
                return Promise.pure(null)
            }
            if (order.orderItems.any { OrderItem oi ->
                oi.type == ItemType.PHYSICAL.toString()
            }) {
                // check whether the shipping method id and shipping address id are there
                if (order.shippingAddressId == null) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_shippingAddressId')
                        return Promise.pure(null)
                    }
                    LOGGER.error('name=Missing_shippingAddressId_To_Calculate_Tax')
                        throw AppErrors.INSTANCE.missingParameterField('shippingAddressId').exception()
                }
                if (order.shippingMethodId == null) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_shippingMethodId')
                        return Promise.pure(null)
                    }
                    LOGGER.error('name=Missing_shippingMethodId_To_Calculate_Tax')
                        throw AppErrors.INSTANCE.missingParameterField('shippingMethodId').exception()
                }
            } else {
                // check pi is there
                if (CollectionUtils.isEmpty(order.paymentInstruments)) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_PI')
                        return Promise.pure(null)
                    }
                    LOGGER.error('name=Missing_paymentInstruments_To_Calculate_Tax')
                        throw AppErrors.INSTANCE.missingParameterField('paymentInstruments').exception()
                }
            }
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT)).syncRecover {
                Throwable throwable ->
                    LOGGER.error('name=Fail_To_Calculate_Tax', throwable)
                    // TODO parse the tax error
                    throw AppErrors.INSTANCE.billingConnectionError().exception()
            }.then { Balance balance ->
                if (balance == null) {
                    // TODO: log order charge action error?
                    LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                    throw AppErrors.INSTANCE.balanceNotFound().exception()
                } else {
                    CoreBuilder.fillTaxInfo(order, balance)
                }
                return Promise.pure(null)
            }
        }
    }
}