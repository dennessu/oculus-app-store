/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.common.id.PaymentId;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentCallbackParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * payment callback resource interface.
 */
@Path("/payment-callback")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
@InProcessCallable
public interface PaymentCallbackResource {
    @POST
    @Path("/{paymentId}/properties")
    Promise<Response> postPaymentProperties(@PathParam("paymentId") PaymentId paymentId, PaymentCallbackParams properties);
}
