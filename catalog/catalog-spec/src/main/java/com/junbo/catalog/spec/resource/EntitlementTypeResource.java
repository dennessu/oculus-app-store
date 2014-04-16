/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * entitlement type resource interface.
 */
@Api("entitlement-types")
@Path("/entitlement-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface EntitlementTypeResource {
    @ApiOperation("Get entitlement type info")
    @GET
    @Path("/{entitlementType}")
    Promise<EntitlementType> getById(@PathParam("entitlementType") String entitlementType);
}
