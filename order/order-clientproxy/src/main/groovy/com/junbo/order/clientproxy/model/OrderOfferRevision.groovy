package com.junbo.order.clientproxy.model

import com.junbo.catalog.spec.model.offer.OfferRevision
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 4/14/14.
 */
@CompileStatic
class OrderOfferRevision {
    OfferRevision catalogOfferRevision
    List<OrderOfferItem> orderOfferItems
}
