package com.junbo.cart.test.validate

import com.junbo.cart.spec.model.Coupon
import com.junbo.cart.spec.model.item.CouponItem

/**
 * Created by fzhang on 14-3-3.
 */
class CouponValidator {

    private CouponItem result

    static CouponValidator fromResult(CouponItem couponItem) {
        def val = new CouponValidator()
        val.result = couponItem
        return val
    }

    CouponValidator coupon(Coupon coupon) {
        assert coupon.id == result.coupon.id
    }
}
