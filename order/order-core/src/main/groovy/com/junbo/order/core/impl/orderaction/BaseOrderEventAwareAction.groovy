package com.junbo.order.core.impl.orderaction
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
import javax.transaction.Transactional

/**
 * Created by chriszhu on 3/13/14.
 */
@CompileStatic
abstract class BaseOrderEventAwareAction implements Action {
    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder
    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    OrderActionType orderActionType
    EventStatus eventStatus
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseOrderEventAwareAction)

    abstract Promise<ActionResult> doExecute(ActionContext context)

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext context) {

        beforeOrderEventAwareAction(context)

        return doExecute(context)
        .recover { Throwable ex ->
            afterThrowingOrderEventAwareAction(context, ex)
            return Promise.pure(null)
        }.then { ActionResult result ->
            afterOrderEventAwareAction(context, result)
            return Promise.pure(result)
        }
    }

    private void beforeOrderEventAwareAction(ActionContext context) {
        assert(context != null)
        LOGGER.info('name=Save_Order_Event_Before. action: {}', this.class.name)

        try {
            if (orderActionType != null) { // only create event if action type is set
                LOGGER.info('name=RECORD_ORDER_EVENT_' + orderActionType.name())
                def orderEvent = getOpenOrderEvent(context)
                if (orderEvent != null && orderEvent.order != null) {
                    transactionHelper.executeInNewTransaction {
                        repo.createOrderEvent(orderEvent)
                    }
                }
            } else {
                LOGGER.info('name=NOT_RECORD_ORDER_EVENT_ACTION_NULL')
            }
        } catch (e) {
            LOGGER.error('name=Save_Order_Event_Before', e)
            throw e
        }
    }

    private void afterThrowingOrderEventAwareAction(
            ActionContext context,
            Throwable ex) {
        assert (context != null)
        LOGGER.info('name=Save_Order_Event_AfterThrowing. action: {}', this.class.name)
        try {
            if (orderActionType != null) { // only create event if action type is set
                def oe = getReturnedOrderEvent(context, EventStatus.ERROR)
                transactionHelper.executeInNewTransaction {
                    repo.createOrderEvent(oe)
                }
            }
            throw ex
        } catch (e) {
            LOGGER.error('name=Save_Order_Event_AfterThrowing', e)
            throw e
        }
    }

    private void afterOrderEventAwareAction(
            ActionContext context,
            ActionResult result) {
        assert (context != null)
        LOGGER.info('name=Save_Order_Event_AfterReturning. action: {}', this.class.name)

        try {
            def oe = getReturnedOrderEvent(context, result)
            if (oe != null) {
                transactionHelper.executeInNewTransaction {
                    repo.createOrderEvent(oe)
                }
            }
        } catch (e) {
            LOGGER.error('name=Save_Order_Event_AfterReturning_Failed', e)
            throw e
        }
    }

    private OrderEvent getOpenOrderEvent(ActionContext context) {
        if (orderActionType != null) {
            def orderEvent = new OrderEvent()
            orderEvent.order = getOrderId(context)
            orderEvent.action = orderActionType
            orderEvent.status = EventStatus.OPEN.toString()
            orderEvent.trackingUuid = getTrackingUuid(context)
            orderEvent.eventTrackingUuid = UUID.randomUUID()
            orderEvent.flowName = getFlowName(context)
            return orderEvent
        }
        return null
    }

    private OrderEvent getReturnedOrderEvent(ActionContext context, ActionResult ar) {
        def orderActionResult = ActionUtils.getOrderActionResult(ar)
        if (orderActionResult != null) {
            EventStatus eventStatus = orderActionResult.returnedEventStatus
            if (eventStatus != null) {
                return getReturnedOrderEvent(context, eventStatus)
            }
        }
        return null
    }

    private OrderEvent getReturnedOrderEvent(ActionContext context, EventStatus eventStatus) {
        def orderEvent = getOpenOrderEvent(context)
        orderEvent?.status = eventStatus.toString()
        return orderEvent
    }

    private String getFlowName(ActionContext context) {
        return ActionUtils.getFlowName(context)
    }

    private UUID getTrackingUuid(ActionContext context) {
        def c = getOrderActionContext(context)
        return c?.trackingUuid
    }

    private OrderId getOrderId(ActionContext context) {
        def c = getOrderActionContext(context)
        return c?.orderServiceContext?.order?.getId()
    }

    private OrderActionContext getOrderActionContext(ActionContext context) {
        return ActionUtils.getOrderActionContext(context)
    }
}