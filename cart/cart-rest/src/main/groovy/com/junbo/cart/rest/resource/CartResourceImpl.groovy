/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.rest.resource

import com.google.common.base.Function
import com.junbo.cart.core.service.CartService
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.cart.spec.resource.CartResource
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import com.junbo.common.id.UserId
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

    @Required
    void setCartService(CartService cartService) {
        this.cartService = cartService
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
                        [ "${containerRequestContext.uriInfo.baseUri}users/${cart.user.value}/carts/${cart.id.value}"]
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
    Promise<Cart> addCart(Long userId, Cart cart) {
        return cartService.addCart(cart, clientId, new UserId(userId))
    }

    @Override
    Promise<Cart> getCart(Long userId, Long cartId) {
        return cartService.getCart(new UserId(userId), new CartId(cartId))
    }

    @Override
    Promise<Cart> getCartPrimary(Long userId) {
        return cartService.getCartPrimary(clientId, new UserId(userId)).then {
            Cart cart = (Cart) it
            respondingContext.push(new RedirectFunction(cart, requestContext))
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Cart> getCartByName(Long userId, String cartName) {
        return cartService.getCartByName(clientId, cartName, new UserId(userId)).then {
            Cart cart = (Cart) it
            respondingContext.push(new RedirectFunction(cart, requestContext))
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Cart> updateCart(Long userId, Long cartId, Cart fromCart) {
        return cartService.updateCart(new UserId(userId), new CartId(cartId), fromCart)
    }

    @Override
    Promise<Cart> mergeCart(Long userId, Long cartId,  Cart fromCart) {
        return cartService.mergeCart(new UserId(userId), new CartId(cartId), fromCart)
    }

    @Override
    Promise<Cart> addOffer(Long userId, Long cartId, OfferItem offer) {
        return cartService.addOfferItem(new UserId(userId), new CartId(cartId), offer)
    }

    @Override
    Promise<Cart> updateOffer(Long userId, Long cartId, Long offerItemId, OfferItem offer) {
        return cartService.updateOfferItem(new UserId(userId), new CartId(cartId), new CartItemId(offerItemId), offer)
    }

    @Override
    Promise<Void> deleteOffer(Long userId, Long cartId, Long offerItemId) {
        return cartService.deleteOfferItem(new UserId(userId), new CartId(cartId), new CartItemId(offerItemId)).then {
            respondingContext.push(new DeleteFunction())
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Cart> addCoupon(Long userId, Long cartId, CouponItem couponItem) {
        return cartService.addCouponItem(new UserId(userId), new CartId(cartId), couponItem)
    }

    @Override
    Promise<Cart> deleteCoupon(Long userId, Long cartId, Long couponItemId) {
        return cartService.deleteCouponItem(new UserId(userId), new CartId(cartId), new CartItemId(couponItemId)).then {
            respondingContext.push(new DeleteFunction())
            return Promise.pure(null)
        }
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
