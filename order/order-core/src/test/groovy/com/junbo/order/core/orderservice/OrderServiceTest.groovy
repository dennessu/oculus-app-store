package com.junbo.order.core.orderservice

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderFlow
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
/**
 * Created by chriszhu on 2/14/14.
 */
@CompileStatic
@TypeChecked
class OrderServiceTest extends BaseTest {

    @Autowired
    OrderServiceImpl orderService

    FlowSelector flowSelector

    OrderFlow orderFlow

    @BeforeMethod
    void setUp() {

        orderFlow = new OrderFlow() {
            @Override
            UUID getName() {
                return UUID.randomUUID()
            }

            @Override
            Promise<List<Order>> execute(OrderServiceContext order) {
                return Promise.pure([new Order()])
            }
        }

        flowSelector = new FlowSelector() {
            @Override
            Promise<FlowType> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure(FlowType.FREE_SETTLE)
            }
        }

        orderService.flowSelector = flowSelector
    }

    @Test(enabled = false)
    void createOrderTest() {

        def order = TestBuilder.buildOrderRequest()


        def createOrderResult = orderService.createOrder(order, null)
//        def getOrderResult = orderService.getOrderByOrderId(TestHelper.generateLong())

        assert(createOrderResult != null)
//        assert(createOrderResult[0] != getOrderResult)
    }

    @Test(enabled = false)
    void createTentativeOrderTest() {
        def order = TestBuilder.buildOrderRequest()
        order.tentative = true
        def createOrderResult = orderService.createOrder(order, null)
//        def getOrderResult = orderService.getOrderByOrderId(TestHelper.generateLong())
        assert(createOrderResult != null)
//        assert(createOrderResult[0] != getOrderResult)
    }

}
