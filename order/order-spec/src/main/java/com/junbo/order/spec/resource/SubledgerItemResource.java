/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.common.id.OrderItemId;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.order.spec.model.SubledgerItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by chriszhu on 2/10/14.
 */
@Path("subledger-items")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@InProcessCallable
@RestResource
public interface SubledgerItemResource {

    @POST
    @RouteBy("subledgerItem.getOrderItem()")
    Promise<SubledgerItem> createSubledgerItem(SubledgerItem subledgerItem);

    @GET
    @RouteBy("orderItemId")
    Promise<List<SubledgerItem>> getSubledgerItemsByOrderItemId(@QueryParam("orderItemId") OrderItemId orderItemId);

    @POST
    @Path("/aggregate")
    @RouteBy("subledgerItems.get(0).getOrderItem()")
    Promise<Void> aggregateSubledgerItem(List<SubledgerItem> subledgerItems);
}
