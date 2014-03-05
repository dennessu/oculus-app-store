package com.junbo.order.core.orderflow
import com.junbo.order.core.BaseTest
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderflow.DefaultFlowSelector
import com.junbo.order.core.impl.orderflow.FreeSettleFlow
import com.junbo.order.core.impl.orderflow.ImmediateSettleFlow
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class FlowSelectorTest extends BaseTest {

    @Autowired
    OrderService orderService

    @Test
    void testSelector_CREATE_PayIn_CreditCard_Digital() {
        def order = TestBuilder.buildOrderRequest()
        orderService.expandOrder(order).syncThen { OrderServiceContext expandedOrder ->
            def flow = new ImmediateSettleFlow()
            assert(new DefaultFlowSelector().select(expandedOrder,
                    OrderServiceOperation.CREATE).name == flow.name)
        }
    }

    @Test
    void testSelector_CREATE_PayIn_NoPI_Digital() {

        def order = TestBuilder.buildOrderRequest()
        order.setPaymentInstruments(null)
        def expandedOrder = orderService.expandOrder(order).syncThen { OrderServiceContext expandedOrder ->
            def flow = new FreeSettleFlow()
            assert(new DefaultFlowSelector().select(expandedOrder,
                    OrderServiceOperation.CREATE).name == flow.name)
        }
    }

}
