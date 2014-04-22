/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTeleAttempt;
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserTeleAttemptGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/22/14.
 */
@Api(value = "users")
@RestResource
@Path("/users/{userId}/tele-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTeleAttemptResource {

    @ApiOperation("Create one user tele attempt resource")
    @POST
    Promise<UserTeleAttempt> create(@PathParam("userId") UserId userId, UserTeleAttempt userTeleAttempt);

    @ApiOperation("Get one user tele attempt resource")
    @GET
    @Path("/{userTeleAttemptId}")
    Promise<UserTeleAttempt> get(@PathParam("userId") UserId userId,
                          @PathParam("userTeleAttemptId") UserTeleAttemptId userTeleAttemptId,
                          @BeanParam UserTeleAttemptGetOptions getOptions);

    @ApiOperation("Search user tele attempt resource")
    @GET
    Promise<Results<UserTeleAttempt>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserTeleAttemptListOptions listOptions);
}
