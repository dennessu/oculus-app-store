import com.junbo.common.id.UserId
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.rest.resource.OrderResourceImpl
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.OrderStatus
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by chriszhu on 7/3/14.
 */
class OrderE2ETest extends BaseTest {

    @Resource(name = 'defaultOrderResource')
    OrderResourceImpl orderResource
    @Resource(name = 'mockOrderService')
    OrderServiceImpl orderServiceImpl
    @Resource(name = 'mockCsrLogResource')
    CsrLogResource csrLogResource

    @BeforeMethod
    void setUp() {
        //orderServiceImpl.facadeContainer.billingFacade = EasyMock.createMock(BillingFacade.class)
        //orderServiceImpl.facadeContainer.ratingFacade = EasyMock.createMock(RatingFacade.class)
        orderResource.orderService = orderServiceImpl
        orderResource.csrLogResource = csrLogResource
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
    }
}
