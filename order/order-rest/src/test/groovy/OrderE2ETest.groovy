import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceImpl
import com.junbo.order.rest.resource.OrderResourceImpl
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

    @BeforeMethod
    void setUp() {
        //orderServiceImpl.facadeContainer.billingFacade = EasyMock.createMock(BillingFacade.class)
        //orderServiceImpl.facadeContainer.ratingFacade = EasyMock.createMock(RatingFacade.class)
        orderResource.orderService = orderServiceImpl
    }

    @Test(enabled = true)
    void testPostTentativeOrder() {
        orderServiceImpl.flowSelector = new FlowSelector() {
            @Override
            Promise<String> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
                return Promise.pure('MOCK_RATE_ORDER')
            }
        }
        def order = TestBuilder.buildOrderRequest()
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.user = new UserId(idGenerator.nextId(UserId))

        //Balance balance = TestBuilder.buildBalanceWithItems(order, BalanceType.DEBIT)
        //RatingRequest ratingRequest = TestBuilder.buildRatingRequest(order)

        //Balance taxedBalance = TestBuilder.buildTaxedBalance(balance)
//        EasyMock.expect(orderServiceImpl.flowSelector.select(
//                EasyMock.isA(OrderServiceContext.class), EasyMock.isA(OrderServiceOperation.class))).andReturn(
//                Promise.pure('MOCK_RATE_ORDER'))
 //       EasyMock.expect(((OrderServiceImpl)(orderResource.orderService)).facadeContainer.ratingFacade.rateOrder(
 //               EasyMock.same(order))).andReturn(Promise.pure(ratingRequest))
//        EasyMock.expect(fulfillmentAction.orderRepository.createFulfillmentHistory(
//                Matcher.memberEquals(fulfillmentHistories[0]))).andReturn(null)
//        EasyMock.expect(fulfillmentAction.orderRepository.createFulfillmentHistory(
//                Matcher.memberEquals(fulfillmentHistories[1]))).andReturn(null)

        //EasyMock.replay(orderServiceImpl.flowSelector)

        def orderResult = orderResource.createOrder(order).get()

        assert orderResult.status == OrderStatus.OPEN.name()
        //EasyMock.verify(orderServiceImpl.flowSelector)
    }
}
