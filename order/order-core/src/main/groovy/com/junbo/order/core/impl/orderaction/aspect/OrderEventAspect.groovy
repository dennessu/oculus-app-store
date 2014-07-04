package com.junbo.order.core.impl.orderaction.aspect

import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.BaseOrderEventAwareAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 3/11/14.
 */
@Aspect
@Component('orderEventAspect')
@CompileStatic
@TypeChecked
class OrderEventAspect {

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder
    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper


    private final static Logger LOGGER = LoggerFactory.getLogger(OrderEventAspect)

    @Before(value = '@annotation(orderEventAwareBefore)', argNames = 'jp, orderEventAwareBefore')
    Promise<ActionResult> beforeOrderEventAwareAction(JoinPoint jp, OrderEventAwareBefore orderEventAwareBefore) {
        assert(orderEventAwareBefore != null)
        LOGGER.info('name=Save_Order_Event_Before. action: {}', orderEventAwareBefore.action())

        try {
            if (getOrderActionType(jp) != null) { // only create event if action type is set
                LOGGER.info('name=RECORD_ORDER_EVENT_' + getOrderActionType(jp))
                def orderEvent = getOpenOrderEvent(jp)
                if (orderEvent != null && orderEvent.order != null) {
                    transactionHelper.executeInNewTransaction {
                        repo.createOrderEvent(orderEvent)
                    }
                }
            } else {
                LOGGER.info('name=NOT_RECORD_ORDER_EVENT_ACTION_NULL')
            }
            return Promise.pure(null)
        } catch (e) {
            LOGGER.error('name=Save_Order_Event_Before', e)
            throw e
        }
    }

    @AfterThrowing(value = '@annotation(orderEventAwareAfter)',
            argNames = 'jp, orderEventAwareAfter, ex', throwing = 'ex')
    Promise<ActionResult> afterThrowingOrderEventAwareAction(
            JoinPoint jp,
            OrderEventAwareAfter orderEventAwareAfter,
            Throwable ex) {
        assert (orderEventAwareAfter != null)
        LOGGER.info('name=Save_Order_Event_AfterThrowing. action: {}', orderEventAwareAfter.action())
        try {
            if (getOrderActionType(jp) != null) { // only create event if action type is set
                def oe = getReturnedOrderEvent(jp, EventStatus.ERROR)
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

    @AfterReturning(value = '@annotation(orderEventAwareAfter)',
            argNames = 'jp, orderEventAwareAfter, rv', returning = 'rv')
    Promise<ActionResult> afterOrderEventAwareAction(
            JoinPoint jp,
            OrderEventAwareAfter orderEventAwareAfter,
            Promise<ActionResult> rv) {
        assert (orderEventAwareAfter != null)
        LOGGER.info('name=Save_Order_Event_AfterReturning. action: {}', orderEventAwareAfter.action())

        return rv.syncRecover { Throwable throwable ->
            def oe = getReturnedOrderEvent(jp, EventStatus.ERROR)
            if (oe != null && oe.order != null) {
                transactionHelper.executeInNewTransaction {
                    repo.createOrderEvent(oe)
                }
            }
            LOGGER.error('name=Save_Error_Order_Event_AfterReturning', throwable)
            throw throwable
        }
        .syncThen { ActionResult ar ->
            try {
                def oe = getReturnedOrderEvent(jp, ar)
                if (oe != null) {
                    transactionHelper.executeInNewTransaction {
                        repo.createOrderEvent(oe)
                    }
                }
                return ar
            } catch (e) {
                LOGGER.error('name=Save_Order_Event_AfterReturning_Failed', e)
                throw e
            }
        }
    }

    private OrderEvent getOpenOrderEvent(JoinPoint jp) {
        if (getOrderActionType(jp) != null) {
            def orderEvent = new OrderEvent()
            orderEvent.order = getOrderId(jp)
            orderEvent.action = getOrderActionType(jp)
            orderEvent.status = EventStatus.OPEN.toString()
            orderEvent.trackingUuid = getTrackingUuid(jp)
            orderEvent.eventTrackingUuid = UUID.randomUUID()
            orderEvent.flowName = getFlowName(jp)
            return orderEvent
        }
        return null
    }

    private OrderEvent getReturnedOrderEvent(JoinPoint jp, ActionResult ar) {
        def orderActionResult = ActionUtils.getOrderActionResult(ar)
        if (orderActionResult != null) {
            EventStatus eventStatus = orderActionResult.returnedEventStatus
            if (eventStatus != null) {
                return getReturnedOrderEvent(jp, eventStatus)
            }
        }
        return null
    }

    private OrderEvent getReturnedOrderEvent(JoinPoint jp, EventStatus eventStatus) {
        def orderEvent = getOpenOrderEvent(jp)
        orderEvent?.status = eventStatus.toString()
        return orderEvent
    }

    private OrderActionType getOrderActionType(JoinPoint jp) {

        def orderEventAwareAction = (BaseOrderEventAwareAction) jp.target
        return orderEventAwareAction?.orderActionType
    }

    private String getFlowName(JoinPoint jp) {
        ActionContext context = getActionContext(jp)
        return ActionUtils.getFlowName(context)
    }

    private UUID getTrackingUuid(JoinPoint jp) {
        def context = getOrderActionContext(jp)
        return context?.trackingUuid
    }

    private OrderId getOrderId(JoinPoint jp) {
        def context = getOrderActionContext(jp)
        return context?.orderServiceContext?.order?.getId()
    }

    @SuppressWarnings('UnnecessaryGetter')
    private ActionContext getActionContext(JoinPoint jp) {
        if (jp == null) {
            return null
        }

        Object[] args = jp.getArgs()
        def actionContext = (ActionContext) args?.find { Object arg ->
            ActionContext.isInstance(arg)
        }

        return actionContext
    }

    private OrderActionContext getOrderActionContext(JoinPoint jp) {
        return ActionUtils.getOrderActionContext(getActionContext(jp))
    }
}
