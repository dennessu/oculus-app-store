package com.junbo.order.core.impl.orderaction
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.SubledgerType
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

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

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
        return Promise.each(serviceContext.order.orderItems) { OrderItem orderItem ->
            subledgerService.createChargebackReverseSubledgerItem(chargeBackReverseSubledgerType, orderItem)
            return Promise.pure()
        }
    }
}
