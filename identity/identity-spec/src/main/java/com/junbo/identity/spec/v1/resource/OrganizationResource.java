/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.OrganizationId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions;
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liangfu on 5/22/14.
 */
@Api(value = "organizations")
@RestResource
@InProcessCallable
@Path("/organizations")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface OrganizationResource {

    @ApiOperation("Create an organization")
    @RouteBy(value = "organization.getOwnerId()", switchable = true)
    @POST
    Promise<Organization> create(Organization organization);

    @ApiOperation("Update an organization")
    @RouteBy(value = "organizationId", switchable = true)
    @PUT
    @Path("/{organizationId}")
    Promise<Organization> put(@PathParam("organizationId") OrganizationId organizationId, Organization organization);

    @ApiOperation("Get a organization info")
    @RouteBy(value = "organizationId", switchable = true)
    @GET
    @Path("/{organizationId}")
    Promise<Organization> get(@PathParam("organizationId") OrganizationId organizationId,
                              @BeanParam OrganizationGetOptions getOptions);

    @ApiOperation("Search organization info")
    @RouteBy(value = "listOptions.getOwnerId()", switchable = true)
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<Organization>> list(@BeanParam OrganizationListOptions listOptions);

    @ApiOperation("Delete an organization")
    @RouteBy(value = "organizationId", switchable = true)
    @DELETE
    @Path("/{organizationId}")
    Promise<Response> delete(@PathParam("organizationId") OrganizationId organizationId);
}
