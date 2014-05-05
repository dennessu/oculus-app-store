package com.junbo.cart.test.client

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
@CompileStatic
class CartClient {

    CartResource cartResource

    Cart getPrimaryCart(UserId userId) {
        return resource().getCartPrimary(userId).wrapped().get()
    }

    Cart updateCart(UserId userId, CartId cartId, Cart cart) {
        return resource().updateCart(userId, cartId, cart).wrapped().get()
    }

    CartResource resource() {
        return cartResource
    }
}
