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
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @RouteBy(value = "userGroup.getUserId()", switchable = true)
    @POST
    Promise<UserGroup> create(UserGroup userGroup);

    @ApiOperation("Get one user group membership")
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{userGroupMembershipId}")
    Promise<UserGroup> get(@PathParam("userGroupMembershipId") UserGroupId userGroupId,
                            @BeanParam UserGroupGetOptions getOptions);

    @ApiOperation("Update one user group membership")
    @RouteBy(value = "userGroup.getUserId()", switchable = true)
    @PUT
    @Path("/{userGroupMembershipId}")
    Promise<UserGroup> put(@PathParam("userGroupMembershipId") UserGroupId userGroupId,
                            UserGroup userGroup);

    @ApiOperation("Delete one user group membership")
    @RouteByAccessToken(switchable = true)
    @DELETE
    @Path("/{userGroupMembershipId}")
    Promise<Response> delete(@PathParam("userGroupMembershipId") UserGroupId userGroupId);

    @ApiOperation("search user group memberships")
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<UserGroup>> list(@BeanParam UserGroupListOptions listOptions);
}
