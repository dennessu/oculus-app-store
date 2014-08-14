/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by xmchen on 14-8-14.
 */
@Path("/address-validator")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@InProcessCallable
public interface AddressValidatorResource {
    @GET
    // This won't touch any DB, so it doesn't need to do route.
    Promise<Address> validateAddress(Address address);
}
