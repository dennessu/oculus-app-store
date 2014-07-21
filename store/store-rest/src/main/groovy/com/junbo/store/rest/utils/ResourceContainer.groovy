package com.junbo.store.rest.utils
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.identity.spec.v1.resource.*
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.order.spec.resource.OrderResource
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Class to hold all the other sources IAP depends on.
 */
@CompileStatic
@Component('storeResourceContainer')
class ResourceContainer {

    @Resource(name='store.offerItemClient')
    ItemResource itemResource

    @Resource(name='store.offerClient')
    OfferResource offerResource

    @Resource(name='store.offerRevisionClient')
    OfferRevisionResource offerRevisionResource

    @Resource(name='store.offerItemRevisionClient')
    ItemRevisionResource itemRevisionResource

    @Resource(name='store.entitlementClient')
    EntitlementResource entitlementResource

    @Resource(name='store.fulfilmentClient')
    FulfilmentResource fulfilmentResource

    @Resource(name='store.orderClient')
    OrderResource orderResource

    @Resource(name='store.paymentInstrumentClient')
    PaymentInstrumentResource paymentInstrumentResource

    @Resource(name='store.itemCryptoClient')
    ItemCryptoResource itemCryptoResource

    @Resource(name='store.userResourceClient')
    UserResource userResource

    @Resource(name='store.userCredentialResourceClient')
    UserCredentialResource userCredentialResource

    @Resource(name='store.userPersonalInfoResourceClient')
    UserPersonalInfoResource userPersonalInfoResource

    @Resource(name='store.userCredentialVerifyAttemptResourceClient')
    UserCredentialVerifyAttemptResource userCredentialVerifyAttemptResource

    @Resource(name='store.tokenEndpointClientProxy')
    TokenEndpoint tokenEndpoint

    @Resource(name='store.tokenInfoEndpointClientProxy')
    TokenInfoEndpoint tokenInfoEndpoint

    @Resource(name='store.piTypeResourceClientProxy')
    PITypeResource piTypeResource

    @Resource(name='store.currencyResourceClientProxy')
    CurrencyResource currencyResource

    @Resource(name='store.emailVerifyEndpointClientProxy')
    EmailVerifyEndpoint emailVerifyEndpoint
}
