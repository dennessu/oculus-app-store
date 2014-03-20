/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.resource;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.CartId;
import com.junbo.common.id.CartItemId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fzhang@wan-san.com on 14-1-20.
 */
@Api("carts")
@Path("users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CartResource {

    @ApiOperation("Create a cart")
    @POST
    @Path("/{userId}/carts")
    Promise<Cart> addCart(@PathParam("userId") UserId userId, Cart cart);

    @ApiOperation("Get a cart")
    @GET
    @Path("/{userId}/carts/{cartId}")
    Promise<Cart> getCart(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId);

    @ApiOperation("Get the primary cart")
    @GET
    @Path("/{userId}/carts/primary")
    Promise<Cart> getCartPrimary(@PathParam("userId") UserId userId);

    @ApiOperation("Get cart by name")
    @GET
    @Path("/{userId}/carts")
    Promise<Cart> getCartByName(@PathParam("userId") UserId userId, @QueryParam("cartName") String cartName);

    @ApiOperation("Get a cart")
    @PUT
    @Path("/{userId}/carts/{cartId}")
    Promise<Cart> updateCart(@PathParam("userId") UserId userId,
                                      @PathParam("cartId") CartId cartId, Cart cart);

    @ApiOperation("Merge the carts")
    @POST
    @Path("/{userId}/carts/{cartId}/merge")
    Promise<Cart> mergeCart(@PathParam("userId") UserId userId,
                                     @PathParam("cartId") CartId cartId, Cart fromCart);

    @POST
    @Path("/{userId}/carts/{cartId}/offers")
    Promise<Cart> addOffer(@PathParam("userId") UserId userId,
                                    @PathParam("cartId") CartId cartId, OfferItem offer);

    @PUT
    @Path("/{userId}/carts/{cartId}/offers/{offerItemId}")
    Promise<Cart> updateOffer(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId,
                                           @PathParam("offerItemId") CartItemId offerItemId, OfferItem offer);

    @DELETE
    @Path("/{userId}/carts/{cartId}/offers/{offerItemId}")
    Promise<Void> deleteOffer(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId,
                                       @PathParam("offerItemId") CartItemId offerItemId);

    @POST
    @Path("/{userId}/carts/{cartId}/coupons/")
    Promise<Cart> addCoupon(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId,
                                     CouponItem couponItem);

    @DELETE
    @Path("/{userId}/carts/{cartId}/coupons/{couponItemId}")
    Promise<Void> deleteCoupon(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId,
                                        @PathParam("couponItemId") CartItemId couponItemId);
}
