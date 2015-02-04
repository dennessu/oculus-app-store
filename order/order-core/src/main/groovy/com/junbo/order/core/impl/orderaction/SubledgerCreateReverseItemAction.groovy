package com.junbo.order.core.impl.orderaction

import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.impl.common.SubledgerUtils
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerAmount
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
class SubledgerCreateReverseItemAction implements Action, InitializingBean {


    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerCreateReverseItemAction)

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    SubledgerCreateReverseItemActionType actionType

    public enum SubledgerCreateReverseItemActionType {
        REFUND,
        CHARGE_BACK
    }

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
            LOGGER.error('name=SubledgerCreateReverseItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }

    private Promise innerExecute(OrderServiceContext serviceContext) {
        List<OrderItem> reversedOrderItems
        if (actionType == SubledgerCreateReverseItemActionType.REFUND) {
            reversedOrderItems = serviceContext.refundedOrderItems
        } else {
            reversedOrderItems = serviceContext.order.orderItems
        }

        return Promise.each(reversedOrderItems) { OrderItem orderItem ->
            subledgerItemResource.getSubledgerItemsByOrderItemId(orderItem.getId()).then { List<SubledgerItem> subledgerItems ->
                if (subledgerItems.isEmpty()) {
                    LOGGER.error('name=Empty_SubledgerItems,orderItemId={}', orderItem.getId())
                    return Promise.pure(null)
                }

                List<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }.asList()
                if (payoutSubledgerItems.isEmpty()) {
                    LOGGER.error('name=Empty_PayoutSubledgerItems,orderItemId={}', orderItem.getId())
                    return Promise.pure(null)
                }

                List<SubledgerItem> existingReverseSubledgerItems = subledgerItems.findAll {
                    SubledgerItem item -> SubledgerUtils.getSubledgerType(item).payoutActionType == SubledgerType.PayoutActionType.DEDUCT
                }.asList()

                SubledgerItem reverseSubledgerItem = createReverseSubledgerItem(payoutSubledgerItems[0], existingReverseSubledgerItems, orderItem)
                SubledgerAmount amount = SubledgerUtils.getSubledgerAmount(reverseSubledgerItem)
                if (!amount.anyPositive()) {
                    return Promise.pure()
                }

                subledgerItemResource.createSubledgerItem(reverseSubledgerItem).syncThen {
                    LOGGER.info('name=CreateReverseSubledgerItem, orderItemId={}, quantity={}',
                            reverseSubledgerItem.orderItem, reverseSubledgerItem.totalQuantity)
                }
            }
        }.syncThen {
            return null
        }
    }

    private SubledgerItem createReverseSubledgerItem(SubledgerItem payoutSubledgerItem, List<SubledgerItem> existingReverseSubledgerItems, OrderItem reverseOrderItem) {
        SubledgerItem subledgerItem = SubledgerUtils.buildSubledgerItem(payoutSubledgerItem, null)

        SubledgerType subledgerType
        if (this.actionType == SubledgerCreateReverseItemActionType.CHARGE_BACK) {
            subledgerType = SubledgerType.CHARGE_BACK // todo : handle DECLINE, CHARGE_BACK outside of time window
        } else {
            subledgerType = SubledgerType.REFUND
        }
        subledgerItem.subledgerType = subledgerType.name()

        SubledgerAmount payoutAmount = SubledgerUtils.getSubledgerAmount(payoutSubledgerItem)
        SubledgerAmount reversedAmount = new SubledgerAmount()
        existingReverseSubledgerItems.each { SubledgerItem e ->
            reversedAmount = reversedAmount.add(SubledgerUtils.getSubledgerAmount(e))
        }
        SubledgerAmount remainedAmount = payoutAmount.substract(reversedAmount)
        SubledgerAmount zero = new SubledgerAmount()

        SubledgerAmount finalAmount = getReverseAmountFromOrderItem(reverseOrderItem, payoutSubledgerItem)
        if (subledgerType.payoutActionType == SubledgerType.PayoutActionType.DEDUCT) {
            finalAmount = finalAmount.min(remainedAmount).max(zero)
        }
        SubledgerUtils.updateSubledgerAmount(finalAmount, subledgerItem)
        return subledgerItem
    }

    private SubledgerAmount getReverseAmountFromOrderItem(OrderItem reverseOrderItem, SubledgerItem payoutSubledgerItem) {
        SubledgerAmount amount = new SubledgerAmount()
        amount.totalQuantity = reverseOrderItem.quantity
        amount.totalAmount = reverseOrderItem.totalAmount
        amount.taxAmount = (reverseOrderItem.totalTax == null) ? 0 : reverseOrderItem.totalTax
        amount.totalPayoutAmount = reverseOrderItem.totalAmount * payoutSubledgerItem.totalPayoutAmount / payoutSubledgerItem.totalAmount
        return amount
    }

    @Override
    void afterPropertiesSet() throws Exception {
        Assert.notNull(actionType)
    }
}
