package com.junbo.order.core.impl.orderaction
import com.google.common.math.DoubleMath
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemAction
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.resource.SubledgerItemResource
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

    private final static double TOLERANCE = 0.00001

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

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
            Promise.each(serviceContext.refundedOrderItems) { OrderItem orderItem ->
                subledgerItemResource.getSubledgerItemsByOrderItemId(orderItem.getId()).then { List<SubledgerItem> subledgerItems ->
                    if (subledgerItems.isEmpty()) {
                        return Promise.pure(null)
                    }

                    Collection<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerItemAction == SubledgerItemAction.PAYOUT.name() }
                    Collection<SubledgerItem> refundSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerItemAction == SubledgerItemAction.REFUND.name() }
                    if (payoutSubledgerItems.size() != 1) {
                        LOGGER.warn('name=NumberOf_Payout_SubledgerItem_Invalid, orderItemId={}, size={}, skipped', payoutSubledgerItems.size(), orderItem.id)
                        return Promise.pure(null)
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

                    BigDecimal totalAmountRemain = payoutSubledgerItem.totalAmount
                    BigDecimal totalPayoutAmountRemain = payoutSubledgerItem.totalPayoutAmount
                    refundSubledgerItems.each { SubledgerItem subledgerItem ->
                        totalAmountRemain -= subledgerItem.totalAmount
                        totalPayoutAmountRemain -= subledgerItem.totalPayoutAmount
                    }

                    if (DoubleMath.fuzzyEquals(totalAmountRemain.doubleValue(), orderItem.totalAmount.doubleValue(), TOLERANCE)) {  // amount fully refunded
                        refundedSubledgerItem.totalAmount = totalAmountRemain
                        refundedSubledgerItem.totalPayoutAmount = totalPayoutAmountRemain
                    } else {
                        refundedSubledgerItem.totalAmount = orderItem.totalAmount
                        refundedSubledgerItem.totalPayoutAmount = orderItem.totalAmount * payoutSubledgerItem.totalPayoutAmount / payoutSubledgerItem.totalAmount
                    }

                    subledgerItemResource.createSubledgerItem(refundedSubledgerItem).syncThen {
                        LOGGER.info('name=CreateRefundSubledgerItem, orderItemId={}, quantity={}',
                                refundedSubledgerItem.orderItem, refundedSubledgerItem.totalQuantity)
                    }
                }
            }
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SubledgerCreateItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }
}
