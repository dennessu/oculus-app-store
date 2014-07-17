package com.junbo.order.core.impl.orderaction

import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemAction
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by fzhang on 7/8/2014.
 */
@CompileStatic
class SubledgerCreateRefundItemAction implements Action {


    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerCreateRefundItemAction)

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext

        return builder.getCurrency(serviceContext).then { com.junbo.identity.spec.v1.model.Currency currency ->
            List<OrderItem> refundedOrderItems = serviceContext.refundedOrderItems
            refundedOrderItems.each { OrderItem orderItem ->
                List<SubledgerItem> subledgerItems = subledgerService.getSubledgerItemsByOrderItemId(orderItem.getId())
                if (subledgerItems.isEmpty()) {
                    return
                }

                Collection<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerItemAction == SubledgerItemAction.PAYOUT.name() }
                Collection<SubledgerItem> refundSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerItemAction == SubledgerItemAction.REFUND.name() }
                if (payoutSubledgerItems.size() != 1) {
                    LOGGER.warn('name=NumberOf_Payout_SubledgerItem_Invalid, orderItemId={}, size={}, skipped', payoutSubledgerItems.size(), orderItem.id)
                    return
                }

                SubledgerItem payoutSubledgerItem = payoutSubledgerItems[0]
                SubledgerItem refundedSubledgerItem = new SubledgerItem(
                        orderItem: orderItem.getId(),
                        offer: payoutSubledgerItem.offer,
                        status: SubledgerItemStatus.PENDING_PROCESS.name(),
                        subledgerItemAction: SubledgerItemAction.REFUND.name(),
                        totalQuantity: orderItem.quantity.longValue(),
                        originalSubledgerItem: payoutSubledgerItem.getId()
                )

                long lastRefundedQuantity = 0
                refundSubledgerItems.each { SubledgerItem subledgerItem ->
                    lastRefundedQuantity += subledgerItem.totalQuantity
                }

                if (lastRefundedQuantity + orderItem.quantity >= payoutSubledgerItem.totalQuantity) {
                    BigDecimal refundedAmount = 0, refundedPayoutAmout = 0
                    refundSubledgerItems.each { SubledgerItem subledgerItem ->
                        refundedAmount += subledgerItem.totalAmount
                        refundedPayoutAmout += subledgerItem.totalPayoutAmount
                    }
                    refundedSubledgerItem.totalAmount = payoutSubledgerItem.totalAmount - refundedAmount
                    refundedSubledgerItem.totalPayoutAmount = payoutSubledgerItem.totalPayoutAmount - refundedPayoutAmout
                } else {
                    refundedSubledgerItem.totalAmount = (payoutSubledgerItem.totalAmount * refundedSubledgerItem.totalQuantity / payoutSubledgerItem.totalQuantity).
                            setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                    refundedSubledgerItem.totalPayoutAmount = (payoutSubledgerItem.totalPayoutAmount * refundedSubledgerItem.totalQuantity / payoutSubledgerItem.totalQuantity).
                            setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                }

                subledgerService.createSubledgerItem(refundedSubledgerItem)
                LOGGER.info('name=CreateRefundSubledgerItem, orderItemId={}, quantity={}',
                        refundedSubledgerItem.orderItem, refundedSubledgerItem.totalQuantity)
            }
            return Promise.pure(null)
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SubledgerCreateItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }
}
