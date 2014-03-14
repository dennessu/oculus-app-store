/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserGroupGetOption;
import com.junbo.identity.spec.v1.model.users.UserGroup;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/groups/{groupId}")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserGroupResource {
    @POST
    @Path("/users")
    Promise<UserGroup> create(@PathParam("groupId") GroupId groupId,
                              UserGroup userGroup);

    @PUT
    @Path("/users/{userId}")
    Promise<UserGroup> update(@PathParam("groupId") GroupId groupId,
                              @PathParam("userId") UserId userId,
                              UserGroup userGroup);

    @POST
    @Path("/users/{userId}")
    Promise<UserGroup> patch(@PathParam("groupId") GroupId groupId,
                             @PathParam("userId") UserId userId,
                             UserGroup userGroup);

    @GET
    @Path("/users/{userId}")
    Promise<UserGroup> get(@PathParam("groupId") GroupId groupId,
                           @PathParam("userId") UserId userId);

    @DELETE
    @Path("/users/{userId}")
    Promise<UserGroup> delete(@PathParam("groupId") GroupId groupId,
                              @PathParam("userId") UserId userId);

    @GET
    @Path("/users")
    Promise<ResultList<UserGroup>> list(@PathParam("groupId") GroupId groupId,
                                        @BeanParam UserGroupGetOption getOption);
}
