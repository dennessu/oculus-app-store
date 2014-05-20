/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.resource;

import com.junbo.common.id.RoleAssignmentId;
import com.junbo.common.model.Results;
import com.junbo.authorization.spec.model.RoleAssignment;
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * RoleAssignmentResource.
 */
@Api("authorization")
@RestResource
@Path("/role-assignments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface RoleAssignmentResource {

    @ApiOperation("Create a role assignment")
    @POST
    Promise<RoleAssignment> create(RoleAssignment roleAssignment);

    @ApiOperation("Get a role assignment")
    @GET
    @Path("/{roleAssignmentId}")
    Promise<RoleAssignment> get(@PathParam("roleAssignmentId") RoleAssignmentId roleAssignmentId);


    @ApiOperation("Delete a role assignment")
    @DELETE
    @Path("/{roleAssignment}")
    Promise<Void> delete(@PathParam("roleAssignment") RoleAssignmentId roleAssignmentId);

    @ApiOperation("search role assignment")
    @GET
    Promise<Results<RoleAssignment>> list(@BeanParam RoleAssignmentListOptions listOptions);
}
