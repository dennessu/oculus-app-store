package com.junbo.order.core.impl.orderaction
import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.SubledgerType
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
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

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

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

        SubledgerType subledgerType
        if (this.actionType == SubledgerCreateReverseItemActionType.CHARGE_BACK) {
            subledgerType = isChargeBackOutsideOfTimeWindow(serviceContext.orderEvent) ? SubledgerType.CHARGE_BACK_OTW : SubledgerType.CHARGE_BACK // todo : handle DECLINE ?
        } else {
            subledgerType = SubledgerType.REFUND
        }

        return Promise.each(reversedOrderItems) { OrderItem orderItem ->
            subledgerService.createReverseSubledgerItem(subledgerType, orderItem)
            return Promise.pure()
        }
    }

    private boolean isChargeBackOutsideOfTimeWindow(OrderEvent orderEvent) {
        if (StringUtils.isEmpty(orderEvent?.properties)) {
            return false
        }
        try {
            Map<String, Object> propertiesMap = (Map<String, Object> )ObjectMapperProvider.instance().readValue(orderEvent.properties, new TypeReference<Map<String, Object> >() {})
            return propertiesMap.containsKey('outsideOfTimeWindow')
        } catch (Exception ex) {
            LOGGER.error('name=Error_Parse_Properties', ex)
            return false
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        Assert.notNull(actionType)
    }
}
