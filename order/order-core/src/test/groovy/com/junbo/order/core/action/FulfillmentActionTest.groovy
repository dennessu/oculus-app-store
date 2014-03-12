package com.junbo.order.core.action

import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.orderaction.FulfillmentAction
import com.junbo.order.core.matcher.Matcher
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.FulfillmentEvent
import org.easymock.EasyMock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-3-10.
 */
class FulfillmentActionTest {

    def action = new FulfillmentAction()

    @BeforeMethod
    void setUp() {
        action.fulfillmentFacade = EasyMock.createMock(FulfillmentFacade.class)
        action.orderRepository = EasyMock.createMock(OrderRepository.class)
    }

    @Test(enabled = false)
    void testExecuteFulfillmentPending() {
        def order = TestBuilder.buildOrderRequest()
        order.id = new OrderId(TestBuilder.generateLong())
        def fulfilmentResult = TestBuilder.buildFulfilmentRequest(order)
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem()
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem(FulfilmentStatus.PENDING)

        def orderEvent = TestBuilder.buildOrderEvent(order.id, OrderActionType.FULFILL, EventStatus.PENDING)

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

        EasyMock.expect(action.fulfillmentFacade.postFulfillment(
                EasyMock.same(order))).andReturn(Promise.pure(fulfilmentResult))
        EasyMock.expect(action.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[0]))).andReturn(null)
        EasyMock.expect(action.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[1]))).andReturn(null)
        //EasyMock.expect(action.orderRepository.createOrderEvent(
        //        Matcher.memberEquals(orderEvent), OrderActionType.FULFILL, EventStatus.PENDING)).andReturn(null)

        EasyMock.replay(action.fulfillmentFacade, action.orderRepository)

        action.execute(TestBuilder.buildActionContext(order)).wrapped().get()
        EasyMock.verify(action.fulfillmentFacade, action.orderRepository)
    }

    @Test(enabled = false)
    void testExecuteFulfillmentError() {
        def order = TestBuilder.buildOrderRequest()
        EasyMock.expect(action.fulfillmentFacade.postFulfillment(EasyMock.same(order))).andReturn(
            Promise.throwing(new IllegalArgumentException())
        )
        def orderEvent = TestBuilder.buildOrderEvent(order.id, OrderActionType.FULFILL, EventStatus.ERROR)
        //EasyMock.expect(action.orderRepository.createOrderEvent(Matcher.memberEquals(orderEvent))).andReturn(null)
        EasyMock.replay(action.fulfillmentFacade, action.orderRepository)
        action.execute(TestBuilder.buildActionContext(order)).wrapped().get()
        EasyMock.verify(action.fulfillmentFacade, action.orderRepository)
    }
}
