package com.junbo.cart.test.validate

import com.junbo.cart.spec.model.Cart
import com.junbo.common.id.UserId

/**
 * Created by fzhang on 14-3-3.
 */
class CartValidator {

    private Cart result

    static CartValidator fromResult(Cart cart) {
        def val = new CartValidator()
        val.result = cart
        return val
    }

    CartValidator resourceAge(Long resourceAge) {
        assert resourceAge.toString() == result.resourceAge
        return this
    }

    CartValidator user(UserId userId) {
        assert userId == result.user
        return this
    }

    CartValidator updatedTime(Date updatedTime) {
        assert updatedTime == result.updatedTime
        return this
    }

    CartValidator offerNumber(int offerNum) {
        assert offerNum == result.offers.size()
        return this
    }

    CartValidator couponNumber(int couponNum) {
        assert couponNum == result.coupons.size()
        return this
    }
}
