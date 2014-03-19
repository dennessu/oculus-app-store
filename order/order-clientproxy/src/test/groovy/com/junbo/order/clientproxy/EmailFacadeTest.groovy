package com.junbo.order.clientproxy

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderId
import com.junbo.order.clientproxy.common.TestBuilder
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-17.
 */
@CompileStatic
class EmailFacadeTest extends BaseTest {
    @Resource(name = 'emailFacade')
    EmailFacade emailFacade

    @Test(enabled = false)
    public void testSendEmail() {
        def user = TestBuilder.buildUser()
        user.userName = 'armandinhowa@gmail.com'
        def offer = new Offer()
        offer.setName('fake_offer')
        offer.name = 'fake_offer'
        offer.setRevision(1)
        offer.setOwnerId(TestBuilder.generateLong())
        offer.setId(TestBuilder.generateLong())
        Item offerItem = new Item()
        offerItem.type = 0L
        def offers = [offer, offer]
        def order = new Order()
        order.setId(new OrderId(TestBuilder.generateLong()))
        def orderItem = new OrderItem()
        orderItem.setOffer(new OfferId(offer.id))
        orderItem.quantity = 1
        orderItem.unitPrice = 10.00G
        order.orderItems = [orderItem]
        order.totalAmount = 20.00G
        order.totalTax = 4.00G
        emailFacade.sendOrderConfirmationEMail(order, user, offers)
    }
}
