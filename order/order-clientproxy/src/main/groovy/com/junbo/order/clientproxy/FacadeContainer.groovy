package com.junbo.order.clientproxy

import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.clientproxy.entitlement.EntitlementFacade
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.clientproxy.identity.CountryFacade
import com.junbo.order.clientproxy.identity.CurrencyFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 14-3-17.
 */
@Component('orderFacadeContainer')
@CompileStatic
@TypeChecked
class FacadeContainer {
    @Autowired
    @Qualifier('orderPaymentFacade')
    PaymentFacade paymentFacade
    @Autowired
    @Qualifier('orderAsyncBillingFacade')
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
    @Qualifier('orderCatalogFacade')
    CatalogFacade catalogFacade
    @Autowired
    @Qualifier('orderCountryFacade')
    CountryFacade countryFacade
    @Autowired
    @Qualifier('orderCurrencyFacade')
    CurrencyFacade currencyFacade
    @Autowired
    @Qualifier('orderEntitlementFacade')
    EntitlementFacade entitlementFacade
}
