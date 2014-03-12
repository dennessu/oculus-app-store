package com.junbo.order.db.repo

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
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-3-12.
 */
class OrderRepositoryTest extends BaseTest {

    @Autowired
    private OrderRepository orderRepository

    def List mocks = []

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
        order.shippingAddressId = new ShippingAddressId(TestHelper.generateId())
        order.shippingMethodId = TestHelper.generateLong() % 100
        order.orderItems << createOrderItem()
        order.discounts << createDiscount(order, order.orderItems.last())
        order.paymentInstruments << new PaymentInstrumentId(TestHelper.generateId())
        orderRepository.updateOrder(order)
        verifyByRead(order)

        // remove id and check
        order.orderItems.each {
            it.id = null
        }
        order.discounts.each {
            it.id = null
            it.orderItem = null
            it.order = null
        }
        orderRepository.updateOrder(order)
        verifyByRead(order)

        // update order item, discount, paymentId
        order.orderItems[0].offer = TestHelper.generateLong()
        order.discounts[0].coupon = 'Code' + TestHelper.generateLong()
        order.paymentInstruments[0] = new PaymentInstrumentId(TestHelper.generateId())
        orderRepository.updateOrder(order)
        verifyByRead(order)

        // remove order item, discount, paymentId
        order.orderItems.clear()
        order.discounts.clear()
        order.paymentInstruments.clear()
        orderRepository.updateOrder(order)
        verifyByRead(order)
    }

    void verifyByRead(Order order) {
        assertOrderEquals(orderRepository.getOrder(order.id.value), order)

        // verify items
        assertListEquals(orderRepository.getOrderItems(order.id.value), order.orderItems,
                { OrderItem it ->
                    return it.id
                },
                { OrderItem actual, OrderItem expected ->
                    assertOrderItemEquals(actual, expected)
                }
        )

        // verify discounts
        assertListEquals(orderRepository.getDiscounts(order.id.value), order.discounts,
                { Discount it ->
                    return it.id
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
        item.id = null
        item.orderId = null
        return item
    }

    private Discount createDiscount(Order order, OrderItem orderItem) {
        def item = modelMapper.toDiscountModel(TestHelper.generateOrderDiscountInfoEntity(), new MappingContext())
        item.order = null
        item.id = null
        item.orderItem = null
        item.ownerOrder = order
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
        assert actual.offerRevision == expected.offerRevision
        assert actual.offer == expected.offer
        assert actual.federatedId == expected.federatedId
        assert actual.type == expected.type
    }

    void assertDiscountEquals(Discount actual, Discount expected) {
        assert actual.id == expected.id

        assert actual.discountAmount == expected.discountAmount
        assert actual.order == expected.order
        assert actual.discountType == expected.discountType
        assert actual.orderItem == expected.orderItem
        //assert actual.promotion == expected.promotion
        //assert actual.coupon == expected.coupon
    }

    void assertOrderEquals(Order actual, Order expected) {
        assert actual.id == expected.id
        assert actual.currency == expected.currency
        assert actual.tentative == expected.tentative
        assert actual.shippingMethodId == expected.shippingMethodId
        assert actual.shippingAddressId == expected.shippingAddressId
    }
    //protected <T> createMock()

    private <T> T mock(Class<T> type) {
        def mock = EasyMock.createMock(type)
        mocks << mock
        return mock
    }

    private void replay() {
        EasyMock.replay(mocks.toArray())
    }

    private void verify() {
        EasyMock.verify(mocks.toArray())
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

    @AfterMethod
    public void clearMock() {
        mocks.clear()
    }


}
