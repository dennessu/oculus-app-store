package com.junbo.order.clientproxy.email

import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.model.Order
/**
 * Created by LinYi on 14-3-14.
 */
interface EmailFacade {
    Promise<Email> sendOrderConfirmationEmail(Order order, User user, List<Offer> offers)
    Promise<Email> sendOrderRefundEmail(Order order, User user, List<Offer> offer)
}
