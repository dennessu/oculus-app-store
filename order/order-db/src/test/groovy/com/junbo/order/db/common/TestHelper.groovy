/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.common

import com.junbo.order.db.entity.*
import com.junbo.order.spec.model.*
import groovy.transform.CompileStatic

import java.security.SecureRandom

/**
 * Created by LinYi on 14-2-25.
 */

@CompileStatic
class TestHelper {

    private static final int DEFAULT_PRICE = 10

    private static final int DEFAULT_QUANTITY = 1

    private static final int RAND_INT_MAX = 100

    private static long nextId = System.currentTimeMillis()

    static long generateId() {
        return nextId++
    }

    static UUID generateUUID() {
        return UUID.randomUUID()
    }

    static long generateLong() {
        return System.currentTimeMillis()
    }

    static OrderEntity generateOrder() {
        OrderEntity order = new OrderEntity()
        order.setOrderId(generateId())
        order.setUserId(generateLong())
        order.setCountry('US')
        order.setCurrency('USD')
        order.setOrderTypeId(OrderType.PAY_IN)
        order.setTrackingUuid(UUID.randomUUID())
        order.setCreatedTime(new Date())
        order.setCreatedBy('Test')
        order.setUpdatedBy('Test')
        order.setUpdatedTime(new Date())
        order.setOrderStatusId(OrderStatus.OPEN)

        return order
    }

    static OrderItemEntity generateOrderItem() {
        OrderItemEntity entity = new OrderItemEntity()
        def rand = new SecureRandom()
        entity.setOrderItemId(rand.nextLong())
        entity.setOrderId(rand.nextLong())
        entity.setOrderItemType(ItemType.DIGITAL)
        entity.setProductItemId('123')
        entity.setProductSkuId('TestProductSkuId')
        entity.setProductItemVersion('TestProductItemVersion')
        entity.setUnitPrice(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setQuantity(Integer.valueOf(DEFAULT_QUANTITY))
        entity.setTotalPrice(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setFederatedId('TestFederatedId')
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('Test')
        entity.setUpdatedBy('Test')
        entity.setUpdatedTime(new Date())
        return entity
    }

    static OrderDiscountInfoEntity generateOrderDiscountInfoEntity() {
        OrderDiscountInfoEntity entity = new OrderDiscountInfoEntity()
        def rand = new SecureRandom()
        entity.setDiscountInfoId(generateId())
        entity.setOrderId(generateLong())
        entity.setDiscountType(DiscountType.OFFER_DISCOUNT)
        entity.setDiscountAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setDiscountRate(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('Tester')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('Tester')
        return entity
    }

    static OrderEventEntity generateOrderEventEntity() {
        OrderEventEntity orderEventEntity = new OrderEventEntity()
        orderEventEntity.setOrderId(generateId())
        orderEventEntity.setEventId(generateLong())
        orderEventEntity.setActionId(OrderActionType.FULFILL)
        orderEventEntity.setStatusId(EventStatus.COMPLETED)
        return orderEventEntity
    }

    static OrderItemFulfillmentEventEntity generateOrderItemFulfillmentEventEntity() {
        OrderItemFulfillmentEventEntity entity = new OrderItemFulfillmentEventEntity()
        entity.setEventId(generateId())
        entity.setAction(FulfillmentAction.FULFILL)
        entity.setStatus(EventStatus.COMPLETED)
        entity.setOrderId(generateLong())
        entity.setOrderItemId(generateLong())
        entity.setTrackingUuid(generateUUID())
        entity.setFulfillmentId('TEST_FULFILLMENT_ID')
        return entity
    }


    static OrderItemPreorderInfoEntity generateOrderItemPreorderInfoEntity() {
        OrderItemPreorderInfoEntity entity = new OrderItemPreorderInfoEntity()
        entity.setOrderItemPreorderInfoId(generateId())
        entity.setOrderItemId(generateLong())
        entity.setBillingDate(new Date())
        entity.setPreNotificationDate(new Date())
        entity.setReleaseDate(new Date())
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('Tester')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('Tester')
        return entity
    }

    static OrderItemPreorderUpdateHistoryEntity generateOrderItemPreorderUpdateHistoryEntity() {
        OrderItemPreorderUpdateHistoryEntity entity = new OrderItemPreorderUpdateHistoryEntity()
        def rand = new SecureRandom()
        entity.setOrderItemPreorderUpdateHistoryId(generateId())
        entity.setOrderItemId(generateLong())
        entity.setUpdateTypeId((short) rand.nextInt(RAND_INT_MAX))
        entity.setUpdateColumn('COLUMN_TO_UPDATE')
        entity.setUpdateBeforeValue('BEFORE_UPDATE')
        entity.setUpdateAfterValue('AFTER_UPDATE')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('TESTER')
        return entity
    }

    static SubledgerEntity generateSubledgerEntity() {
        SubledgerEntity entity = new SubledgerEntity()
        def rand = new SecureRandom()
        entity.setSubledgerId(generateId())
        entity.setSellerId(generateLong())
        entity.setSellerTaxProfileId(generateLong())
        entity.setCurrencyId((short) rand.nextInt(RAND_INT_MAX))
        entity.setTotalAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('TESTER')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('TESTER')
        return entity
    }

    static SubledgerItemEntity generateSubledgerItemEntity() {
        SubledgerItemEntity entity = new SubledgerItemEntity()
        def rand = new SecureRandom()
        entity.setSubledgerItemId(generateId())
        entity.setOrderItemId(generateLong())
        entity.setSubledgerId(generateLong())
        entity.setOriginalSubledgerItemId(generateLong())
        entity.setTotalAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('TESTER')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('Tester')
        return entity
    }
}
