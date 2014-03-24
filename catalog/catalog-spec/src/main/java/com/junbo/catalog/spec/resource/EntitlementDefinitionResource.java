/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * API for entitlementDefinition.
 */
@Api("entitlement-definitions")
@Path("/entitlement-definitions")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
public interface EntitlementDefinitionResource {

    @ApiOperation("Get an entitlement definition")
    @GET
    @Path("/{entitlementDefinitionId}")
    Promise<EntitlementDefinition> getEntitlementDefinition(
            @PathParam("entitlementDefinitionId") EntitlementDefinitionId entitlementDefinitionId);

    @ApiOperation("Get or search entitlement definitions")
    @GET
    Promise<Results<EntitlementDefinition>>
    getEntitlementDefinitionDefinitions(
            @NotNull @QueryParam("developerId") UserId developerId,
            @QueryParam("type") String type,
            @QueryParam("group") String group,
            @QueryParam("tag") String tag,
            @BeanParam PageableGetOptions pageMetadata);

    @ApiOperation("Create an entitlement definitions")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    Promise<EntitlementDefinition> postEntitlementDefinition(
            @Valid EntitlementDefinition entitlementDefinition);
}
