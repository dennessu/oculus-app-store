package com.junbo.order.jobs
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.resource.proxy.OrderEventResourceClientProxy
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 4/18/2014.
 */
@CompileStatic
@Component('orderChargeProcessor')
class OrderChargeProcessor implements OrderProcessor {

    OrderEventResourceClientProxy orderEventResourceClientProxy

    @Override
    OrderProcessResult process(Order order) {
        assert CoreUtils.isPendingOnChargeConfirmation(order)
        // todo post order event with charge action
        return new OrderProcessResult(success: true)
    }
}
