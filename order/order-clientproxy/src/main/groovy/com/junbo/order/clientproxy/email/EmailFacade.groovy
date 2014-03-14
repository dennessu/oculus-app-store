package com.junbo.order.clientproxy.email

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order

/**
 * Created by LinYi on 14-3-14.
 */
interface EmailFacade {
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<Offer> offers)
}
