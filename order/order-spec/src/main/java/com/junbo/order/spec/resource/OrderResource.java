/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Discount;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.spec.model.OrderItem;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by chriszhu on 2/10/14.
 */
@Path("orders")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OrderResource {

    @POST
    Promise<List<Order>> createOrders(Order order,
                                      @Context HttpHeaders headers);

    @GET
    @Path("/{orderId}")
    Promise<Order> getOrderByOrderId(@PathParam("orderId") Long orderId,
                                     @Context HttpHeaders headers);

    @PUT
    @Path("/{orderId}")
    Promise<List<Order>> updateOrderByOrderId(@PathParam("orderId") Long orderId, Order order,
                                           @Context HttpHeaders headers);

    @GET
    @Path("/{orderId}/order-events")
    Promise<List<OrderEvent>> getOrderEvents(@PathParam("orderId") Long orderId,
                                             @Context HttpHeaders headers);

    @POST
    @Path("/{orderId}/order-events")
    Promise<OrderEvent> createOrderEvent(@PathParam("orderId") Long orderId,
                                         @Context HttpHeaders headers);

    @GET
    @Path("/{orderId}/discounts")
    Promise<List<Discount>> getOrderDiscounts(@PathParam("orderId") Long orderId,
                                              @Context HttpHeaders headers);

    @GET
    @Path("/{orderId}/order-items")
    Promise<List<OrderItem>> getOrderItemsByOrderId(@PathParam("orderId") Long orderId,
                                                    @Context HttpHeaders headers);
}

