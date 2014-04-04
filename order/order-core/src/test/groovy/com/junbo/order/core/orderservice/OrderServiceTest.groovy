package com.junbo.order.core.orderservice
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/14/14.
 */
@CompileStatic
@TypeChecked
class OrderServiceTest extends BaseTest {

    @Resource(name = 'mockOrderService')
    OrderServiceImpl orderService

    FlowSelector flowSelector

    private void setupFlow(String flowName) {

        flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure(flowName)
            }
        }

        orderService.flowSelector = flowSelector
    }

    @Test
    void testGetOrderByTrackingUuid() {
        setupFlow('MOCK_RATE_ORDER')
        def request1 = TestBuilder.buildOrderRequest()
        def order1 = orderService.createQuote(request1, null).wrapped().get()
        def request2 = TestBuilder.buildOrderRequest()
        request2.user = request1.user
        request2.trackingUuid = request1.trackingUuid
        def order2 = orderService.createQuote(request2, null).wrapped().get()
        assert order1.trackingUuid == order2.trackingUuid
        assert order1.user == order2.user
        assert order1.createdTime == order2.createdTime

        def request3 = TestBuilder.buildOrderRequest()
        request3.trackingUuid = request1.trackingUuid
        try {
            orderService.createQuote(request3, null).wrapped().get()
            assert false
        } catch (AppErrorException e) {
            assert e.error.code == AppErrors.INSTANCE.orderDuplicateTrackingGuid().code
        }
    }
}
