package com.junbo.order.test

import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.order.spec.model.Order
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
    void testQuoteAndImmediateSettle() {

        def user = serviceFacade.postUser()
        def paymentInstrument = serviceFacade.postCreditCardPaymentInstrument(user)
        def order = new Order()
        def offer = serviceFacade.getOfferByName('3D Parking Simulator')
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
                    generator.generateOrderItem(new OfferId(offer.id), 2)
            ]
        }

        def resultOrder = serviceFacade.postQuotes(order)
        assert resultOrder.orderItems.size() == 1
        //resultOrder.orderItems << generator.generateOrderItem(new OfferId(1002), 3)
        resultOrder = serviceFacade.putQuotes(resultOrder)
        assert resultOrder.id != null
        //assert resultOrder.orderItems.size() == 3
        assert resultOrder.totalAmount != null

        resultOrder = serviceFacade.settleQuotes(resultOrder.id)
        assert !resultOrder.tentative

        List<Balance> balances = serviceFacade.getBalance(resultOrder.id)
        assert balances.size() == 1
        assert balances[0].balanceItems.size() == resultOrder.orderItems.size()
        assert balances[0].totalAmount == resultOrder.totalAmount
        def fulfillment = serviceFacade.getFulfilment(resultOrder.id)
        assert fulfillment.userId == user.id.value
        // todo verify the entitlement
        // def entitlments = serviceFacade.getEntitlements(user.id, ['item001_ANGRY.BIRD_ONLINE_ACCESS'])
        // assert entitlments.size() == 1
    }
}
