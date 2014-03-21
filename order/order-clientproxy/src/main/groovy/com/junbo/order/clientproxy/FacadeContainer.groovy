package com.junbo.order.clientproxy

import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 14-3-17.
 */
@Component('orderFacadeContainer')
@CompileStatic
class FacadeContainer {
    @Autowired
    @Qualifier('orderPaymentFacade')
    PaymentFacade paymentFacade
    @Autowired
    @Qualifier('orderBillingFacade')
    BillingFacade billingFacade
    @Autowired
    @Qualifier('orderRatingFacade')
    RatingFacade ratingFacade
    @Autowired
    @Qualifier('orderIdentityFacade')
    IdentityFacade identityFacade
    @Autowired
    @Qualifier('orderFulfillmentFacade')
    FulfillmentFacade fulfillmentFacade
    @Autowired
    @Qualifier('orderEmailFacade')
    EmailFacade emailFacade
    @Autowired
    @Qualifier('cachedCatalogFacade')
    CatalogFacade catalogFacade
}
