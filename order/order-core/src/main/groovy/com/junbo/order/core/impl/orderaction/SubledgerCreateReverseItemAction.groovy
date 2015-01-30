package com.junbo.order.core.impl.orderaction

import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert

import javax.annotation.Resource
/**
 * Created by fzhang on 7/8/2014.
 */
@CompileStatic
class SubledgerCreateReverseItemAction implements Action, InitializingBean{


    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerCreateReverseItemAction)

    private final static double TOLERANCE = 0.00001

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    SubledgerCreateReverseItemActionType actionType

    public enum SubledgerCreateReverseItemActionType {
        REFUND,
        CHARGE_BACK
    }

    private

    void setReverseSubledgerType(SubledgerType reverseSubledgerType) {
        this.reverseSubledgerType = reverseSubledgerType
    }

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext

        transactionHelper.executeInNewTransaction {
            innerExecute(serviceContext)
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SubledgerCreateRefundItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }

    private Promise innerExecute(OrderServiceContext serviceContext) {
        return builder.getCurrency(serviceContext).then { com.junbo.identity.spec.v1.model.Currency currency ->
            Promise.each(serviceContext.refundedOrderItems) { OrderItem orderItem ->
                subledgerItemResource.getSubledgerItemsByOrderItemId(orderItem.getId()).then { List<SubledgerItem> subledgerItems ->
                    if (subledgerItems.isEmpty()) {
                        return Promise.pure(null)
                    }
                    List<SubledgerItem> original = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }.asList()
                    if (original.isEmpty()) {
                        LOGGER.error('name=Empty_SubledgerItems,orderItemId={}', orderItem.getId())
                        return Promise.pure(null)
                    }

                    boolean hasReversed = subledgerItems.any { SubledgerItem item -> item.subledgerType == SubledgerType.REFUND.name() ||
                            item.subledgerType == SubledgerType.CHARGE_BACK.name()}

                    SubledgerType itemSubledgerType
                    if (this.actionType == SubledgerCreateReverseItemActionType.CHARGE_BACK) {
                        boolean outOfWindow = false
                        if (outOfWindow) {
                            itemSubledgerType = SubledgerType.CHARGE_BACK_OTW
                        } else if (hasReversed) {
                            itemSubledgerType = SubledgerType.CHARGE_BACK_REFUNDED
                        } else {
                            itemSubledgerType = SubledgerType.CHARGE_BACK
                        }
                    } else {
                        itemSubledgerType = SubledgerType.REFUND
                    }

                    // create reverse subledger items
                    original.each { SubledgerItem originalSubledgerItem ->
                        SubledgerItem reverseSubledgerItem = CoreBuilder.buildSubledgerItemFromOriginal(originalSubledgerItem)
                        reverseSubledgerItem.subledgerType = itemSubledgerType
                        subledgerItemResource.createSubledgerItem(reverseSubledgerItem).syncThen {
                            LOGGER.info('name=CreateReverseSubledgerItem, orderItemId={}, quantity={}',
                                    reverseSubledgerItem.orderItem, reverseSubledgerItem.totalQuantity)
                        }
                    }
                }
            }
        }.syncThen {
            return null
        }
    }

    /* temporarily comment out partial refund.
    private Promise innerExecute(OrderServiceContext serviceContext) {
        return builder.getCurrency(serviceContext).then { com.junbo.identity.spec.v1.model.Currency currency ->
            Promise.each(serviceContext.refundedOrderItems) { OrderItem orderItem ->
                subledgerItemResource.getSubledgerItemsByOrderItemId(orderItem.getId()).then { List<SubledgerItem> subledgerItems ->
                    if (subledgerItems.isEmpty()) {
                        return Promise.pure(null)
                    }

                    Collection<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }
                    Collection<SubledgerItem> refundSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.REFUND.name() }
                    if (payoutSubledgerItems.size() != 1) {
                        LOGGER.warn('name=NumberOf_Payout_SubledgerItem_Invalid, orderItemId={}, size={}, skipped', payoutSubledgerItems.size(), orderItem.id)
                        return Promise.pure(null)
                    }

                    SubledgerItem payoutSubledgerItem = payoutSubledgerItems[0]
                    SubledgerItem refundedSubledgerItem = new SubledgerItem(
                            orderItem: orderItem.getId(),
                            offer: payoutSubledgerItem.offer,
                            status: SubledgerItemStatus.PENDING_PROCESS.name(),
                            subledgerItemAction: SubledgerType.REFUND.name(),
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
        }.syncThen {
            return null
        }
    }*/

    @Override
    void afterPropertiesSet() throws Exception {
        Assert.notNull(actionType)
    }
}
