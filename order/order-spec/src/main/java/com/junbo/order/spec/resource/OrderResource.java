/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by chriszhu on 2/10/14.
 */
@Api("orders")
@Path("orders")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OrderResource {

    @ApiOperation("Create an order")
    @POST
    Promise<List<Order>> createOrders(Order order);

    @ApiOperation("Get an order")
    @GET
    @Path("/{orderId}")
    Promise<Order> getOrderByOrderId(@PathParam("orderId") OrderId orderId);

    @ApiOperation("Get orders by user")
    @GET
    Promise<List<Order>> getOrderByUserId(@QueryParam("userId") UserId userId);

    @ApiOperation("Put an order")
    @PUT
    @Path("/{orderId}")
    Promise<List<Order>> updateOrderByOrderId(@PathParam("orderId") OrderId orderId, Order order);
}

