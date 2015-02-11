import com.junbo.authorization.AuthorizeCallback
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.order.auth.OrderAuthorizeCallback
import com.junbo.order.auth.OrderAuthorizeCallbackFactory
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.*
import com.junbo.order.spec.resource.OrderEventResource
import com.junbo.order.spec.resource.OrderResource
import org.springframework.beans.factory.annotation.Required
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by chriszhu on 7/3/14.
 */
class OrderE2ETest extends BaseTest {

    @Resource(name = 'defaultOrderResource')
    OrderResource orderResource
    @Resource(name = 'defaultOrderEventResource')
    OrderEventResource orderEventResource
    @Resource(name = 'mockOrderService')
    OrderService orderServiceImpl
    @Resource(name = 'mockOrderEventService')
    OrderEventService orderEventServiceImpl
    @Resource(name = 'mockCsrLogResource')
    CsrLogResource csrLogResource
    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    class TestOrderAuthorizeCallbackFactory extends OrderAuthorizeCallbackFactory {

        @Required
        void setOrderResource( OrderResource orderResource) {
            this.orderResource = orderResource
        }

        @Override
        AuthorizeCallback<Order> create(Order entity) {
            return new OrderAuthorizeCallback(this, entity)
        }

        AuthorizeCallback<Order> create(UserId userId) {
            return create(new Order(user: userId))
        }

        AuthorizeCallback<Order> create(OrderId orderId) {
            return create(new Order())
        }
    }

    @BeforeMethod
    void setUp() {
        //orderServiceImpl.facadeContainer.billingFacade = EasyMock.createMock(BillingFacade.class)
        //orderServiceImpl.facadeContainer.ratingFacade = EasyMock.createMock(RatingFacade.class)
        orderResource.orderService = orderServiceImpl
        orderResource.csrLogResource = csrLogResource
        orderEventResource.orderService = orderServiceImpl
        orderEventResource.orderEventService = orderEventServiceImpl
        orderEventResource.authorizeCallbackFactory = new TestOrderAuthorizeCallbackFactory()
    }

    @Test(enabled = true)
    Order testPostTentativeOrder() {
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderServiceImpl.flowSelector = new FlowSelector() {
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

        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderGet.orderItems.each { OrderItem oi ->
            assert oi.fulfillmentHistories[0].fulfillmentEvent == FulfillmentEventType.REVERSE_FULFILL.name()
            assert oi.fulfillmentHistories[0].success
            assert oi.fulfillmentHistories[1].fulfillmentEvent == FulfillmentEventType.FULFILL.name()
            assert oi.fulfillmentHistories[1].success
        }
    }

    @Test(enabled = true)
    void testUpdateRefundStatus() {
        def orderGet
        transactionHelper.executeInNewTransaction {
            def order = testPutTentativeOrder()
            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_REFUND_ORDER')
                }
            }
            order.orderItems = null
            def orderResult = orderResource.updateOrderByOrderId(order.getId(), order).get()
            orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_UPDATE_REFUND')
                }
            }
        }
        transactionHelper.executeInNewTransaction {
            def orderEvent = new OrderEvent(
                    order: orderGet.getId(),
                    action: OrderActionType.REFUND.name(),
                    status: EventStatus.COMPLETED.name()
            )
            def oe = orderEventResource.createOrderEvent(orderEvent).get()
            orderGet = orderResource.getOrderByOrderId(orderGet.getId()).get()
            assert orderGet.billingHistories[0].billingEvent == BillingAction.REFUND.name()
        }
        transactionHelper.executeInNewTransaction {
            def orderEvent = new OrderEvent(
                    order: orderGet.getId(),
                    action: OrderActionType.REFUND.name(),
                    status: EventStatus.COMPLETED.name()
            )
            def oe = orderEventResource.createOrderEvent(orderEvent).get()
            orderGet = orderResource.getOrderByOrderId(orderGet.getId()).get()
            assert orderGet.billingHistories[0].billingEvent == BillingAction.REFUND.name()
        }
    }

    @Test(enabled = true)
    void testRefundOrderChangedPayload() {
        def order = testPutTentativeOrder()
        orderServiceImpl.flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_REFUND_ORDER')
            }
        }
        order.orderItems = null
        order.billingHistories = null
        order.payments = null
        def orderResult = orderResource.updateOrderByOrderId(order.getId(), order).get()
        def orderGet = orderResource.getOrderByOrderId(orderResult.getId()).get()

        assert orderResult.status == OrderStatus.REFUNDED.name()
        assert !orderResult.tentative
        assert orderGet.status == OrderStatus.REFUNDED.name()
        assert !orderGet.tentative
        orderGet.orderItems.each { OrderItem oi ->
            assert oi.fulfillmentHistories[0].fulfillmentEvent == FulfillmentEventType.REVERSE_FULFILL.name()
            assert oi.fulfillmentHistories[0].success
            assert oi.fulfillmentHistories[1].fulfillmentEvent == FulfillmentEventType.FULFILL.name()
            assert oi.fulfillmentHistories[1].success
        }
    }

    @Test(enabled = true)
    void testBillingException() {
        Order tentativeOrder
        transactionHelper.executeInNewTransaction {
            tentativeOrder = testPostTentativeOrder()
            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_IMMEDIATE_SETTLE')
                }
            }
            tentativeOrder.tentative = false
            JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                    requestIpAddress: '127.0.0.1'
            )
            tentativeOrder.payments[0].cancelRedirectUrl = 'exception'
        }
        transactionHelper.executeInNewTransaction {
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), tentativeOrder).get()
            } catch (ex) {
                assert ex != null
                assert ((AppErrorException)ex).error.error().code == '199.116'
            }
        }
        transactionHelper.executeInNewTransaction {
            def orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.ERROR.name()
            assert !orderGet.tentative
            orderGet.payments[0].cancelRedirectUrl = 'aaa'
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), orderGet).get()
            } catch (ex) {}
            orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.ERROR.name()
            assert !orderGet.tentative
        }
    }

    @Test(enabled = true)
    void testBillingAppErrorException() {
        Order tentativeOrder
        transactionHelper.executeInNewTransaction {
            tentativeOrder = testPostTentativeOrder()
            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_IMMEDIATE_SETTLE')
                }
            }
            tentativeOrder.tentative = false
            JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                    requestIpAddress: '127.0.0.1'
            )
            tentativeOrder.payments[0].cancelRedirectUrl = 'app500'
        }
        transactionHelper.executeInNewTransaction {
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), tentativeOrder).get()
            } catch (ex) {
                assert ex != null
                assert ((AppErrorException)ex).error.error().code == '199.116'
            }
        }
        transactionHelper.executeInNewTransaction {
            def orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.ERROR.name()
            assert !orderGet.tentative
            orderGet.payments[0].cancelRedirectUrl = 'aaa'
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), orderGet).get()
            } catch (ex) {}
            orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.ERROR.name()
            assert !orderGet.tentative
        }
    }

    @Test(enabled = true)
    void testBillingDecline() {
        Order tentativeOrder
        transactionHelper.executeInNewTransaction {
            tentativeOrder = testPostTentativeOrder()
            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_IMMEDIATE_SETTLE')
                }
            }
            tentativeOrder.tentative = false
            JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                    requestIpAddress: '127.0.0.1'
            )
            tentativeOrder.payments[0].cancelRedirectUrl = 'decline'
        }
        transactionHelper.executeInNewTransaction {
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), tentativeOrder).get()
            } catch (ex) {
                assert ex != null
                assert ((AppErrorException)ex).error.error().code == '199.119'
            }
        }
        transactionHelper.executeInNewTransaction {
            def orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.OPEN.name()
            assert orderGet.tentative
            assert orderGet.billingHistories.any { BillingHistory bh ->
                bh.billingEvent == BillingAction.CHARGE.name() && !bh.success
            }
            orderGet.payments[0].cancelRedirectUrl = 'aaa'
            def orderComp = orderResource.updateOrderByOrderId(tentativeOrder.getId(), orderGet).get()
            orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.COMPLETED.name()
            assert !orderGet.tentative
            assert orderComp.status == OrderStatus.COMPLETED.name()
            assert !orderComp.tentative
        }
    }

    @Test(enabled = true)
    void testBillingDeclineException() {
        Order tentativeOrder
        transactionHelper.executeInNewTransaction {
            tentativeOrder = testPostTentativeOrder()
            orderServiceImpl.flowSelector = new FlowSelector() {
                @Override
                Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                    return Promise.pure('MOCK_IMMEDIATE_SETTLE')
                }
            }
            tentativeOrder.tentative = false
            JunboHttpContext.data = new JunboHttpContext.JunboHttpContextData(
                    requestIpAddress: '127.0.0.1'
            )
            tentativeOrder.payments[0].cancelRedirectUrl = 'app400'
        }
        transactionHelper.executeInNewTransaction {
            try {
                orderResource.updateOrderByOrderId(tentativeOrder.getId(), tentativeOrder).get()
            } catch (ex) {
                assert ex != null
                assert ((AppErrorException)ex).error.getHttpStatusCode() / 100 as int == 4
            }
        }
        transactionHelper.executeInNewTransaction {
            def orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.OPEN.name()
            assert orderGet.tentative
            orderGet.payments[0].cancelRedirectUrl = 'aaa'
            def orderComp = orderResource.updateOrderByOrderId(tentativeOrder.getId(), orderGet).get()
            orderGet = orderResource.getOrderByOrderId(tentativeOrder.getId()).get()
            assert orderGet.status == OrderStatus.COMPLETED.name()
            assert !orderGet.tentative
            assert orderComp.status == OrderStatus.COMPLETED.name()
            assert !orderComp.tentative
        }
    }

    @Test(enabled = true)
    void testPostTooManyItems() {
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        orderServiceImpl.flowSelector = new FlowSelector() {
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

    @Test(enabled = true)
    void testGetByUserId() {
        def user = new UserId(idGenerator.nextId(UserId))
        def order1 = chargeOrder(user)
        def order2 = chargeOrder(user)
        def getOrders = orderResource.getOrderByUserId(user, new OrderQueryParam(), null).get()
        assert getOrders.items.size() == 2
        def eid1 = getOrders.items[0].orderItems[0].fulfillmentHistories[0].entitlements[0].value
        def eid2 = getOrders.items[1].orderItems[0].fulfillmentHistories[0].entitlements[0].value
        assert eid1 != eid2
    }

    private Order chargeOrder(UserId userId) {
        orderServiceImpl.flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_RATE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.user = userId
        def tentativeOrder = orderResource.createOrder(order).get()
        orderServiceImpl.flowSelector = new FlowSelector() {
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
        return orderResult
    }
}
