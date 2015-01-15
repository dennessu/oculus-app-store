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
import com.junbo.entitlement.spec.model.RevokeRequest;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

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
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{entitlementId}")
    Promise<Entitlement> getEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @ApiOperation("Create an entitlement")
    @RouteBy(value = "entitlement.getUserId()", switchable = true)
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> postEntitlement(@Valid Entitlement entitlement);

    @RouteByAccessToken(switchable = true)
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/bulk")
    Promise<Map<Long, List<Entitlement>>> postEntitlements(Map<Long, List<Entitlement>> entitlements);

    @ApiOperation("Update an entitlement")
    @RouteBy(value = "entitlement.getUserId()", switchable = true)
    @PUT
    @Path("/{entitlementId}")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> updateEntitlement(
            @PathParam("entitlementId") EntitlementId entitlementId,
            @Valid Entitlement entitlement);

    @ApiOperation("Delete an entitlement")
    @RouteByAccessToken(switchable = true)
    @DELETE
    @Path("/{entitlementId}")
    Promise<Response> deleteEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @ApiOperation("Search entitlements")
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<Entitlement>> searchEntitlements(@BeanParam EntitlementSearchParam searchParam,
                                                     @BeanParam PageMetadata pageMetadata);

    @RouteByAccessToken(switchable = true)
    @POST
    Promise<Response> revokeEntitlement(RevokeRequest revokeRequest);
}
