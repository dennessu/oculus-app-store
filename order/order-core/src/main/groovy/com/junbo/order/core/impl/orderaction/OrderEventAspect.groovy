package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
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

        Object[] args = jp.args()
        ActionContext actionContext = (ActionContext) args.find { Object arg ->
            ActionContext.isInstance(arg)
        }

        // Create Open Order Event
        // Save Order Eventpoint
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderId = context?.orderServiceContext?.order?.id
        if (orderId == null) {
            // TODO add log: do not need to create order event if the order is not saved yet.
            return
        }

        def orderEvent = new OrderEvent()
        orderEvent.order = orderId
        orderEvent.action = context.orderActionType.toString()
        orderEvent.status = EventStatus.OPEN.toString()
        repo.createOrderEvent(orderEvent, context.flowType.toString(), context.trackingUuid)
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
        // Create Open Order Event
        // Save Order Event
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderId = context?.orderServiceContext?.order?.id
        if (orderId == null) {
            // TODO add log: do not need to create order event if the order is not saved yet.
            return
        }

        result.syncThen { ActionResult ar ->
            def orderActionResult = ActionUtils.getOrderActionResult(actionContext)
            if (orderActionResult == null) {
                return Promise.pure(ar)
            }
            EventStatus eventStatus = orderActionResult.returnedEventStatus
            if (eventStatus != null) {
                def orderEvent = new OrderEvent()
                orderEvent.order = orderId
                orderEvent.action = context.orderActionType.toString()
                orderEvent.status = eventStatus.toString()
                repo.createOrderEvent(orderEvent, context.flowType.toString(), context.trackingUuid)
            }
            return Promise.pure(ar)
        }
    }
}
