/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.billing.spec.model.VatIdValidationResponse;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by LinYi on 2014/6/16.
 */
@Path("/vat")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@InProcessCallable
public interface VatResource {
    @GET
    @Path("/{vatId}")
    Promise<VatIdValidationResponse> validateVatId(@PathParam("vatId") String vatId);
}

