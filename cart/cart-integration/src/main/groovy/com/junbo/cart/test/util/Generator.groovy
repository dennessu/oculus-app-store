package com.junbo.cart.test.util

import com.junbo.cart.spec.model.Coupon
import com.junbo.cart.spec.model.Offer
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
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
        ret.offer = new Offer()
        ret.offer.id = offerId
        return ret
    }

    CouponItem couponItem() {
        return couponItem(RandomStringUtils.randomAlphabetic(5) + '-' + genNextId())
    }

    CouponItem couponItem(String couponId) {
        def ret = new CouponItem()
        ret.coupon = new Coupon()
        ret.coupon.id = couponId
        return ret
    }

    private long genNextId() {
        return nextId++
    }
}
