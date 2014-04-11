package com.junbo.order.core.orderservice

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

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
}
