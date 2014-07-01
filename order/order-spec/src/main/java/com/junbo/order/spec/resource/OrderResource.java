/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderQueryParam;
import com.junbo.order.spec.model.PageParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by chriszhu on 2/10/14.
 */
@Api("orders")
@Path("orders")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface OrderResource {

    @ApiOperation("Create an order")
    @POST
    @RouteBy("order.getUser()")
    Promise<Order> createOrder(Order order);

    @ApiOperation("Get an order")
    @GET
    @Path("/{orderId}")
    @RouteBy("orderId")
    Promise<Order> getOrderByOrderId(@PathParam("orderId") OrderId orderId);

    @ApiOperation("Get orders by user")
    @GET
    @RouteBy(value = "userId", fallbackToAnyLocal = true)
    Promise<Results<Order>> getOrderByUserId(@QueryParam("userId") UserId userId,
                                             @BeanParam OrderQueryParam orderQueryParam,
                                             @BeanParam PageParam pageParam);

    @ApiOperation("Put an order")
    @PUT
    @Path("/{orderId}")
    @RouteBy("orderId")
    Promise<Order> updateOrderByOrderId(@PathParam("orderId") OrderId orderId, Order order);
}
