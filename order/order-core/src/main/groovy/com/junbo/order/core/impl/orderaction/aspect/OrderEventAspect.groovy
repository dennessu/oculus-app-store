package com.junbo.order.core.impl.orderaction.aspect
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.BaseOrderEventAwareAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.OrderEvent
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 3/11/14.
 */
@Aspect
@Component('orderEventAspect')
class OrderEventAspect {

    @Resource(name = 'orderRepository')
    OrderRepository repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Pointcut(value = '@annotation(com.junbo.order.core.annotation.OrderEventAwareBefore)')
    void orderEventAwareBeforeCut() { return }

    @Pointcut(value = '@annotation(com.junbo.order.core.annotation.OrderEventAwareAfter)')
    void orderEventAwareAfterCut() { return }

    @Transactional
    @Before(value = 'orderEventAwareBeforeCut()')
    void beforeOrderEventAwareAction(JoinPoint jp) {

        def orderEvent = getOpenOrderEvent(jp)
        if (orderEvent == null) {
            return
        }
        repo.createOrderEvent(orderEvent)
    }

    @Transactional
    @AfterReturning(value = 'orderEventAwareAfterCut()', argNames = 'rv', returning = 'rv')
    Promise<ActionResult> afterOrderEventAwareAction(JoinPoint jp, Object rv) {
        if (rv == null || !Promise.isInstance(rv)) {
            // TODO log
            return
        }
        def result = (Promise<ActionResult>)rv

        Object[] args = jp.args()
        ActionContext actionContext = (ActionContext) args.find { Object arg ->
            ActionContext.isInstance(arg)
        }

        result.syncThen { ActionResult ar ->
            def orderActionResult = ActionUtils.getOrderActionResult(actionContext)
            if (orderActionResult == null) {
                return Promise.pure(ar)
            }
            EventStatus eventStatus = orderActionResult.returnedEventStatus
            if (eventStatus != null) {
                repo.createOrderEvent(getReturnedOrderEvent(jp, eventStatus.toString()))
            }
            return Promise.pure(ar)
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

    private getReturnedOrderEvent(JoinPoint jp, EventStatus eventStatus) {
        def orderEvent = getOpenOrderEvent(jp)
        orderEvent?.status = eventStatus
        return orderEvent
    }

    private OrderActionType getOrderActionType(JoinPoint jp) {
        def orderEventAwareAction = (BaseOrderEventAwareAction)jp.this
        return orderEventAwareAction?.orderActionType
    }

    private String getFlowType(JoinPoint jp) {
        def context = getOrderActionContext(jp)
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

    private OrderActionContext getOrderActionContext(JoinPoint jp) {
        if (jp == null) {
            return null
        }

        Object[] args = jp.args()
        def actionContext = (ActionContext) args?.find { Object arg ->
            ActionContext.isInstance(arg)
        }

        return ActionUtils.getOrderActionContext(actionContext)
    }
}
