package com.junbo.order

import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.order.spec.model.Order
import com.junbo.order.test.ServiceFacade
import com.junbo.order.test.util.Generator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-3-17.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
class OrderPayInTest extends AbstractTestNGSpringContextTests {

    @Autowired
    def ServiceFacade serviceFacade

    @Autowired
    def Generator generator

    @Test
    void testQuote() {
        def user = serviceFacade.postUser()
        def paymentInstrument = serviceFacade.postCreditCardPaymentInstrument(user)
        def order = new Order()

        order.user = user.id
        order.with {
            trackingUuid = UUID.randomUUID()
            type = 'PAY_IN'
            country = 'US'
            currency = 'USD'
            tentative = true
            paymentInstruments = [
                    new PaymentInstrumentId(paymentInstrument.id)
            ]
            orderItems = [
                    generator.generateOrderItem('DIGITAL', new OfferId(1000), 2),
                    generator.generateOrderItem('DIGITAL', new OfferId(1001), 3)
            ]
        }

        def resultOrder = serviceFacade.postQuotes(order)
        assert resultOrder.orderItems.size() == 2
        resultOrder.orderItems << generator.generateOrderItem('DIGITAL', new OfferId(1002), 3)
        resultOrder = serviceFacade.putQuotes(resultOrder)
        assert resultOrder.id != null
        assert resultOrder.orderItems.size() == 3
        assert resultOrder.totalAmount != null
    }
}
