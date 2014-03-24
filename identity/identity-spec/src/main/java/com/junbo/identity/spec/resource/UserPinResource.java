/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.entity.UserPinGetOptions;
import com.junbo.identity.spec.options.list.UserPinListOption;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/pins")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPinResource {

    @ApiOperation("create user pin")
    @POST
    @Path("/")
    Promise<UserPin> create(@PathParam("userId") UserId userId, UserPin userPin);

    @ApiOperation("get user pin")
    @GET
    @Path("/{userPinId}")
    Promise<UserPin> get(@PathParam("userId") UserId userId,
                         @PathParam("userPinId") UserPinId userPinId,
                         @BeanParam UserPinGetOptions getOptions);

    @ApiOperation("search user pins")
    @GET
    @Path("/")
    Promise<Results<UserPin>> list(@PathParam("userId") UserId userId,
                                      @BeanParam UserPinListOption listOptions);

}
