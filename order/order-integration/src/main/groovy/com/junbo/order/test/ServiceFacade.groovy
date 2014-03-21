package com.junbo.order.test

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.OrderId
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.UserResource
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.resource.OrderResource
import com.junbo.payment.spec.model.Address
import com.junbo.payment.spec.model.CreditCardRequest
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.Phone
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 14-3-17.
 */
@Component('serviceFacade')
class ServiceFacade {

    private final static int DEFAULT_PAGE_SIZE = 20

    @Autowired
    UserResource userResource

    @Autowired
    OrderResource orderResource

    @Autowired
    OfferResource offerResource

    @Autowired
    ItemResource itemResource

    @Autowired
    BalanceResource balanceResource

    @Autowired
    PaymentInstrumentResource paymentInstrumentResource

    List<Offer> offers

    User postUser() {
        User user = new User()
        user.userName = RandomStringUtils.randomAlphabetic(10) + '@wan-san.com'
        user.password = '123456!@bcd'
        user.status = 'ACTIVE'
        return userResource.postUser(user).wrapped().get()
    }

    PaymentInstrument postCreditCardPaymentInstrument(User user) {
        def pi = new PaymentInstrument().with {
            accountName = 'David'
            accountNum = '4111111111111111'
            isDefault = true
            trackingUuid = UUID.randomUUID()
            type = 'CREDITCARD'
            creditCardRequest = new CreditCardRequest().with {
                expireDate = '2050-11-27'
                encryptedCvmCode = '111'
                it
            }
            address = new Address().with {
                addressLine1 = 'ThirdStreetFerriday'
                city = 'LA'
                state = 'CA'
                country = 'US'
                postalCode = '12345'
                it
            }
            phone = new Phone().with {
                type = 'Home'
                number = '12345678'
                it
            }
            it
        }
        return paymentInstrumentResource.postPaymentInstrument(user.id, pi).wrapped().get()
    }

    Order postQuotes(Order order) {
        order.tentative = true
        return orderResource.createOrders(order).wrapped().get().get(0)
    }

    Order settleQuotes(OrderId orderId) {
        def order = new Order()
        order.tentative = false
        return orderResource.updateOrderByOrderId(orderId, order).wrapped().get().get(0)
    }

    Order putQuotes(Order order) {
        order.tentative = true
        return orderResource.updateOrderByOrderId(order.id, order).wrapped().get().get(0)
    }

    Offer getOfferByName(String offerName) {
        def option = new OffersGetOptions()
        option.size = DEFAULT_PAGE_SIZE
        option.start = 0
        if (offers == null) {
            while (true) {
                offers = new ArrayList<>()
                def offerResults = offerResource.getOffers(option).wrapped().get()
                offers.addAll(offerResults.results)
                if (offerResults.results.size() < option.size) {
                    break
                }
                option.start += option.size
            }
        }
        return offers.find {
            it.name == offerName
        }
    }
}
