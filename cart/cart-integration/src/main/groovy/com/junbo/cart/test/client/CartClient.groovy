package com.junbo.cart.test.client

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.cart.spec.resource.CartResource
import com.junbo.cart.spec.resource.proxy.CartResourceClientProxy

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class CartClient extends BaseClient {

    Cart getPrimaryCart(Long userId) {
        return resource().getCartPrimary(userId).wrapped().get()
    }

    Cart updateCart(Long userId, Long cartId, Cart cart) {
        return resource().updateCart(userId, cartId, cart).wrapped().get()
    }

    Cart addOffer(Long userId, Long cartId, OfferItem item) {
        return resource().addOffer(userId, cartId, item).wrapped().get()
    }

    Cart updateOffer(Long userId, Long cartId, Long offerItemId, OfferItem item) {
        return resource().updateOffer(userId, cartId, offerItemId, item).wrapped().get()
    }

    Cart addCoupon(Long userId, Long cartId, CouponItem item) {
        return resource().addCoupon(userId, cartId, item).wrapped().get()
    }

    Cart deleteCoupon(Long userId, Long cartId, Long itemId) {
        resource().deleteCoupon(userId, cartId, itemId).wrapped().get()
        return resource().getCart(userId, cartId).wrapped().get()
    }

    Cart mergeCart(Long userId, Long cartId, Cart fromCart) {
        return resource().mergeCart(userId, cartId, fromCart).wrapped().get()
    }

    CartResource resource() {
        return new CartResourceClientProxy(asyncHttpClient, messageTranscoder, baseUrl)
    }
}
