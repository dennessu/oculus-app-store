package com.junbo.cart.core.validation

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User

/**
 * Created by fzhang@wan-san.com on 14-1-23.
 */
interface Validation {

    Validation validateUser(UserId userId, User user)

    Validation validateCartAdd(String clientId, UserId userId, Cart cart)

    Validation validateCartUpdate(Cart newCart, Cart oldCart)

    Validation validateCartOwner(Cart cart, UserId userId)

    Validation validateMerge(Cart mergeCart)

    Validation validateOfferAdd(OfferItem offerItem)

    Validation validateOfferUpdate(OfferItem offerItem)
}