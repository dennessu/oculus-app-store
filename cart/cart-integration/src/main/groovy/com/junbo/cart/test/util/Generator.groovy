package com.junbo.cart.test.util

import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.OfferId
import org.apache.commons.lang.RandomStringUtils
/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class Generator {

    private long nextId = System.currentTimeMillis()

    OfferItem offerItem() {
        return offerItem(genNextId())
    }

    OfferItem offerItem(long offerId) {
        def ret = new OfferItem()
        ret.quantity = 1
        ret.offer = new OfferId(offerId)
        ret.selected = true
        return ret
    }

    String couponCode() {
        return RandomStringUtils.randomAlphabetic(20)
    }

    private long genNextId() {
        return nextId++
    }
}
