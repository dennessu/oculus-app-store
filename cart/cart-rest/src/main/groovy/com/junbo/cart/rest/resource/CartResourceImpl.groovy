/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.rest.resource

import com.google.common.base.Function
import com.junbo.cart.core.service.CartService
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerResponse
import org.glassfish.jersey.server.internal.process.RespondingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
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

    @Autowired
    private ContainerRequestContext requestContext

    @Autowired
    private RespondingContext respondingContext

    private CartService cartService

    private PathParamTranscoder pathParamTranscoder

    @Required
    void setCartService(CartService cartService) {
        this.cartService = cartService
    }

    void setPathParamTranscoder(PathParamTranscoder pathParamTranscoder) {
        this.pathParamTranscoder = pathParamTranscoder
    }

    private class RedirectFunction implements Function<ContainerResponse, ContainerResponse> {

        private Cart cart

        private final ContainerRequestContext containerRequestContext

        RedirectFunction(Cart cart, ContainerRequestContext containerRequestContext) {
            this.cart = cart
            this.containerRequestContext = containerRequestContext
        }

        @Override
        ContainerResponse apply(ContainerResponse response) {
            if (cart != null) {
                response.status = 302
                response.headers['Location'] =
                        [ "${containerRequestContext.uriInfo.baseUri}" +
                                "users/${pathParamTranscoder.encode(cart.user)}/" +
                                "carts/${pathParamTranscoder.encode(cart.id)}"]
            }
            return response
        }
    }

    private class DeleteFunction implements Function<ContainerResponse, ContainerResponse> {
        @Override
        ContainerResponse apply(ContainerResponse response) {
            if (response.status < 400) {
                response.status = 204
            }
            return response
        }
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
            respondingContext.push(new RedirectFunction(cart, requestContext))
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
        // todo need to change once client id is final
        if (requestContext != null && requestContext.headers[CLIENT_ID] != null &&
                !requestContext.headers[CLIENT_ID].isEmpty()) {
            return requestContext.headers[CLIENT_ID].iterator().next()
        }
        return 'dev'
    }
}
