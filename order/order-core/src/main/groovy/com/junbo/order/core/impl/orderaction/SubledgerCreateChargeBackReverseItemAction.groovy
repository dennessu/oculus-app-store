package com.junbo.order.core.impl.orderaction
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.impl.common.SubledgerUtils
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerAmount
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by fzhang on 2015/2/3.
 */
@CompileStatic
class SubledgerCreateChargeBackReverseItemAction implements Action {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerCreateReverseItemAction)

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext

        transactionHelper.executeInNewTransaction {
            innerExecute(serviceContext)
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SubledgerCreateChargeBackReverseItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }

    private Promise innerExecute(OrderServiceContext serviceContext) {
        SubledgerType chargeBackReverseSubledgerType = SubledgerType.CHARGE_BACK_REVERSAL // todo handle charge back reversal outside of time window
        SubledgerType chargeBackSubledgerType = chargeBackReverseSubledgerType == SubledgerType.CHARGE_BACK_REVERSAL ? SubledgerType.CHARGE_BACK : SubledgerType.CHARGE_BACK_OTW

        return Promise.each(serviceContext.order.orderItems) { OrderItem orderItem ->
            subledgerItemResource.getSubledgerItemsByOrderItemId(orderItem.getId()).then { List<SubledgerItem> subledgerItems ->
                List<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }.asList()
                if (payoutSubledgerItems.isEmpty()) {
                    LOGGER.error('name=Empty_PayoutSubledgerItems,orderItemId={}', orderItem.getId())
                    return Promise.pure(null)
                }

                boolean hasChargeBackReverseItem = subledgerItems.any { SubledgerItem item ->
                    return item.subledgerType == SubledgerType.CHARGE_BACK_REVERSAL.name() || item.subledgerType == SubledgerType.CHARGE_BACK_REVERSAL_OTW.name()
                }
                if (!hasChargeBackReverseItem) {
                    LOGGER.error("name=ReverseChargeBack_Subledger_Item_Already_Exists, orderId={}, orderItem={}", serviceContext.order.getId(), orderItem.getId())
                    return Promise.pure()
                }

                List<SubledgerItem> chargeBackSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == chargeBackSubledgerType.name() }.asList()
                if (chargeBackSubledgerItems.isEmpty()) {
                    LOGGER.error("name=ChargeBack_Items_NotFound, orderId={}, orderItem={}, chargeBackType={}", serviceContext.order.getId(), orderItem.getId(), chargeBackSubledgerType.name())
                    return Promise.pure()
                }
                SubledgerAmount amount = new SubledgerAmount()
                chargeBackSubledgerItems.each { SubledgerItem chargeBackSubledgerItem ->
                    amount = amount.add(SubledgerUtils.getSubledgerAmount(chargeBackSubledgerItem))
                }
                if (!amount.anyPositive()) {
                    return Promise.pure()
                }

                SubledgerItem subledgerItem = SubledgerUtils.buildSubledgerItem(payoutSubledgerItems[0], amount)
                subledgerItem.subledgerType = chargeBackReverseSubledgerType.name()
                subledgerItemResource.createSubledgerItem(subledgerItem).syncThen {
                    LOGGER.info('name=CreateSubledgerChargeBackReverseItem, orderItemId={}, quantity={}',
                            subledgerItem.orderItem, subledgerItem.totalQuantity)
                }
            }
        }.syncThen {
            return null
        }
    }
}
