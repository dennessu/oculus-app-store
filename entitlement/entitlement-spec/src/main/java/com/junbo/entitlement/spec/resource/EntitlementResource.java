/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.*;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API for entitlement.
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
public interface EntitlementResource {
    @GET
    @Path("entitlements/{entitlementId}")
    Promise<Entitlement> getEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @GET
    @Path("users/{userId}/entitlements")
    Promise<ResultList<Entitlement>> getEntitlements(
            @PathParam("userId") UserId userId,
            @BeanParam EntitlementSearchParam searchParam,
            @BeanParam PageMetadata pageMetadata);

    @POST
    @Path("entitlements")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> postEntitlement(@Valid Entitlement entitlement);

    @PUT
    @Path("entitlements/{entitlementId}")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> updateEntitlement(
            @PathParam("entitlementId") EntitlementId entitlementId,
            @Valid Entitlement entitlement);

    @DELETE
    @Path("entitlements/{entitlementId}")
    Promise<Response> deleteEntitlement(@PathParam("entitlementId") EntitlementId entitlementId);

    @POST
    @Path("entitlements/transfer")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<Entitlement> transferEntitlement(@Valid EntitlementTransfer entitlementTransfer);
}
