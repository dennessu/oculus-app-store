/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.resource;

import com.junbo.common.id.SubscriptionId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.subscription.spec.model.Subscription;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * subscription dao.
 */
@Path("/subscriptions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface SubscriptionResource {
    @POST
    @Path("/")
    @RouteBy("request.getUserId()")
    Promise<Subscription> postSubscription(@Valid Subscription request);

    @GET
    @Path("/{subscriptionId}")
    @RouteBy("subscriptionId")
    Promise<Subscription> getSubscriptionById(@PathParam("subscriptionId") SubscriptionId subscriptionId);

}
