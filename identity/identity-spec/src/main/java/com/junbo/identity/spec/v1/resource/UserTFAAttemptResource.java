/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTFAAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTFAAttempt;
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserTFAAttemptGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/22/14.
 */
@Api(value = "users")
@RestResource
@InProcessCallable
@Path("/users/{userId}/tfa-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTFAAttemptResource {

    @ApiOperation("Create one user tfa attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<UserTFAAttempt> create(@PathParam("userId") UserId userId, UserTFAAttempt userTeleAttempt);

    @ApiOperation("Get one user tfa attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{userTFAAttemptId}")
    Promise<UserTFAAttempt> get(@PathParam("userId") UserId userId,
                          @PathParam("userTFAAttemptId") UserTFAAttemptId userTFAAttemptId,
                          @BeanParam UserTFAAttemptGetOptions getOptions);

    @ApiOperation("Search user tfa attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<UserTFAAttempt>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserTFAAttemptListOptions listOptions);
}
