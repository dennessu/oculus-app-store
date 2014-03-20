package com.junbo.cart.test.util

import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CouponId
import com.junbo.common.id.OfferId

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
        return ret
    }

    CouponItem couponItem() {
        return couponItem(genNextId())
    }

    CouponItem couponItem(long couponId) {
        def ret = new CouponItem()
        ret.coupon = new CouponId(couponId)
        return ret
    }

    private long genNextId() {
        return nextId++
    }
}
