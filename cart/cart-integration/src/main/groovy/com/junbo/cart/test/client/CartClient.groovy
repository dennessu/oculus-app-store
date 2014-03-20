package com.junbo.cart.test.client

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
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

    Cart mergeCart(UserId userId, CartId cartId, Cart fromCart) {
        return resource().mergeCart(userId, cartId, fromCart).wrapped().get()
    }

    CartResource resource() {
        return cartResource
    }
}
