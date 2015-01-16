package com.junbo.order.core.action
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.core.BaseTest
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.FulfillmentAction
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import com.junbo.order.core.matcher.Matcher
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.FulfillmentEventType
import groovy.transform.CompileStatic
import org.easymock.EasyMock
import org.springframework.aop.framework.Advised
import org.springframework.aop.support.AopUtils
import org.springframework.test.annotation.Rollback
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by fzhang on 14-3-10.
 */
@CompileStatic
class FulfillmentActionTest extends BaseTest{

    @Resource(name='fulfillmentAction')
    Action fulfillmentAction

    private FacadeContainer facadeContainer
    private FulfillmentFacade fulfillmentFacade
    private OrderRepositoryFacade orderRepository

    @BeforeMethod
    void setUp() {
        FulfillmentAction target = getTargetObject(fulfillmentAction, FulfillmentAction)
        target.facadeContainer = facadeContainer = new FacadeContainer()
        target.facadeContainer.fulfillmentFacade = fulfillmentFacade = EasyMock.createMock(FulfillmentFacade.class)
        target.orderRepository = orderRepository = EasyMock.createMock(OrderRepositoryFacade.class)
    }

    @Test(enabled = true)
    void testExecuteFulfillmentPending() {
        def order = TestBuilder.buildOrderRequest()
        order.orderItems.add(TestBuilder.buildOrderItem())
        order.id = new OrderId(idGenerator.nextId(OrderId))
        def fulfilmentResult = TestBuilder.buildFulfilmentRequest(order)
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem(FulfilmentStatus.SUCCEED, order.orderItems[0])
        fulfilmentResult.items << TestBuilder.buildFulfilmentItem(FulfilmentStatus.PENDING, order.orderItems[1])

        def fulfillmentHistories = [new FulfillmentHistory(), new FulfillmentHistory()]
        fulfillmentHistories[0].trackingUuid = UUID.fromString(fulfilmentResult.trackingUuid)
        fulfillmentHistories[0].fulfillmentEvent = FulfillmentEventType.FULFILL.toString()
        fulfillmentHistories[0].orderItemId = fulfilmentResult.items[0].itemReferenceId
        fulfillmentHistories[0].fulfillmentId =  fulfilmentResult.items[0].fulfilmentId
        fulfillmentHistories[0].success = true

        fulfillmentHistories[1].trackingUuid = UUID.fromString(fulfilmentResult.trackingUuid)
        fulfillmentHistories[1].fulfillmentEvent = FulfillmentEventType.REQUEST_FULFILL.toString()
        fulfillmentHistories[1].orderItemId = fulfilmentResult.items[1].itemReferenceId
        fulfillmentHistories[1].fulfillmentId =  fulfilmentResult.items[1].fulfilmentId
        fulfillmentHistories[1].success = true

        EasyMock.expect(facadeContainer.fulfillmentFacade.postFulfillment(
                EasyMock.same(order))).andReturn(Promise.pure(fulfilmentResult))
        EasyMock.expect(orderRepository.createFulfillmentHistory(
                Matcher.memberEquals(fulfillmentHistories[0]))).andReturn(null)
        EasyMock.expect(orderRepository.createFulfillmentHistory(
                Matcher.memberEquals(fulfillmentHistories[1]))).andReturn(null)

        EasyMock.replay(facadeContainer.fulfillmentFacade, orderRepository)

        def actionResult = (OrderActionResult) fulfillmentAction.execute(TestBuilder.buildActionContext(order)).
                get().data[ActionUtils.DATA_ORDER_ACTION_RESULT]
        assert actionResult.returnedEventStatus == EventStatus.PENDING
        EasyMock.verify(facadeContainer.fulfillmentFacade, orderRepository)
    }

    @Test(enabled = true)
    @Rollback
    void testExecuteFulfillmentError() {
        def order = TestBuilder.buildOrderRequest()
        order.id = new OrderId(idGenerator.nextId(OrderId))
        EasyMock.expect(facadeContainer.fulfillmentFacade.postFulfillment(EasyMock.same(order))).andReturn(
                Promise.throwing(new IllegalArgumentException())
        )
        EasyMock.replay(facadeContainer.fulfillmentFacade, orderRepository)
        Boolean recovered = false
        fulfillmentAction.execute(TestBuilder.buildActionContext(order)).syncRecover { Throwable ex ->
            assert ex instanceof AppErrorException
            assert ((AppErrorException) ex).error.error().code == AppErrors.INSTANCE.fulfillmentConnectionError(null).error().code
            recovered = true
            return null
        }.syncThen {
            return null
        }.get()
        assert recovered
        EasyMock.verify(facadeContainer.fulfillmentFacade, orderRepository)
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
