package com.junbo.order.db.repo

import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.ShippingAddressId
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-3-12.
 */
class OrderRepositoryTest extends BaseTest {

    @Autowired
    private OrderRepository orderRepository

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
        order.shippingAddress = new ShippingAddressId(TestHelper.generateId())
        order.shippingMethod = TestHelper.generateLong() % 100
        order.orderItems << createOrderItem()
        order.discounts << createDiscount(order, order.orderItems.last())
        order.paymentInstruments << new PaymentInstrumentId(TestHelper.generateId())
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // remove id and check
        order.orderItems.each {
            it.orderItemId = null
        }
        order.discounts.each {
            it.discountInfoId = null
            it.orderItemId = null
            it.orderId = null
        }
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // update order item, discount, paymentId
        order.orderItems[0].offer = new OfferId(TestHelper.generateLong())
        order.discounts[0].coupon = 'Code' + TestHelper.generateLong()
        order.paymentInstruments[0] = new PaymentInstrumentId(TestHelper.generateId())
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // remove order item, discount, paymentId
        order.orderItems.clear()
        order.discounts.clear()
        order.paymentInstruments.clear()
        orderRepository.updateOrder(order, false)
        verifyByRead(order)
    }

    void verifyByRead(Order order) {
        assertOrderEquals(orderRepository.getOrder(order.id.value), order)

        // verify items
        assertListEquals(orderRepository.getOrderItems(order.id.value), order.orderItems,
                { OrderItem it ->
                    return it.orderItemId
                },
                { OrderItem actual, OrderItem expected ->
                    assertOrderItemEquals(actual, expected)
                }
        )

        // verify discounts
        assertListEquals(orderRepository.getDiscounts(order.id.value), order.discounts,
                { Discount it ->
                    return it.discountInfoId
                },
                { Discount actual, Discount expected ->
                    assertDiscountEquals(actual, expected)
                }
        )

        // verify pi
        assert new HashSet<PaymentInstrumentId>(orderRepository.getPaymentInstrumentIds(order.id.value)) ==
                new HashSet<PaymentInstrumentId>(order.paymentInstruments)
    }

    private OrderItem createOrderItem() {
        def item = modelMapper.toOrderItemModel(TestHelper.generateOrderItem(), new MappingContext())
        item.orderItemId = null
        item.orderId = null
        return item
    }

    private Discount createDiscount(Order order, OrderItem orderItem) {
        def item = modelMapper.toDiscountModel(TestHelper.generateOrderDiscountInfoEntity(), new MappingContext())
        item.orderId = null
        item.discountInfoId = null
        item.orderItemId = null
        item.ownerOrderItem = orderItem
        return item
    }

    private Order createOrder() {
        def order = modelMapper.toOrderModel(TestHelper.generateOrder(), new MappingContext())
        order.id = null
        order.orderItems = [ createOrderItem(), createOrderItem()]
        order.discounts = [
                createDiscount(order, order.orderItems[0]),
                createDiscount(order, order.orderItems[1])
        ]
        order.paymentInstruments = [
                new PaymentInstrumentId(TestHelper.generateLong()),
                new PaymentInstrumentId(TestHelper.generateLong())
        ]
        return order
    }

    void assertOrderItemEquals(OrderItem actual, OrderItem expected) {
        assert actual.orderId == expected.orderId
        assert actual.offer == expected.offer
        assert actual.type == expected.type
    }

    void assertDiscountEquals(Discount actual, Discount expected) {
        assert actual.discountInfoId == expected.discountInfoId

        assert actual.discountAmount == expected.discountAmount
        assert actual.orderId == expected.orderId
        assert actual.discountType == expected.discountType
        assert actual.orderItemId == expected.orderItemId
        //assert actual.promotion == expected.promotion
        //assert actual.coupon == expected.coupon
    }

    void assertOrderEquals(Order actual, Order expected) {
        assert actual.id == expected.id
        assert actual.currency == expected.currency
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
