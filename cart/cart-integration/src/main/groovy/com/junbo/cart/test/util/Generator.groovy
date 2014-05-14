package com.junbo.cart.test.util
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CouponId
import com.junbo.common.id.OfferId
import groovy.transform.CompileStatic

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
@CompileStatic
class Generator {

    private long nextId = System.currentTimeMillis()

    OfferItem offerItem() {
        return offerItem(genNextId())
    }

    OfferItem offerItem(long offerId) {
        def ret = new OfferItem()
        ret.quantity = 1
        ret.offer = new OfferId(offerId)
        ret.isSelected = true
        return ret
    }

    CouponId coupon() {
        return new CouponId(genNextId())
    }

    private long genNextId() {
        return nextId++
    }
}
