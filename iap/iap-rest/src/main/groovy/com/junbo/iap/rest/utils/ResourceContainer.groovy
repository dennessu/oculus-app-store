package com.junbo.iap.rest.utils

import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.order.spec.resource.OrderResource
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Class to hold all the other sources IAP depends on.
 */
@CompileStatic
@Component('iapResourceContainer')
class ResourceContainer {

    @Resource(name='iap.offerItemClient')
    ItemResource itemResource

    @Resource(name='iap.offerClient')
    OfferResource offerResource

    @Resource(name='iap.offerRevisionClient')
    OfferRevisionResource offerRevisionResource

    @Resource(name='iap.offerItemRevisionClient')
    ItemRevisionResource itemRevisionResource

    @Resource(name='iap.entitlementClient')
    EntitlementResource entitlementResource

    @Resource(name='iap.fulfilmentClient')
    FulfilmentResource fulfilmentResource

    @Resource(name='iap.orderClient')
    OrderResource orderResource

    @Resource(name='iap.paymentInstrumentClient')
    PaymentInstrumentResource paymentInstrumentResource

    @Resource(name='iap.itemCryptoClient')
    ItemCryptoResource itemCryptoResource
}
