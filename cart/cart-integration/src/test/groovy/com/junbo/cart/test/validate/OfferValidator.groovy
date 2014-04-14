package com.junbo.cart.test.validate

import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.OfferId

/**
 * Created by fzhang on 14-3-3.
 */
class OfferValidator {

    private OfferItem result

    static OfferValidator fromResult(OfferItem offerItem) {
        def val = new OfferValidator()
        val.result = offerItem
        return val
    }

    OfferValidator offer(OfferId offer) {
        assert offer == result.offer
        return this
    }

    OfferValidator quantity(Long quantity) {
        assert quantity == result.quantity
        return this
    }

    OfferValidator selected (Boolean selected) {
        assert selected == result.selected
        return this
    }
}
