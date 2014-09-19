/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.resource;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.id.CartId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.common.swagger.ApiErrors;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fzhang@wan-san.com on 14-1-20.
 */
@Api("carts")
@ApiErrors(value = ApiErrors.class, errors = { "userNotFound", "userStatusInvalid" })
@Path("/users/{userId}/carts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CartResource {

    @ApiOperation("Create a cart")
    @ApiErrors(errors = { "cartAlreadyExists", "fieldInvalid" })
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<Cart> addCart(@PathParam("userId") UserId userId, Cart cart);

    @ApiOperation("Get a cart")
    @ApiErrors(errors = "cartNotFound")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{cartId}")
    Promise<Cart> getCart(@PathParam("userId") UserId userId, @PathParam("cartId") CartId cartId);

    @ApiOperation("Get the primary cart")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/primary")
    Promise<Cart> getCartPrimary(@PathParam("userId") UserId userId);

    @ApiOperation("Get cart by name")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<Cart>> getCartByName(@PathParam("userId") UserId userId, @QueryParam("cartName") String cartName);

    @ApiOperation("Update a cart")
    @ApiErrors(errors = { "cartNotFound", "fieldInvalid" })
    @RouteBy(value = "userId", switchable = true)
    @PUT
    @Path("/{cartId}")
    Promise<Cart> updateCart(@PathParam("userId") UserId userId,
                                      @PathParam("cartId") CartId cartId, Cart cart);
}
