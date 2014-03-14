/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.resource;

import com.junbo.common.id.SubscriptionId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.subscription.spec.model.Subscription;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
    Promise<Subscription> postSubscription(Subscription request);

    @GET
    @Path("/{subscriptionId}")
    Promise<Subscription> getSubscription(@Context HttpHeaders httpHeaders,
                                                 @PathParam("subscriptionId") SubscriptionId subscriptionId);

}
