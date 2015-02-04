package com.junbo.order.db.repo

import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderPendingActionId
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.spec.model.OrderPendingAction
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.OrderPendingActionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.Assert
import org.testng.annotations.Test

import java.text.SimpleDateFormat
/**
 * Created by acer on 2015/2/2.
 */
class OrderPendingActionRepositoryTest extends BaseTest {

    @Autowired(required = true)
    @Qualifier('sqlOrderPendingActionRepository')
    OrderPendingActionRepository sqlOrderPendingActionRepository

    @Autowired(required = true)
    @Qualifier('cloudantOrderPendingActionRepository')
    OrderPendingActionRepository cloudantOrderPendingActionRepository

    @Test
    public void testOrderPendingActionRepositorySQL()  {
        def orderIdA = new OrderId(idGenerator.nextId(OrderId))
        def orderIdB = new OrderId(idGenerator.nextId(OrderId))
        List<OrderPendingAction> orderPendingActionList = [
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.RISK_REVIEW),
                TestHelper.generateOrderPendingAction(orderIdB, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdB, OrderPendingActionType.RISK_REVIEW)
        ]

        List<OrderPendingAction> created = []
        orderPendingActionList.each { OrderPendingAction action ->
            created << sqlOrderPendingActionRepository.get(sqlOrderPendingActionRepository.create(action).get().getId()).get()
            Thread.sleep(500)
        }

        // verify created
        int index = 0
        created.each { OrderPendingAction createdPendingAction ->
            verifyOrderPendingAction(createdPendingAction, orderPendingActionList[index++])
        }

        // query by order Id
        def list = sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE).get()
        Assert.assertEquals(list.size(), 2)
        verifyOrderPendingActionList(list, shardAlgorithm.dataCenterId(orderIdA.value), shardAlgorithm.shardId(orderIdA.value),
                OrderPendingActionType.FB_TRANSACTION_RECEIVE, null, null, false)

        list = sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdB, OrderPendingActionType.RISK_REVIEW).get()
        Assert.assertEquals(list.size(), 1)
        verifyOrderPendingActionList(list, shardAlgorithm.dataCenterId(orderIdB.value), shardAlgorithm.shardId(orderIdB.value),
                OrderPendingActionType.RISK_REVIEW, null, null, false)

        // query by shard
        int dcId = shardAlgorithm.dataCenterId(orderIdA.value)
        int shardId = shardAlgorithm.shardId(orderIdA.value)
        list = sqlOrderPendingActionRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE, null, new Date(System.currentTimeMillis() + 1000),
            new PageParam(start: 0, count: 2)).get()
        Assert.assertEquals(list.size(), 2)
        verifyOrderPendingActionList(list, dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE, null, new Date(System.currentTimeMillis() + 1000), true)

        Date start = created[0].createdTime
        Date end = created[2].createdTime
        list = sqlOrderPendingActionRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE, start, end,
                new PageParam(start: 0, count: 10)).get()
        Assert.assertEquals(list.size(), 2)
        verifyOrderPendingActionList(list, dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE, start, end, true)

        list = sqlOrderPendingActionRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE, null, new Date(System.currentTimeMillis() + 1000),
                new PageParam(start: 1, count: 1)).get()
        Assert.assertEquals(list.size(), 1)

        Assert.assertEquals(sqlOrderPendingActionRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE,
                null,
                new SimpleDateFormat("yyyy-MM-dd").parse('2000-01-01'),
                new PageParam(start: 0, count: 2)).get().size(), 0)
        Assert.assertEquals(sqlOrderPendingActionRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, OrderPendingActionType.FB_TRANSACTION_RECEIVE,
                created[0].createdTime,
                created[0].createdTime,
                new PageParam(start: 0, count: 2)).get().size(), 0)

        List<OrderPendingAction> updated = []
        created.each {  OrderPendingAction e ->
            e.completed = true
            updated << sqlOrderPendingActionRepository.update(e, e).get()
        }

        Assert.assertEquals(sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE).get().size(), 0)
        Assert.assertEquals(sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdA, OrderPendingActionType.RISK_REVIEW).get().size(), 0)
        Assert.assertEquals(sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdB, OrderPendingActionType.FB_TRANSACTION_RECEIVE).get().size(), 0)
        Assert.assertEquals(sqlOrderPendingActionRepository.getOrderPendingActionsByOrderId(orderIdB, OrderPendingActionType.RISK_REVIEW).get().size(), 0)
    }

    @Test
    public void testOrderPendingActionRepositoryCloudant()  {
        def orderIdA = new OrderId(idGenerator.nextId(OrderId))
        def orderIdB = new OrderId(idGenerator.nextId(OrderId))
        List<OrderPendingAction> orderPendingActionList = [
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdA, OrderPendingActionType.RISK_REVIEW),
                TestHelper.generateOrderPendingAction(orderIdB, OrderPendingActionType.FB_TRANSACTION_RECEIVE),
                TestHelper.generateOrderPendingAction(orderIdB, OrderPendingActionType.RISK_REVIEW)
        ]

        List<OrderPendingAction> created = []

        try {
            orderPendingActionList.each { OrderPendingAction action ->
                action.setId(new OrderPendingActionId(idGenerator.nextId(OrderPendingActionId, action.orderId.value)))
                created << cloudantOrderPendingActionRepository.create(action).get()
            }

            // verify created
            int index = 0
            created.each { OrderPendingAction createdPendingAction ->
                verifyOrderPendingAction(createdPendingAction, orderPendingActionList[index], false)
                OrderPendingAction read = cloudantOrderPendingActionRepository.get(createdPendingAction.getId()).get()
                verifyOrderPendingAction(read, orderPendingActionList[index], false)
                index++
            }
        } finally {

            List<OrderPendingAction> updated = []
            created.each {  OrderPendingAction e ->
                e.completed = true
                updated << cloudantOrderPendingActionRepository.update(e, e).get()
            }
        }
    }

    private void verifyOrderPendingAction(OrderPendingAction actual, OrderPendingAction expected, boolean hasResourceAge) {
        Assert.assertEquals(actual.completed, expected.completed)
        Assert.assertEquals(actual.actionType, expected.actionType)
        Assert.assertEquals(actual.orderId, expected.orderId)
        Assert.assertNotNull(actual.createdTime)
        if (hasResourceAge) {
            Assert.assertNotNull(actual.resourceAge)
        }
        Assert.assertEquals(actual.getProperties(), expected.getProperties())
    }

    private void verifyOrderPendingAction(OrderPendingAction actual, OrderPendingAction expected) {
        verifyOrderPendingAction(actual, expected, true)
    }

    private void verifyOrderPendingActionList(List<OrderPendingAction> actuals, int dcId, int shardId,
                                              OrderPendingActionType expectedAction, Date startTime, Date endTime, boolean ascending) {
        OrderPendingAction pre
        actuals.each { OrderPendingAction action ->
            if (ascending && pre != null) {
                Assert.assertTrue(action.createdTime.after(pre.createdTime))
            }
            int curDcId = shardAlgorithm.dataCenterId(action.id.value)
            int curShardId = shardAlgorithm.shardId(action.id.value)
            Assert.assertEquals(action.actionType, expectedAction.name())
            if (startTime != null) {
                Assert.assertTrue(!action.createdTime.before(startTime))
            }
            if (endTime != null) {
                Assert.assertTrue(action.createdTime.before(endTime))
            }
            Assert.assertEquals(curDcId, dcId)
            Assert.assertEquals(curShardId, shardId)
            pre = action
        }
    }
}
