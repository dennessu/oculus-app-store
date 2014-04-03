/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.entity.UserGroupGetOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("users")
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserGroupResource {

    @POST
    @Path("/{userId}/groups")
    Promise<UserGroup> create(@PathParam("userId") UserId userId,
                              UserGroup userGroup);

    @PUT
    @Path("/{userId}/groups/{userGroupId}")
    Promise<UserGroup> put(@PathParam("userId") UserId userId,
                           @PathParam("userGroupId") UserGroupId userGroupId,
                           UserGroup userGroup);

    @POST
    @Path("/{userId}/groups/{userGroupId}")
    Promise<UserGroup> patch(@PathParam("userId") UserId userId,
                             @PathParam("userGroupId") UserGroupId userGroupId,
                             UserGroup userGroup);

    @DELETE
    @Path("/{userId}/groups/{userGroupId}")
    Promise<UserGroup> delete(@PathParam("userId") UserId userId,
                              @PathParam("userGroupId") UserGroupId userGroupId);

    @GET
    @Path("/{userId}/groups/{userGroupId}")
    Promise<UserGroup> get(@PathParam("userId") UserId userId,
                           @PathParam("userGroupId") UserGroupId userGroupId,
                           @BeanParam UserGroupGetOptions getOptions);

    @GET
    @Path("/{userId}/groups")
    Promise<Results<UserGroup>> list(@PathParam("userId") UserId userId,
                                        @BeanParam UserGroupListOptions listOptions);

}
