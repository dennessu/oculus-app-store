package com.junbo.cart.test.client

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import com.junbo.common.id.UserId

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class CartClient {

    CartResource cartResource

    Cart getPrimaryCart(UserId userId) {
        return resource().getCartPrimary(userId).wrapped().get()
    }

    Cart updateCart(UserId userId, CartId cartId, Cart cart) {
        return resource().updateCart(userId, cartId, cart).wrapped().get()
    }

    Cart addOffer(UserId userId, CartId cartId, OfferItem item) {
        return resource().addOffer(userId, cartId, item).wrapped().get()
    }

    Cart updateOffer(UserId userId, CartId cartId, CartItemId offerItemId, OfferItem item) {
        return resource().updateOffer(userId, cartId, offerItemId, item).wrapped().get()
    }

    Cart addCoupon(UserId userId, CartId cartId, CouponItem item) {
        return resource().addCoupon(userId, cartId, item).wrapped().get()
    }

    Cart deleteCoupon(UserId userId, CartId cartId, CartItemId itemId) {
        resource().deleteCoupon(userId, cartId, itemId).wrapped().get()
        return resource().getCart(userId, cartId).wrapped().get()
    }

    Cart mergeCart(UserId userId, CartId cartId, Cart fromCart) {
        return resource().mergeCart(userId, cartId, fromCart).wrapped().get()
    }

    CartResource resource() {
        return cartResource
    }
}
