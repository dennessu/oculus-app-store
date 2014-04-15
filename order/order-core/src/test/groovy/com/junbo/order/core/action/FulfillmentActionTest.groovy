package com.junbo.order.core.action
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.core.BaseTest
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.FulfillmentAction
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import com.junbo.order.core.matcher.Matcher
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.FulfillmentEvent
import org.easymock.EasyMock
import org.springframework.test.annotation.Rollback
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by fzhang on 14-3-10.
 */
class FulfillmentActionTest extends BaseTest{

    @Resource(name='fulfillmentAction')
    FulfillmentAction fulfillmentAction

    @BeforeMethod
    void setUp() {
        fulfillmentAction.facadeContainer = new FacadeContainer()
        fulfillmentAction.facadeContainer.fulfillmentFacade = EasyMock.createMock(FulfillmentFacade.class)
        fulfillmentAction.orderRepository = EasyMock.createMock(OrderRepository.class)
    }

    @Test(enabled = true)
    void testExecuteFulfillmentPending() {
        def order = TestBuilder.buildOrderRequest()
        order.id = new OrderId(idGenerator.nextId(OrderId))
        def fulfilmentResult = TestBuilder.buildFulfilmentRequest(order)
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem(FulfilmentStatus.SUCCEED)
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem(FulfilmentStatus.PENDING)

        def fulfillmentEvents = [new FulfillmentEvent(), new FulfillmentEvent()]
        fulfillmentEvents[0].with {
            trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
            action = com.junbo.order.db.entity.enums.FulfillmentAction.FULFILL.toString()
            orderItem = new OrderItemId(fulfilmentResult.items[0].orderItemId)
            status = EventStatus.COMPLETED.name()
            fulfillmentId =  fulfilmentResult.items[0].fulfilmentId
        }
        fulfillmentEvents[1].with {
            trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
            action = com.junbo.order.db.entity.enums.FulfillmentAction.FULFILL.toString()
            orderItem = new OrderItemId(fulfilmentResult.items[1].orderItemId)
            status = EventStatus.PENDING.name()
            fulfillmentId =  fulfilmentResult.items[1].fulfilmentId
        }

        EasyMock.expect(fulfillmentAction.facadeContainer.fulfillmentFacade.postFulfillment(
                EasyMock.same(order))).andReturn(Promise.pure(fulfilmentResult))
        EasyMock.expect(fulfillmentAction.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[0]))).andReturn(null)
        EasyMock.expect(fulfillmentAction.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[1]))).andReturn(null)

        EasyMock.replay(fulfillmentAction.facadeContainer.fulfillmentFacade, fulfillmentAction.orderRepository)

        def actionResult = (OrderActionResult) fulfillmentAction.execute(TestBuilder.buildActionContext(order)).wrapped().
                get().data[ActionUtils.DATA_ORDER_ACTION_RESULT]
        assert actionResult.returnedEventStatus == EventStatus.PENDING
        EasyMock.verify(fulfillmentAction.facadeContainer.fulfillmentFacade, fulfillmentAction.orderRepository)
    }

    @Test(enabled = true)
    @Rollback
    void testExecuteFulfillmentError() {
        def order = TestBuilder.buildOrderRequest()
        order.id = new OrderId(idGenerator.nextId(OrderId))
        EasyMock.expect(fulfillmentAction.facadeContainer.fulfillmentFacade.postFulfillment(EasyMock.same(order))).andReturn(
                Promise.throwing(new IllegalArgumentException())
        )
        EasyMock.replay(fulfillmentAction.facadeContainer.fulfillmentFacade, fulfillmentAction.orderRepository)
        Boolean recovered = false
        fulfillmentAction.execute(TestBuilder.buildActionContext(order)).syncRecover { Throwable ex ->
            assert ex instanceof AppErrorException
            assert ((AppErrorException) ex).error.code == AppErrors.INSTANCE.fulfillmentConnectionError().code
            recovered = true
            return null
        }.syncThen {
            return null
        }.wrapped()?.get()
        assert recovered
        EasyMock.verify(fulfillmentAction.facadeContainer.fulfillmentFacade, fulfillmentAction.orderRepository)
    }
}
