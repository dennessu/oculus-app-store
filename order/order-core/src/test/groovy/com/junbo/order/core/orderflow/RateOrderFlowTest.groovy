package com.junbo.order.core.orderflow
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.core.BaseTest
import com.junbo.order.core.OrderService
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Discount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test
/**
 * Created by chriszhu on 3/13/14.
 */
class RateOrderFlowTest extends BaseTest{

    @Autowired
    FlowExecutor executor
    @Autowired
    @Qualifier('mockOrderService')
    OrderService orderService
    @Autowired
    OrderRepository repo

    @Test(enabled = true)
    void testFlowExecute() {
        def order = TestBuilder.buildOrderRequest()

        Map<String, Object> requestScope = [:]
        requestScope.put(ActionUtils.REQUEST_FLOW_TYPE, (Object) 'MOCK_RATE_ORDER')
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderActionType = OrderActionType.RATE
        orderActionContext.orderServiceContext = new OrderServiceContext(order)
        orderActionContext.trackingUuid = order.trackingUuid
        requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object)orderActionContext)

        def context = executor.start(
                'MOCK_RATE_ORDER',
                requestScope).wrapped().get()
        // Check the order is same as the returned order
        def o = ActionUtils.getOrderActionContext(context).orderServiceContext.order
        assert (o != null)
        o.discounts?.each { Discount d ->
            d.ownerOrderItem = null
        }
        def getOrder = orderService.getOrderByOrderId(o.id.value).wrapped().get()
        assert (o.id.value == getOrder.id.value)
        assert (o.discounts.size() == getOrder.discounts.size())
        assert (o.orderItems.size() == getOrder.orderItems.size())
        assert (o.totalAmount == getOrder.totalAmount)
        assert (o.totalTax == getOrder.totalTax)
    }
}
