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
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by LinYi on 14-2-25.
 */
@CompileStatic
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
        assert orderModel.id.value == orderEntity.orderId :  'The order Id should not be different.'
        assert orderModel.id.value == returnedOrderEntity.orderId : 'The order Id should not be different.'
    }

    @Test(enabled = true)
    void testOrderItemMapper() {
        MappingContext context = new MappingContext()

        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem()
        OrderItem orderItemModel = modelMapper.toOrderItemModel(orderItemEntity, context)
        OrderItemEntity returnedOrderItemEntity = modelMapper.toOrderItemEntity(orderItemModel, context)
        assert orderItemModel != null :  'Fail to map order item entity to model.'
        assert returnedOrderItemEntity != null :  'Fail to map order item model to entity.'
        assert (orderItemModel.id.value == orderItemEntity.orderItemId)
    }

    @Test(enabled = true)
    void testOrderDiscountMapper() {
        MappingContext context = new MappingContext()

        OrderDiscountInfoEntity orderDiscountInfoEntity = TestHelper.generateOrderDiscountInfoEntity()
        Discount discount = modelMapper.toDiscountModel(orderDiscountInfoEntity, context)
        OrderDiscountInfoEntity returnedOrderDiscountInfoEntity = modelMapper.toDiscountEntity(discount, context)
        assert discount != null : 'Fail to map discount entity to model.'
        assert returnedOrderDiscountInfoEntity != null : 'Fail to map discount model to entity.'
        assert discount.id.value == orderDiscountInfoEntity.discountInfoId :
                'The order discount Id should not be different.'
        assert discount.id.value ==  returnedOrderDiscountInfoEntity.discountInfoId :
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
        assert orderEvent.action == returnedOrderEventEntity.actionId.toString() :
                'The order action should not be different.'
    }

    @Test(enabled = true)
    void testOrderItemFulfillmentEventMapper() {
        MappingContext context = new MappingContext()

        OrderItemFulfillmentEventEntity orderItemFulfillmentEventEntity =
                TestHelper.generateOrderItemFulfillmentEventEntity()
        FulfillmentEvent fulfillmentEvent =
                modelMapper.toFulfillmentEventModel(orderItemFulfillmentEventEntity, context)
        OrderItemFulfillmentEventEntity returnedFulfillmentEventEntity =
                modelMapper.toOrderItemFulfillmentEventEntity(fulfillmentEvent, context)
        assert fulfillmentEvent != null : 'Fail to map fulfillment event entity to model.'
        assert returnedFulfillmentEventEntity != null : 'Fail to map fulfillment event model to entity.'
        assert fulfillmentEvent.id.value == orderItemFulfillmentEventEntity.eventId :
                'The fulfillment id should not be different.'
        assert fulfillmentEvent.id.value ==  returnedFulfillmentEventEntity.eventId :
                'The fulfillment id should not be different.'
    }

    // TODO Fix this case
    @Test(enabled = false)
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
        assert subledger.payoutAmount == subledgerEntity.totalAmount : 'The amount should not be different.'
    }

    @Test(enabled = true)
    void testSubledgerItemMapper() {
        MappingContext context = new MappingContext()

        SubledgerItemEntity subledgerItemEntity = TestHelper.generateSubledgerItemEntity()
        SubledgerItem subledgerItem = modelMapper.toSubledgerItemModel(subledgerItemEntity, context)
        SubledgerItemEntity returnedSubledgerItemEntity = modelMapper.toSubledgerItemEntity(subledgerItem, context)
        assert subledgerItem != null : 'Fail to map subledger item entity to model.'
        assert returnedSubledgerItemEntity != null : 'Fail to map subledger item model to entity.'
        assert subledgerItem.orderItemId.value == subledgerItemEntity.orderItemId :
                'The order item id should not be different.'
        assert subledgerItem.orderItemId.value == returnedSubledgerItemEntity.orderItemId :
                'The order item id should not be different.'
    }
}
