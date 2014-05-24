/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.rest.resource

import com.junbo.cart.core.service.CartService
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.ext.Provider

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
@Provider
@Component
@Scope('prototype')
@CompileStatic
class CartResourceImpl implements CartResource {

    static final String CLIENT_ID = 'Client-ID'

    private CartService cartService

    private PathParamTranscoder pathParamTranscoder

    @Required
    void setCartService(CartService cartService) {
        this.cartService = cartService
    }

    void setPathParamTranscoder(PathParamTranscoder pathParamTranscoder) {
        this.pathParamTranscoder = pathParamTranscoder
    }

    @Override
    Promise<Cart> addCart(UserId userId, Cart cart) {
        return cartService.addCart(cart, clientId, userId)
    }

    @Override
    Promise<Cart> getCart(UserId userId, CartId cartId) {
        return cartService.getCart(userId, cartId)
    }

    @Override
    Promise<Cart> getCartPrimary(UserId userId) {
        return cartService.getCartPrimary(clientId, userId).then {
            Cart cart = (Cart) it

            String location = JunboHttpContext.requestUri.toString().replaceFirst(
                    '/primary$', '/' + pathParamTranscoder.encode(cart.id))

            JunboHttpContext.responseStatus = 302
            JunboHttpContext.responseHeaders.add('Location', location)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<Cart>> getCartByName(UserId userId, String cartName) {
        return cartService.getCartByName(clientId, cartName, userId).syncThen { Cart cart ->
            def results = new Results()
            results.items = []
            if (cart != null) {
                results.items << cart
            }
            return results
        }
    }

    @Override
    Promise<Cart> updateCart(UserId userId, CartId cartId, Cart fromCart) {
        return cartService.updateCart(userId, cartId, fromCart)
    }

    private String getClientId() {
        return 'dev'
    }
}
