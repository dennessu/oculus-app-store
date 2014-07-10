/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.mapper

import com.junbo.oom.core.MappingContext
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.db.entity.*
import com.junbo.order.spec.model.*
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
/**
 * Created by LinYi on 14-2-25.
 */
@CompileStatic
@TypeChecked
class MapperTest extends BaseTest {
    @Autowired
    ModelMapper modelMapper

    @Test(enabled = true)
    void testMapper() {
        MappingContext context = new MappingContext()

        /* Order Mapper*/
        OrderEntity orderEntity = TestHelper.generateOrder()
        Order orderModel = modelMapper.toOrderModel(orderEntity, context)
        OrderEntity returnedOrderEntity = modelMapper.toOrderEntity(orderModel, context)
        assert orderModel != null :  'Fail to map order entity to model.'
        assert returnedOrderEntity != null :'Fail to map order model to entity.'
        assert orderModel.getId().value == orderEntity.orderId :  'The order Id should not be different.'
        assert orderModel.getId().value == returnedOrderEntity.orderId : 'The order Id should not be different.'
        assert orderEntity.tentative == returnedOrderEntity.tentative
    }

    @Test(enabled = true)
    void testOrderItemMapper() {
        MappingContext context = new MappingContext()

        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem()
        OrderItem orderItemModel = modelMapper.toOrderItemModel(orderItemEntity, context)
        OrderItemEntity returnedOrderItemEntity = modelMapper.toOrderItemEntity(orderItemModel, context)
        assert orderItemModel != null :  'Fail to map order item entity to model.'
        assert returnedOrderItemEntity != null :  'Fail to map order item model to entity.'
        assert orderItemModel.getId().value == orderItemEntity.orderItemId
    }

    @Test(enabled = true)
    void testOrderDiscountMapper() {
        MappingContext context = new MappingContext()

        OrderDiscountInfoEntity orderDiscountInfoEntity = TestHelper.generateOrderDiscountInfoEntity()
        Discount discount = modelMapper.toDiscountModel(orderDiscountInfoEntity, context)
        OrderDiscountInfoEntity returnedOrderDiscountInfoEntity = modelMapper.toDiscountEntity(discount, context)
        assert discount != null : 'Fail to map discount entity to model.'
        assert returnedOrderDiscountInfoEntity != null : 'Fail to map discount model to entity.'
        assert discount.orderItemId.value == orderDiscountInfoEntity.orderItemId :
                'The order discount Id should not be different.'
        assert discount.id ==  returnedOrderDiscountInfoEntity.discountInfoId :
                'The order discount Id should not be different.'
    }

    @Test(enabled = true)
    void testOrderEventMapper() {
        MappingContext context = new MappingContext()

        OrderEventEntity orderEventEntity = TestHelper.generateOrderEventEntity()
        OrderEvent orderEvent = modelMapper.toOrderEventModel(orderEventEntity, context)
        OrderEventEntity returnedOrderEventEntity = modelMapper.toOrderEventEntity(orderEvent, context)

        assert orderEvent != null : 'Fail to map order event entity to model.'
        assert returnedOrderEventEntity != null : 'Fail to map order event model to entity.'
        assert orderEvent.action == orderEventEntity.actionId.toString() :'The order action should not be different.'
        assert orderEvent.eventTrackingUuid == orderEventEntity.eventTrackingUuid
        assert returnedOrderEventEntity.eventTrackingUuid == orderEventEntity.eventTrackingUuid
        assert orderEvent.action == returnedOrderEventEntity.actionId.toString() :
                'The order action should not be different.'
    }

    @Test(enabled = true)
    void testOrderItemFulfillmentHistoryMapper() {
        MappingContext context = new MappingContext()

        OrderItemFulfillmentHistoryEntity orderItemFulfillmentHistoryEntity =
                TestHelper.generateOrderItemFulfillmentHistoryEntity()
        FulfillmentHistory fulfillmentHistory =
                modelMapper.toFulfillmentHistoryModel(orderItemFulfillmentHistoryEntity, context)
        OrderItemFulfillmentHistoryEntity returnedFulfillmentHistoryEntity =
                modelMapper.toOrderItemFulfillmentHistoryEntity(fulfillmentHistory, context)
        assert fulfillmentHistory != null : 'Fail to map fulfillment event entity to model.'
        assert returnedFulfillmentHistoryEntity != null : 'Fail to map fulfillment event model to entity.'
        assert fulfillmentHistory.id == orderItemFulfillmentHistoryEntity.historyId :
                'The fulfillment id should not be different.'
        assert fulfillmentHistory.id ==  returnedFulfillmentHistoryEntity.historyId :
                'The fulfillment id should not be different.'
    }

    @Test(enabled = true)
    void testOrderBillingHistoryMapper() {
        MappingContext context = new MappingContext()

        def orderBillingHistoryEntity = TestHelper.generateOrderBillingHistoryEntity()
        def billingEvent =
                modelMapper.toOrderBillingHistoryModel(orderBillingHistoryEntity, context)
        def returnedOrderBillingHistoryEntity =
                modelMapper.toOrderBillingHistoryEntity(billingEvent, context)
        assert billingEvent != null : 'Fail to map billing event entity to model.'
        assert returnedOrderBillingHistoryEntity != null : 'Fail to map billing event model to entity.'
        assert billingEvent.balanceId == orderBillingHistoryEntity.balanceId :
                'The balance should not be different.'
        assert  billingEvent.balanceId == returnedOrderBillingHistoryEntity.balanceId :
                'The balance id should not be different.'
    }

    @Test(enabled = true)
    void testPreorderInfoEventMapper() {
        MappingContext context = new MappingContext()

        OrderItemPreorderInfoEntity preorderInfoEntity = TestHelper.generateOrderItemPreorderInfoEntity()
        PreorderInfo preorderInfo = modelMapper.toPreOrderInfoModel(preorderInfoEntity, context)
        OrderItemPreorderInfoEntity returnedPreorderInfoEntity =
                modelMapper.toOrderItemPreorderInfoEntity(preorderInfo, context)
        assert preorderInfo != null : 'Fail to map preorder info entity to model.'
        assert returnedPreorderInfoEntity != null : 'Fail to map preorder info model to entity.'
        assert preorderInfo.billingTime == preorderInfoEntity.billingDate :
                'The billing date should not be different.'
        assert preorderInfo.billingTime == returnedPreorderInfoEntity.billingDate :
                'The billing date should not be different.'
    }

    @Test(enabled = true)
    void testUpdateHistoryMapper() {
        MappingContext context = new MappingContext()

        OrderItemPreorderUpdateHistoryEntity updateHistoryEntity =
                TestHelper.generateOrderItemPreorderUpdateHistoryEntity()
        PreorderUpdateHistory updateHistory = modelMapper.toUpdateHistoryModel(updateHistoryEntity, context)
        OrderItemPreorderUpdateHistoryEntity returnedUpdateHistoryEntity =
                modelMapper.toUpdateHistoryEntity(updateHistory, context)
        assert updateHistory != null : 'Fail to map update history entity to model.'
        assert returnedUpdateHistoryEntity != null : 'Fail to map update history model to entity.'
        assert updateHistory.beforeValue ==  updateHistoryEntity.updateBeforeValue :
                'The value before update should not be different.'
        assert updateHistory.afterValue ==  returnedUpdateHistoryEntity.updateAfterValue :
                'The value after update should not be different.'
    }

    @Test(enabled = true)
    void testSubledgerMapper() {
        MappingContext context = new MappingContext()

        SubledgerEntity subledgerEntity = TestHelper.generateSubledgerEntity()
        Subledger subledger = modelMapper.toSubledgerModel(subledgerEntity, context)
        SubledgerEntity returnedSubledgerEntity = modelMapper.toSubledgerEntity(subledger, context)
        assert subledger != null : 'Fail to map subledger entity to model.'
        assert returnedSubledgerEntity != null : 'Fail to map subledger model to entity.'
        assert subledger.totalAmount == subledgerEntity.totalAmount : 'The amount should not be different.'
        assert subledgerEntity.offerId == returnedSubledgerEntity.offerId
    }

    @Test(enabled = true)
    void testSubledgerItemMapper() {
        MappingContext context = new MappingContext()

        SubledgerItemEntity subledgerItemEntity = TestHelper.generateSubledgerItemEntity()
        SubledgerItem subledgerItem = modelMapper.toSubledgerItemModel(subledgerItemEntity, context)
        SubledgerItemEntity returnedSubledgerItemEntity = modelMapper.toSubledgerItemEntity(subledgerItem, context)
        assert subledgerItem != null : 'Fail to map subledger item entity to model.'
        assert returnedSubledgerItemEntity != null : 'Fail to map subledger item model to entity.'
        assert subledgerItem.orderItem.value == subledgerItemEntity.orderItemId :
                'The order item id should not be different.'
        assert subledgerItem.orderItem.value == returnedSubledgerItemEntity.orderItemId :
                'The order item id should not be different.'
        assert subledgerItemEntity.offerId == returnedSubledgerItemEntity.offerId
    }
}
