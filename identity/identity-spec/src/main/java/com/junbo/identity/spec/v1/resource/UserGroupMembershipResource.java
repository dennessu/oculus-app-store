/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserGroup;
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions;
import com.junbo.identity.spec.v1.option.model.UserGroupGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "user-group-memberships")
@RestResource
@InProcessCallable
@Path("/user-group-memberships")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserGroupMembershipResource {
    @ApiOperation("Create one user group membership")
    @POST
    Promise<UserGroup> create(UserGroup userGroup);

    @ApiOperation("Get one user group membership")
    @GET
    @Path("/{userGroupMembershipId}")
    Promise<UserGroup> get(@PathParam("userGroupMembershipId") UserGroupId userGroupId,
                            @BeanParam UserGroupGetOptions getOptions);

    @POST
    @Path("/{userGroupMembershipId}")
    Promise<UserGroup> patch(@PathParam("userGroupMembershipId") UserGroupId userGroupId,
                              UserGroup userGroup);

    @ApiOperation("Update one user group membership")
    @PUT
    @Path("/{userGroupMembershipId}")
    Promise<UserGroup> put(@PathParam("userGroupMembershipId") UserGroupId userGroupId,
                            UserGroup userGroup);

    @ApiOperation("Delete one user group membership")
    @DELETE
    @Path("/{userGroupMembershipId}")
    Promise<Void> delete(@PathParam("userGroupMembershipId") UserGroupId userGroupId);

    @ApiOperation("search user group memberships")
    @GET
    Promise<Results<UserGroup>> list(@BeanParam UserGroupListOptions listOptions);
}
