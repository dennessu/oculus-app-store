/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API for entitlement.
 */
@Api("entitlements")
@Path("/entitlements")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface EntitlementResource {
    @ApiOperation("Get an entitlement by id")
    @GET
    @Path("/{entitlementId}")
    Promise<Entitlement> getEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @ApiOperation("Create an entitlement")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> postEntitlement(@Valid Entitlement entitlement);

    @ApiOperation("Update an entitlement")
    @PUT
    @Path("/{entitlementId}")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> updateEntitlement(
            @PathParam("entitlementId") EntitlementId entitlementId,
            @Valid Entitlement entitlement);

    @ApiOperation("Delete an entitlement")
    @DELETE
    @Path("/{entitlementId}")
    Promise<Response> deleteEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @ApiOperation("Search entitlements")
    @GET
    Promise<Results<Entitlement>> searchEntitlements(@BeanParam EntitlementSearchParam searchParam,
                                                     @BeanParam PageMetadata pageMetadata);
}
