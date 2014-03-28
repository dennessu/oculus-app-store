package com.junbo.order.core.orderservice
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.*
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
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
}
