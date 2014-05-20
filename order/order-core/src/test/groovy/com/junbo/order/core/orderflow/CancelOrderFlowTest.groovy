package com.junbo.order.core.orderflow

import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.BaseTest
import com.junbo.order.core.OrderService
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.repo.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by chriszhu on 5/19/14.
 */
class CancelOrderFlowTest extends BaseTest {

    @Autowired
    FlowExecutor executor
    @Autowired
    @Qualifier('mockOrderService')
    OrderService orderService
    @Autowired
    OrderRepository repo
    @Resource(name = 'mockFacadeContainer')
    FacadeContainer facadeContainer
    @Resource(name = 'mockOrderInternalService')
    OrderInternalService orderInternalService

    @Test(enabled = true)
    void testFlowExecute() {
        def order = TestBuilder.buildOrderRequest()
        order.user = new UserId(idGenerator.nextId(UserId.class))
        order.id = new OrderId(idGenerator.nextId(OrderId.class))
        order.status = OrderStatus.OPEN.name()
        order.tentative = false
        Map<String, Object> requestScope = [:]
        requestScope.put(ActionUtils.REQUEST_FLOW_NAME, (Object) 'MOCK_CANCEL_ORDER')
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderServiceContext = new OrderServiceContext(order)
        orderActionContext.trackingUuid = UUID.randomUUID()
        requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object)orderActionContext)

        def ratedOrder = orderInternalService.rateOrder(order).wrapped().get()
        repo.createOrder(ratedOrder)

        def context = executor.start(
                'MOCK_CANCEL_ORDER',
                requestScope).wrapped().get()
        // Check the order is same as the returned order
        def o = ActionUtils.getOrderActionContext(context).orderServiceContext.order
        assert (o != null)

        //mock balance
        def balance = CoreBuilder.buildBalance(order)
        facadeContainer.billingFacade.createBalance(balance, false)

        def getOrder = orderService.getOrderByOrderId(o.id.value).wrapped().get()
        assert (o.id.value == getOrder.id.value)
        assert (o.status == OrderStatus.CANCELED.name())
    }
}
