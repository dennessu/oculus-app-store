/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.common
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.*
import com.junbo.order.db.entity.*
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.*
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

import java.security.SecureRandom
/**
 * Created by LinYi on 14-2-25.
 */

@CompileStatic
@TypeChecked
@Component
class TestHelper implements ApplicationContextAware {

    private static final int DEFAULT_PRICE = 10

    private static final int DEFAULT_QUANTITY = 1

    private static final int RAND_INT_MAX = 100

    private static IdGenerator idGenerator

    private static IdGenerator orderIdGenerator

    private static SecureRandom rand = new SecureRandom()

    static long generateId() {
        return idGenerator.nextId(0)
    }

    static String generateStrId() {
        return idGenerator.nextId(0).toString()
    }

    static long generateOrderId() {
        return orderIdGenerator.nextId(0)
    }

    static UUID generateUUID() {
        return UUID.randomUUID()
    }

    static long generateLong() {
        sleep(1)
        return System.currentTimeMillis()
    }

    static <T> T randEnum(Class<T> enumType) {
        return (T) enumType.enumConstants[rand.nextInt(enumType.enumConstants.length)]
    }

    static OrderEntity generateOrder() {
        OrderEntity order = new OrderEntity()
        order.setOrderId(generateOrderId())
        order.setUserId(generateLong())
        order.setCountry('US')
        order.setCurrency('USD')
        order.setCreatedTime(new Date())
        order.setCreatedBy(123L)
        order.setUpdatedBy(456L)
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
        entity.setOrderItemId(generateId())
        entity.setOrderId(generateOrderId())
        entity.setOrderItemType(ItemType.DIGITAL)
        entity.setOfferId(generateLong().toString())
        entity.setUnitPrice(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setQuantity(Integer.valueOf(DEFAULT_QUANTITY))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy(123L)
        entity.setUpdatedBy(123L)
        entity.setUpdatedTime(new Date())
        entity.setTotalAmount(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setTotalDiscount(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setTotalTax(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setDeveloperRevenue(BigDecimal.valueOf(DEFAULT_PRICE))
        entity.setHonoredTime(new Date())
        entity.setIsPreorder(false)
        return entity
    }

    static OrderDiscountInfoEntity generateOrderDiscountInfoEntity() {
        OrderDiscountInfoEntity entity = new OrderDiscountInfoEntity()
        def rand = new SecureRandom()
        entity.setDiscountInfoId(generateId())
        entity.setOrderId(generateOrderId())
        entity.setOrderItemId(generateLong())
        entity.setDiscountType(DiscountType.OFFER_DISCOUNT)
        entity.setDiscountAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setDiscountRate(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy(123L)
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy(123L)
        return entity
    }

    static OrderEventEntity generateOrderEventEntity() {
        OrderEventEntity orderEventEntity = new OrderEventEntity()
        orderEventEntity.setOrderId(generateOrderId())
        orderEventEntity.setEventId(generateId())
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
        entity.setFulfillmentEventId(randEnum(FulfillmentEventType))
        entity.setOrderItemId(generateLong())
        entity.setTrackingUuid(generateUUID())
        entity.setFulfillmentId(RandomStringUtils.randomAlphabetic(20))
        return entity
    }

    static OrderBillingHistoryEntity generateOrderBillingHistoryEntity() {
        def entity = new OrderBillingHistoryEntity()
        entity.historyId = generateId()
        entity.billingEventId = randEnum(BillingAction)
        entity.setOrderId(generateOrderId())
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
        entity.setCreatedBy(123L)
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy(123L)
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
        entity.setUpdatedBy(123L)
        return entity
    }

    static SubledgerEntity generateSubledgerEntity() {
        SubledgerEntity entity = new SubledgerEntity()
        def rand = new SecureRandom()
        entity.setSubledgerId(generateId())
        entity.setSellerId(generateLong())
        entity.setCurrency(RandomStringUtils.randomAlphabetic(3))
        entity.setTotalAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setTotalPayoutAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setTotalQuantity(rand.nextInt(RAND_INT_MAX).longValue())
        entity.setCreatedTime(new Date())
        entity.setCreatedBy(123L)
        entity.setUpdatedTime(new Date())
        entity.setUpdatedBy(123L)
        entity.setStartTime(new Date())
        entity.setEndTime(new Date())
        entity.setPayoutStatus(PayoutStatus.PENDING)
        entity.setOfferId(generateId().toString())
        entity.setCountry('US')
        return entity
    }

    static Subledger generateSubledger() {
        return new Subledger(
            seller: new OrganizationId(generateId()),
            offer: new OfferId(generateStrId()),
            payoutStatus: PayoutStatus.COMPLETED.name(),
            startTime: new Date(),
            endTime: new Date(),
            country: new CountryId('US'),
            currency: new CurrencyId(RandomStringUtils.randomAlphabetic(3)),
            totalAmount: BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX))
        )
    }

    static SubledgerItem generateSubledgerItem(OrderItemId orderItemId) {
        return new SubledgerItem(
                offer: new OfferId(generateStrId()),
                subledger: new SubledgerId(generateId()),
                originalSubledgerItem: new SubledgerItemId(generateId()),
                totalAmount: BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)),
                totalPayoutAmount: BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)),
                totalQuantity: rand.nextInt(RAND_INT_MAX).longValue(),
                orderItem: orderItemId,
                subledgerItemAction: SubledgerItemAction.PAYOUT.name(),
                status: SubledgerItemStatus.OPEN.name()
        )
    }

    static SubledgerItemEntity generateSubledgerItemEntity() {
        SubledgerItemEntity entity = new SubledgerItemEntity()
        def rand = new SecureRandom()
        entity.setSubledgerItemId(generateId())
        entity.setOrderItemId(generateLong())
        entity.setSubledgerId(generateLong())
        entity.setSubledgerItemAction(SubledgerItemAction.PAYOUT)
        entity.setOriginalSubledgerItemId(generateLong())
        entity.setTotalAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setTotalPayoutAmount(BigDecimal.valueOf(rand.nextInt(RAND_INT_MAX)))
        entity.setTotalQuantity(rand.nextInt(RAND_INT_MAX).longValue())
        entity.setCreatedTime(new Date())
        entity.setCreatedBy(123L)
        entity.setUpdatedTime(new Date())
        entity.setOfferId(generateId().toString())
        entity.setUpdatedBy(123L)
        entity.setStatus(SubledgerItemStatus.OPEN)
        return entity
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        orderIdGenerator = (IdGenerator)applicationContext.getBean("oculus40IdGenerator")
        idGenerator = (IdGenerator)applicationContext.getBean("oculus48IdGenerator")
    }
}

