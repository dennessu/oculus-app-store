package com.junbo.order.core.impl.orderaction.aspect
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.BaseOrderEventAwareAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 3/11/14.
 */
@Aspect
@Component('orderEventAspect')
@CompileStatic
class OrderEventAspect {

    @Resource(name = 'orderRepository')
    OrderRepository repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderEventAspect)

    @Transactional
    @Before(value = '@annotation(orderEventAwareBefore)', argNames = 'jp, orderEventAwareBefore')
    Promise<ActionResult> beforeOrderEventAwareAction(JoinPoint jp, OrderEventAwareBefore orderEventAwareBefore) {
        assert(orderEventAwareBefore != null)
        LOGGER.info('name=Save_Order_Event_Before. action: {}', orderEventAwareBefore.action())
        try {
            def orderEvent = getOpenOrderEvent(jp)
            if (orderEvent != null && orderEvent.order != null) {
                repo.createOrderEvent(orderEvent)
            }
            return Promise.pure(null)
        } catch (e) {
            LOGGER.error('name=Save_Order_Event_Before', e)
            throw e
        }
    }

    @Transactional
    @AfterReturning(value = '@annotation(orderEventAwareAfter)',
            argNames = 'jp, orderEventAwareAfter, rv', returning = 'rv')
    Promise<ActionResult> afterOrderEventAwareAction(
            JoinPoint jp,
            OrderEventAwareAfter orderEventAwareAfter,
            Promise<ActionResult> rv) {
        assert (orderEventAwareAfter != null)
        LOGGER.info('name=Save_Order_Event_AfterReturning. action: {}', orderEventAwareAfter.action())
        rv?.syncThen { ActionResult ar ->
            try {
                def orderActionResult = ActionUtils.getOrderActionResult(ar)
                if (orderActionResult != null) {
                    EventStatus eventStatus = orderActionResult.returnedEventStatus
                    if (eventStatus != null) {
                        def oe = getReturnedOrderEvent(jp, eventStatus)
                        repo.createOrderEvent(oe)
                    }
                }
                return ar
            } catch (e) {
                LOGGER.error('name=Save_Order_Event_AfterReturning', e)
                throw e
            }
        }
    }

    private OrderEvent getOpenOrderEvent(JoinPoint jp) {
        def orderEvent = new OrderEvent()
        orderEvent.order = getOrderId(jp)
        orderEvent.action = getOrderActionType(jp)
        orderEvent.status = EventStatus.OPEN.toString()
        orderEvent.trackingUuid = getTrackingUuid(jp)
        orderEvent.flowType = getFlowType(jp)
        return orderEvent
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

    private String getFlowType(JoinPoint jp) {
        ActionContext context = getActionContext(jp)
        return ActionUtils.getFlowType(context)
    }

    private UUID getTrackingUuid(JoinPoint jp) {
        def context = getOrderActionContext(jp)
        return context?.trackingUuid
    }

    private OrderId getOrderId(JoinPoint jp) {
        def context = getOrderActionContext(jp)
        return context?.orderServiceContext?.order?.id
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
