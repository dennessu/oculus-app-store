/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.common.id.OrderId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.spec.model.PageParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Created by chriszhu on 3/12/14.
 */
@Api("order-events")
@Path("order-events")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OrderEventResource {

    @ApiOperation("Get order events")
    @GET
    Promise<Results<OrderEvent>> getOrderEvents(@QueryParam("orderId") OrderId orderId, @BeanParam PageParam pageParam,
                                             @Context HttpHeaders headers);

    @POST
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent,
                                         @Context HttpHeaders headers);
}
