/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.common
import com.junbo.order.db.entity.*
import com.junbo.order.db.entity.enums.*
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.lang.RandomStringUtils

import java.security.SecureRandom
/**
 * Created by LinYi on 14-2-25.
 */

@CompileStatic
@TypeChecked
class TestHelper {

    private static final int DEFAULT_PRICE = 10

    private static final int DEFAULT_QUANTITY = 1

    private static final int RAND_INT_MAX = 100

    private static long nextId = System.currentTimeMillis()

    private static SecureRandom rand = new SecureRandom()

    static long generateId() {
        return nextId++
    }

    static UUID generateUUID() {
        return UUID.randomUUID()
    }

    static long generateLong() {
        sleep(1)
        return nextId++
    }

    static <T> T randEnum(Class<T> enumType) {
        return (T) enumType.enumConstants[rand.nextInt(enumType.enumConstants.length)]
    }

    static OrderEntity generateOrder() {
        OrderEntity order = new OrderEntity()
        order.setOrderId(generateId())
        order.setUserId(generateLong())
        order.setCountry('US')
        order.setCurrency('USD')
        order.setCreatedTime(new Date())
        order.setCreatedBy('Test')
        order.setUpdatedBy('Test')
        order.setUpdatedTime(new Date())
        order.setOrderStatusId(OrderStatus.OPEN)
        order.setTentative(rand.nextBoolean())
        order.setTotalAmount(BigDecimal.valueOf(DEFAULT_PRICE))
        order.setTotalDiscount(BigDecimal.valueOf(DEFAULT_PRICE))
        order.setTotalTax(BigDecimal.valueOf(DEFAULT_PRICE))
        order.setIsTaxInclusive(false)
        order.setTotalShippingFee(BigDecimal.valueOf(DEFAULT_PRICE))
        order.setTotalShippingFeeDiscount(BigDecimal.valueOf(DEFAULT_PRICE))
        order.setHonoredTime(new Date())
        order.setLocale('en-US')
        return order
    }

    static OrderItemEntity generateOrderItem() {
        OrderItemEntity entity = new OrderItemEntity()
        def rand = new SecureRandom()
        entity.setOrderItemId(rand.nextLong())
        entity.setOrderId(rand.nextLong())
        entity.setOrderItemType(ItemType.DIGITAL)
        entity.setProductItemId(generateLong().toString())
        entity.setUnitPrice(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setQuantity(Integer.valueOf(DEFAULT_QUANTITY))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('Test')
        entity.setUpdatedBy('Test')
        entity.setUpdatedTime(new Date())
        entity.setTotalAmount(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setTotalDiscount(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setTotalTax(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setHonoredTime(new Date())
        return entity
    }

    static OrderDiscountInfoEntity generateOrderDiscountInfoEntity() {
        OrderDiscountInfoEntity entity = new OrderDiscountInfoEntity()
        def rand = new SecureRandom()
        entity.setDiscountInfoId(generateId())
        entity.setOrderId(generateLong())
        entity.setOrderItemId(generateLong())
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
        orderEventEntity.trackingUuid = UUID.randomUUID()
        orderEventEntity.eventTrackingUuid = UUID.randomUUID()
        orderEventEntity.flowName = UUID.randomUUID().toString()
        return orderEventEntity
    }

    static OrderItemFulfillmentHistoryEntity generateOrderItemFulfillmentHistoryEntity() {
        OrderItemFulfillmentHistoryEntity entity = new OrderItemFulfillmentHistoryEntity()
        entity.setHistoryId(generateId())
        entity.setFulfillmentEventId(randEnum(FulfillmentAction))
        entity.setOrderId(generateLong())
        entity.setOrderItemId(generateLong())
        entity.setTrackingUuid(generateUUID())
        entity.setFulfillmentId(RandomStringUtils.randomAlphabetic(20))
        return entity
    }

    static OrderBillingHistoryEntity generateOrderBillingHistoryEntity() {
        def entity = new OrderBillingHistoryEntity()
        entity.historyId = generateId()
        entity.billingEventId = randEnum(BillingAction)
        entity.setOrderId(generateLong())
        entity.balanceId = RandomStringUtils.randomAlphabetic(20)
        entity.totalAmount = BigDecimal.TEN
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
        entity.setCurrency(RandomStringUtils.randomAlphabetic(3))
        entity.setTotalAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy('TESTER')
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy('TESTER')
        entity.setStartTime(new Date())
        entity.setEndTime(new Date())
        entity.setPayoutStatus(PayoutStatus.PENDING)
        entity.setProductItemId(generateId().toString())
        entity.setCountry('US')
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
        entity.setSubledgerItemAction(SubledgerItemAction.CHARGE)
        entity.setProductItemId(generateId().toString())
        entity.setUpdatedBy('Tester')
        entity.setStatus(SubledgerItemStatus.PENDING)
        return entity
    }
}
