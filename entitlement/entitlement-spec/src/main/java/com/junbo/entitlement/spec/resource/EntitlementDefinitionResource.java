/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.model.ResultList;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API for entitlementDefinition.
 */
@Path("/entitlementDefinitions")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
public interface EntitlementDefinitionResource {
    @GET
    @Path("/{entitlementDefinitionId}")
    Promise<EntitlementDefinition> getEntitlementDefinition(
            @PathParam("entitlementDefinitionId") EntitlementDefinitionId entitlementDefinitionId);

    @GET
    Promise<ResultList<EntitlementDefinition>>
    getEntitlementDefinitionDefinitions(
            @QueryParam("developerId") UserId developerId,
            @QueryParam("type") String type,
            @QueryParam("group") String group,
            @QueryParam("tag") String tag,
            @BeanParam PageMetadata pageMetadata);

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<EntitlementDefinition> postEntitlementDefinition(
            @Valid EntitlementDefinition entitlementDefinition);

    @PUT
    @Path("/{entitlementDefinitionId}")
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<EntitlementDefinition> updateEntitlementDefinition(
            @PathParam("entitlementDefinitionId") EntitlementDefinitionId entitlementDefinitionId,
            @Valid EntitlementDefinition entitlementDefinition);

    @DELETE
    @Path("/{entitlementDefinitionId}")
    Promise<Response> deleteEntitlementDefinition(
            @PathParam("entitlementDefinitionId") EntitlementDefinitionId entitlementDefinitionId);
}
