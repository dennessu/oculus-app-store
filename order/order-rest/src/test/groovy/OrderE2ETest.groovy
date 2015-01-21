import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.rest.resource.OrderResourceImpl
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.springframework.aop.framework.Advised
import org.springframework.aop.support.AopUtils
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by chriszhu on 7/3/14.
 */
@CompileStatic
class OrderE2ETest extends BaseTest {

    @Resource(name = 'defaultOrderResource')
    OrderResource orderResource
    @Resource(name = 'mockOrderService')
    OrderService orderServiceImpl
    @Resource(name = 'mockCsrLogResource')
    CsrLogResource csrLogResource

    @BeforeMethod
    void setUp() {
        //orderServiceImpl.facadeContainer.billingFacade = EasyMock.createMock(BillingFacade.class)
        //orderServiceImpl.facadeContainer.ratingFacade = EasyMock.createMock(RatingFacade.class)
        getTargetObject(orderResource, OrderResourceImpl).orderService = orderServiceImpl
        getTargetObject(orderResource, OrderResourceImpl).csrLogResource = csrLogResource
    }

    @Test(enabled = true)
    Order testPostTentativeOrder() {
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_RATE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.user = new UserId(idGenerator.nextId(UserId))

        def orderResult = orderResource.createOrder(order).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.OPEN.name()
        assert orderResult.tentative
        assert orderGet.status == OrderStatus.OPEN.name()
        assert orderGet.tentative
        return orderGet
    }

    @Test(enabled = true)
    void testPostFreeOrder() {
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_FREE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.user = new UserId(idGenerator.nextId(UserId))
        order.tentative = false
        order.shippingMethod = 'free'
        order.shippingAddress = null
        order.shippingToName = null
        order.shippingToPhone = null

        def orderResult = orderResource.createOrder(order).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.COMPLETED.name()
        assert !orderResult.tentative
        assert orderGet.status == OrderStatus.COMPLETED.name()
        assert !orderGet.tentative
        assert order.getId() != null
    }

    @Test(enabled = true)
    Order testPutTentativeOrder() {
        def tentativeOrder = testPostTentativeOrder()
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_IMMEDIATE_SETTLE')
            }
        }
        tentativeOrder.tentative = false
        JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                requestIpAddress: '127.0.0.1'
        )
        def orderResult = orderResource.updateOrderByOrderId(tentativeOrder.getId(), tentativeOrder).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.COMPLETED.name()
        assert !orderResult.tentative
        assert orderGet.status == OrderStatus.COMPLETED.name()
        assert !orderGet.tentative
        return orderResult
    }

    @Test(enabled = true)
    void testPutTentativeFreeOrder() {
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_RATE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        order.payments = null
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.user = new UserId(idGenerator.nextId(UserId))
        order.shippingMethod = 'free'

        def orderResult = orderResource.createOrder(order).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.OPEN.name()
        assert orderResult.tentative
        assert orderGet.status == OrderStatus.OPEN.name()
        assert orderGet.tentative

        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_FREE_SETTLE')
            }
        }
        orderGet.tentative = false
        JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                requestIpAddress: '127.0.0.1'
        )
        orderGet.payments = null
        orderResult = orderResource.updateOrderByOrderId(orderGet.getId(), orderGet).get()
        orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.COMPLETED.name()
        assert !orderResult.tentative
        assert orderGet.status == OrderStatus.COMPLETED.name()
        assert !orderGet.tentative
    }

    @Test(enabled = true)
    void testRefundOrder() {
        def order = testPutTentativeOrder()
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_REFUND_ORDER')
            }
        }
        order.orderItems = null
        def orderResult = orderResource.updateOrderByOrderId(order.getId(), order).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.REFUNDED.name()
        assert !orderResult.tentative
        assert orderGet.status == OrderStatus.REFUNDED.name()
        assert !orderGet.tentative
    }

    @Test(enabled = true)
    void testPostTooManyItems() {
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_FREE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        for(i in 0..<6) {
            order.orderItems.add(TestBuilder.buildOrderItem())
        }

        order.user = new UserId(idGenerator.nextId(UserId))
        order.tentative = false
        order.shippingMethod = 'free'
        order.shippingAddress = null
        order.shippingToName = null
        order.shippingToPhone = null

        try {
            def orderResult = orderResource.createOrder(order).get()
        } catch(e) {
            assert ((AppErrorException)e).error.error().code == '199.149'
            return
        }
        assert false
    }

    @Test(enabled = true)
    void testPostTooManyOffers() {
        getTargetObject(orderServiceImpl, OrderServiceImpl).flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_FREE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        for(i in 0..<4) {
            order.orderItems.add(TestBuilder.buildOrderItem())
        }
        order.orderItems.addAll(order.orderItems)

        order.user = new UserId(idGenerator.nextId(UserId))
        order.tentative = false
        order.shippingMethod = 'free'
        order.shippingAddress = null
        order.shippingToName = null
        order.shippingToPhone = null

        try {
            def orderResult = orderResource.createOrder(order).get()
        } catch(e) {
            assert ((AppErrorException)e).error.error().code == '199.150'
            return
        }
        assert false
    }

    @SuppressWarnings("unchecked")
    private <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return (T) proxy.asType(Advised).getTargetSource().getTarget();
        } else {
            return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
        }
    }
}
