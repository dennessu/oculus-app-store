/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.adyen;

import com.adyen.services.payment.PaymentResult;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * adyen rest Api.
 */
@RestResource
@Path("/")
public interface AdyenApi {
    @POST
    @Path("/httppost")
    Promise<PaymentResult> authorise(AdyenPaymentRequest request);
}
