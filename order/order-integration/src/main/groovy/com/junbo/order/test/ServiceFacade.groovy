package com.junbo.order.test

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

    @Autowired
    UserResource userResource

    @Autowired
    OrderResource orderResource

    @Autowired
    PaymentInstrumentResource paymentInstrumentResource

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

    Order putQuotes(Order order) {
        order.tentative = true
        return orderResource.updateOrderByOrderId(order.id, order).wrapped().get().get(0)
    }
}
