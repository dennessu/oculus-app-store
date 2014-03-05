/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.resource;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fzhang@wan-san.com on 14-1-20.
 */
@Path("users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CartResource {

    @POST
    @Path("/{userId}/carts")
    Promise<Cart> addCart(@PathParam("userId") Long userId, Cart cart);

    @GET
    @Path("/{userId}/carts/{cartId}")
    Promise<Cart> getCart(@PathParam("userId") Long userId, @PathParam("cartId") Long cartId);

    @GET
    @Path("/{userId}/carts/primary")
    Promise<Cart> getCartPrimary(@PathParam("userId") Long userId);

    @GET
    @Path("/{userId}/carts")
    Promise<Cart> getCartByName(@PathParam("userId") Long userId, @QueryParam("cartName") String cartName);

    @PUT
    @Path("/{userId}/carts/{cartId}")
    Promise<Cart> updateCart(@PathParam("userId") Long userId,
                                      @PathParam("cartId") Long cartId, Cart cart);

    @POST
    @Path("/{userId}/carts/{cartId}/merge")
    Promise<Cart> mergeCart(@PathParam("userId") Long userId,
                                     @PathParam("cartId") Long cartId, Cart fromCart);

    @POST
    @Path("/{userId}/carts/{cartId}/offers")
    Promise<Cart> addOffer(@PathParam("userId") Long userId,
                                    @PathParam("cartId") Long cartId, OfferItem offer);

    @PUT
    @Path("/{userId}/carts/{cartId}/offers/{offerItemId}")
    Promise<Cart> updateOffer(@PathParam("userId") Long userId, @PathParam("cartId") Long cartId,
                                           @PathParam("offerItemId") Long offerItemId, OfferItem offer);

    @DELETE
    @Path("/{userId}/carts/{cartId}/offers/{offerItemId}")
    Promise<Void> deleteOffer(@PathParam("userId") Long userId, @PathParam("cartId") Long cartId,
                                       @PathParam("offerItemId") Long offerItemId);

    @POST
    @Path("/{userId}/carts/{cartId}/coupons/")
    Promise<Cart> addCoupon(@PathParam("userId") Long userId, @PathParam("cartId") Long cartId,
                                     CouponItem couponItem);

    @DELETE
    @Path("/{userId}/carts/{cartId}/coupons/{couponItemId}")
    Promise<Void> deleteCoupon(@PathParam("userId") Long userId, @PathParam("cartId") Long cartId,
                                        @PathParam("couponItemId") Long couponItemId);
}
