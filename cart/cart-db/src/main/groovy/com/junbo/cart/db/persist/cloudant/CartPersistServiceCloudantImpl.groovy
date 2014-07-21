/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist.cloudant
import com.junbo.cart.common.util.SystemOperation
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.spec.model.Cart
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by fzhang@wan-san.com on 14-1-28.
 */
@CompileStatic
class CartPersistServiceCloudantImpl extends CloudantClient<Cart> implements CartPersistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartPersistServiceCloudantImpl)

    private SystemOperation systemOperation

    void setSystemOperation(SystemOperation systemOperation) {
        this.systemOperation = systemOperation
    }

    @Override
    Promise<Cart> get(CartId cartId) {
        return cloudantGet(cartId.toString());
    }

    @Override
    Promise<Cart> get(String clientId, String cartName, UserId userId) {
        return queryView("by_client_name_user", clientId + ":" + userId.toString() + ":" + cartName).then { List<Cart> carts ->
            return Promise.pure(carts.size() == 0 ? null : carts.get(0));
        }
    }

    @Override
    Promise<Cart> create(Cart newCart) {
        // add cart
        Date currentTime = systemOperation.currentTime()
        newCart.createdTime = currentTime
        newCart.updatedTime = currentTime
        fillMissingField(newCart)

        return cloudantPost(newCart)
    }

    @Override
    Promise<Cart> update(Cart cart, Cart oldCart) {
        // update cart
        Date currentTime = systemOperation.currentTime()
        cart.updatedTime = currentTime
        fillMissingField(cart)
        return cloudantPut(cart, oldCart)
    }

    private static void fillMissingField(Cart cart) {
        cart.offers = cart.offers == null ? [] as List : cart.offers
        cart.coupons = cart.coupons == null ? [] as List : cart.coupons
    }
}
