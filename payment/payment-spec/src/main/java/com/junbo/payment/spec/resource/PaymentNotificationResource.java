/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * payment callback resource interface.
 */
@Path("/notification")
@RestResource
@InProcessCallable
@AuthorizationNotRequired
public interface PaymentNotificationResource {
    //use String other than Object, avoid interface changes from provider
    //Won't enable api level route, in sql level, it will do route
    @POST
    @Path("/adyen")
    Promise<Response> receiveAdyenNotification(String request);

    @GET
    @Path("/facebook-rtu")
    Promise<Response> receiveFacebookNotification(@Context UriInfo uriInfo);

    @POST
    @Path("/facebook-rtu")
    Promise<Response> receiveFacebookNotification(String request);
}
