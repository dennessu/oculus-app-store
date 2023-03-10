package com.junbo.order.jobs.tax

import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.jobs.OrderProcessResult
import com.junbo.order.jobs.OrderProcessor
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.OrderStatus
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Implementation of AuditTaxProcessor
 */
@CompileStatic
@Component('orderAuditTaxProcessor')
class AuditTaxProcessor implements OrderProcessor {
    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Override
    OrderProcessResult process(Order order) {
        assert order.status == OrderStatus.COMPLETED.name() || order.status == OrderStatus.REFUNDED.name()
        if (order.isAudited) {
            return new OrderProcessResult(success: true)
        }
        def auditedOrder = orderInternalService.auditTax(order).get()
        return new OrderProcessResult(success: auditedOrder.isAudited)
    }
}
