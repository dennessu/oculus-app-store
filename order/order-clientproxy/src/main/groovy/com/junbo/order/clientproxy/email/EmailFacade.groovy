package com.junbo.order.clientproxy.email

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order

/**
 * Created by LinYi on 14-3-14.
 */
interface EmailFacade {
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<OfferRevision> offers)
}
