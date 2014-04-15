package com.junbo.order.clientproxy.model

import com.junbo.catalog.spec.model.offer.OfferRevision

/**
 * Created by chriszhu on 4/14/14.
 */
class OrderOfferRevision {
    OfferRevision catalogOfferRevision
    List<OrderOfferItemRevision> orderOfferItemRevisions
}
