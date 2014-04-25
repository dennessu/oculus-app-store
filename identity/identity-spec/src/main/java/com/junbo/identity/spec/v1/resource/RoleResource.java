/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.RoleId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Role;
import com.junbo.identity.spec.v1.option.list.RoleListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * RoleResource.
 */
@Api("roles")
@RestResource
@Path("/roles")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface RoleResource {
    @ApiOperation("Create a role")
    @POST
    Promise<Role> create(Role role);

    @ApiOperation("Get a role")
    @GET
    @Path("/{roleId}")
    Promise<Role> get(@PathParam("roleId") RoleId roleId);

    @POST
    @Path("/{roleId}")
    Promise<Role> patch(@PathParam("roleId") RoleId roleId, Role role);

    @ApiOperation("Full update one role")
    @PUT
    @Path("/{roleId}")
    Promise<Role> put(@PathParam("roleId") RoleId roleId, Role role);

    @ApiOperation("search role")
    @GET
    Promise<Results<Role>> list(@BeanParam RoleListOptions listOptions);
}
