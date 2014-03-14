package com.junbo.order.core.orderflow

import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.core.BaseTest
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.db.repo.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
/**
 * Created by chriszhu on 3/13/14.
 */
class RateOrderFlowTest extends BaseTest{

    @Autowired
    FlowExecutor executor

    @Autowired
    OrderRepository repo

    @Test(enabled = false)
    void testFlowExecute() {
        def order = TestBuilder.buildOrderRequest()

        Map<String, Object> requestScope = [:]
        requestScope.put(ActionUtils.REQUEST_FLOW_TYPE, (Object)'MOCK_RATE_ORDER')

        executor.start(
                'MOCK_RATE_ORDER',
                ActionUtils.initRequestScope(
                new OrderServiceContext(order),
                requestScope)).syncThen { ActionContext context ->
            // Check the order is same as the returned order
            def o = ActionUtils.getOrderActionContext(context).orderServiceContext.order
            assert (o != null)
            def getOrder = repo.getOrder(o.id.value)
            assert (o == getOrder)
        }.wrapped().get()
    }
}
