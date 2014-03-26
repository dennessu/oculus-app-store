package com.junbo.order.core.action
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
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
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by fzhang on 14-3-10.
 */
class FulfillmentActionTest extends BaseTest{

    //def action = new FulfillmentAction()

    @Autowired
    FlowExecutor executor

    @Resource(name='fulfillmentAction')
    FulfillmentAction action

    @BeforeMethod
    void setUp() {
        action.facadeContainer = new FacadeContainer()
        action.facadeContainer.fulfillmentFacade = EasyMock.createMock(FulfillmentFacade.class)
        action.orderRepository = EasyMock.createMock(OrderRepository.class)
    }

    @Test(enabled = true)
    void testExecuteFulfillmentPending() {
        def order = TestBuilder.buildOrderRequest()
        order.id = new OrderId(TestBuilder.generateLong())
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

        EasyMock.expect(action.facadeContainer.fulfillmentFacade.postFulfillment(
                EasyMock.same(order))).andReturn(Promise.pure(fulfilmentResult))
        EasyMock.expect(action.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[0]))).andReturn(null)
        EasyMock.expect(action.orderRepository.createFulfillmentEvent(EasyMock.eq(order.id.value),
                Matcher.memberEquals(fulfillmentEvents[1]))).andReturn(null)

        EasyMock.replay(action.facadeContainer.fulfillmentFacade, action.orderRepository)

        def actionResult = (OrderActionResult) action.execute(TestBuilder.buildActionContext(order)).wrapped().
                get().data[ActionUtils.DATA_ORDER_ACTION_RESULT]
        assert actionResult.returnedEventStatus == EventStatus.PENDING
        EasyMock.verify(action.facadeContainer.fulfillmentFacade, action.orderRepository)
    }

    @Test(enabled = true)
    void testExecuteFulfillmentError() {
        def order = TestBuilder.buildOrderRequest()
        EasyMock.expect(action.facadeContainer.fulfillmentFacade.postFulfillment(EasyMock.same(order))).andReturn(
            Promise.throwing(new IllegalArgumentException())
        )
        EasyMock.replay(action.facadeContainer.fulfillmentFacade, action.orderRepository)
        try {
            (OrderActionResult) action.execute(TestBuilder.buildActionContext(order)).wrapped().
                    get().data[ActionUtils.DATA_ORDER_ACTION_RESULT]
            assert false
        } catch (ex) {
            assert ((AppErrorException)(ex.cause)).error.code == AppErrors.INSTANCE.fulfillmentConnectionError().code
        }
        EasyMock.verify(action.facadeContainer.fulfillmentFacade, action.orderRepository)
    }
}
