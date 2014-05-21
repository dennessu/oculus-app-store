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
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
/**
 * Created by fzhang on 14-3-12.
 */
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
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // remove id and check
        order.orderItems.each {
            it.id = null
        }
        order.discounts.each {
            it.id = null
            it.orderItemId = null
            it.orderId = null
        }
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // update order item, discount, paymentId
        order.orderItems[0].offer = new OfferId(TestHelper.generateLong())
        order.discounts[0].coupon = 'Code' + TestHelper.generateLong()
        order.payments[0] = new PaymentInfo(paymentInstrument : new PaymentInstrumentId(TestHelper.generateId()))
        orderRepository.updateOrder(order, false)
        verifyByRead(order)

        // remove order item, discount, paymentId
        order.orderItems.clear()
        order.discounts.clear()
        order.payments.clear()
        orderRepository.updateOrder(order, false)
        verifyByRead(order)
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
        assert new HashSet<PaymentInstrumentId>(orderRepository.getPayments(order.getId().value).
                collect {it -> return it.paymentInstrument}) ==
                new HashSet<PaymentInstrumentId>(order.payments.collect {it -> return it.paymentInstrument})
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