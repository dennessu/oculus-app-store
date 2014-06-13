package com.junbo.order.db.repo

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.OrderItemRevisionType
import groovy.transform.CompileStatic
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-3-12.
 */
@CompileStatic
class OrderRepositoryFacadeTest extends BaseTest {

    @Autowired
    private OrderRepositoryFacade orderRepository

    @Autowired
    ModelMapper modelMapper

    @Test
    void testCreateOrder() {
        def order = createOrder()
        orderRepository.createOrder(order)
        verifyByRead(order)
    }

    @Test
    void testUpdateOrder() {
        def order = createOrder()
        orderRepository.createOrder(order)

        verifyByRead(order)

        // add order item, discount, paymentId
        order.shippingAddress = new UserPersonalInfoId(TestHelper.generateId())
        order.shippingMethod = TestHelper.generateLong() % 100
        order.orderItems << createOrderItem()
        order.discounts << createDiscount(order, order.orderItems.last())
        order.payments << new PaymentInfo(paymentInstrument : new PaymentInstrumentId(TestHelper.generateId()))
        orderRepository.updateOrder(order, false, false, null)
        verifyByRead(order)

        // remove id and check
        order.orderItems.each { OrderItem it ->
            it.id = null
        }
        order.discounts.each { Discount it ->
            it.id = null
            it.orderItemId = null
            it.orderId = null
        }
        orderRepository.updateOrder(order, false, false, null)
        verifyByRead(order)

        // update order item, discount, paymentId
        order.orderItems[0].offer = new OfferId(TestHelper.generateLong().toString())
        order.discounts[0].coupon = 'Code' + TestHelper.generateLong()
        order.payments[0] = new PaymentInfo(paymentInstrument : new PaymentInstrumentId(TestHelper.generateId()))
        orderRepository.updateOrder(order, false, false, null)
        verifyByRead(order)

        // remove order item, discount, paymentId
        order.orderItems.clear()
        order.discounts.clear()
        order.payments.clear()
        orderRepository.updateOrder(order, false, false, null)
        verifyByRead(order)
    }

    @Test
    void testUpdateNonTentativeOrder() {
        def order = createOrder()
        orderRepository.createOrder(order)

        verifyByRead(order)

        // add order item, discount, paymentId
        order.shippingAddress = new UserPersonalInfoId(TestHelper.generateId())
        order.shippingMethod = TestHelper.generateLong() % 100
        order.orderItems << createOrderItem()
        order.discounts << createDiscount(order, order.orderItems.last())
        order.payments << new PaymentInfo(paymentInstrument : new PaymentInstrumentId(TestHelper.generateId()))
        orderRepository.updateOrder(order, false, false, null)
        verifyByRead(order)

        // update to non-tentative
        order.tentative = false
        orderRepository.updateOrder(order, true, null, null)
        verifyByRead(order)

        def newShippingAddress = TestHelper.generateId()
        order.shippingAddress = new UserPersonalInfoId(newShippingAddress)
        orderRepository.updateOrder(order, false, true, OrderItemRevisionType.ADJUST_SHIPPING)
        verifyByRead(order)
        def newOrder = orderRepository.getOrder(order.getId().value)
        assert(newOrder.orderRevisions.size() == 1)
        assert(newOrder.shippingAddress.value == newShippingAddress)
        assert(newOrder.latestOrderRevisionId == newOrder.orderRevisions[0].getId())
        def newOrders = orderRepository.getOrdersByUserId(order.user.value, new OrderQueryParam(), new PageParam())
        assert(newOrders[0].orderRevisions.size() == 1)
        assert(newOrders[0].shippingAddress.value == newShippingAddress)
        assert(newOrders[0].latestOrderRevisionId == newOrder.orderRevisions[0].getId())

        order.orderItems[0].quantity = 0
        orderRepository.updateOrder(order, false, true, OrderItemRevisionType.REFUND)
        verifyByRead(order)

        def refundItem = orderRepository.getOrderItem(order.orderItems[0].getId().value)
        assert(refundItem.orderItemRevisions.size() == 2)
        assert(refundItem.orderItemRevisions.find() { OrderItemRevision revision ->
            refundItem.latestOrderItemRevisionId == revision.getId() }.quantity == 0)
        def refundItems = orderRepository.getOrderItems(order.getId().value)
        refundItems.each {OrderItem item ->
            assert(item.orderItemRevisions.size() == 2)
            assert(item.orderItemRevisions.find() { OrderItemRevision revision ->
                item.latestOrderItemRevisionId == revision.getId() }.quantity == item.quantity)
        }
    }

    void verifyByRead(Order order) {
        assertOrderEquals(orderRepository.getOrder(order.getId().value), order)

        // verify items
        assertListEquals(orderRepository.getOrderItems(order.getId().value), order.orderItems,
                { OrderItem it ->
                    return it.id
                },
                { OrderItem actual, OrderItem expected ->
                    assertOrderItemEquals(actual, expected)
                }
        )

        // verify discounts
        assertListEquals(orderRepository.getDiscounts(order.getId().value), order.discounts,
                { Discount it ->
                    return it.id
                },
                { Discount actual, Discount expected ->
                    assertDiscountEquals(actual, expected)
                }
        )

        // verify pi
        assert new HashSet<PaymentInstrumentId>(order.payments.
                collect { PaymentInfo it -> return it.paymentInstrument}) ==
                new HashSet<PaymentInstrumentId>(order.payments.collect { PaymentInfo it -> return it.paymentInstrument})
    }

    private OrderItem createOrderItem() {
        def item = modelMapper.toOrderItemModel(TestHelper.generateOrderItem(), new MappingContext())
        item.id = null
        item.orderId = null
        return item
    }

    private Discount createDiscount(Order order, OrderItem orderItem) {
        def item = modelMapper.toDiscountModel(TestHelper.generateOrderDiscountInfoEntity(), new MappingContext())
        item.orderId = null
        item.id = null
        item.orderItemId = null
        item.ownerOrderItem = orderItem
        return item
    }

    private Order createOrder() {
        def orderEntity = TestHelper.generateOrder()
        orderEntity.setUserId(idGenerator.nextId(UserId.class));
        def order = modelMapper.toOrderModel(orderEntity, new MappingContext())
        order.currency = new CurrencyId(RandomStringUtils.randomAlphabetic(2).toUpperCase())
        order.id = null
        order.orderItems = [ createOrderItem(), createOrderItem()]
        order.discounts = [
                createDiscount(order, order.orderItems[0]),
                createDiscount(order, order.orderItems[1])
        ]
        order.payments = [
                new PaymentInfo(paymentInstrument:  new PaymentInstrumentId(TestHelper.generateLong())),
                new PaymentInfo(paymentInstrument: new PaymentInstrumentId(TestHelper.generateLong()))
        ]
        return order
    }

    void assertOrderItemEquals(OrderItem actual, OrderItem expected) {
        assert actual.orderId == expected.orderId
        assert actual.offer == expected.offer
        assert actual.type == expected.type
    }

    void assertDiscountEquals(Discount actual, Discount expected) {
        assert actual.id == expected.id

        assert actual.discountAmount == expected.discountAmount
        assert actual.orderId == expected.orderId
        assert actual.discountType == expected.discountType
        assert actual.orderItemId == expected.orderItemId
        //assert actual.promotion == expected.promotion
        //assert actual.coupon == expected.coupon
    }

    void assertOrderEquals(Order actual, Order expected) {
        assert actual.id == expected.id
        assert actual.currency.value == expected.currency.value
        assert actual.tentative == expected.tentative
        assert actual.shippingMethod == expected.shippingMethod
        assert actual.shippingAddress == expected.shippingAddress
    }

    private void assertListEquals(List actual, List expected, Closure idFunc, Closure assertFunc) {
        def actualMap = [:]
        def expectedMap = [:]
        actual.each {
            actualMap.put(idFunc.call(it), it)
        }
        expected.each {
            expectedMap.put(idFunc.call(it), it)
        }
        assert actualMap.keySet() == expectedMap.keySet()
        actualMap.each { k, v ->
            assertFunc.call(v, expectedMap[k])
        }
    }
}
