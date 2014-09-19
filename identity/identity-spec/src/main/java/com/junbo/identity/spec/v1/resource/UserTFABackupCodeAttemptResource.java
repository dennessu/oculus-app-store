/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTFABackupCodeAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt;
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserTFABackupCodeAttemptGetOptions;
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
@Path("/users/{userId}/tfa-backup-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTFABackupCodeAttemptResource {
    @ApiOperation("Create one user TFA backup attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<UserTFABackupCodeAttempt> create(@PathParam("userId") UserId userId,
                                             UserTFABackupCodeAttempt userTFABackupCodeAttempt);

    @ApiOperation("Get one user TFA backup attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{userTFABackupCodeAttemptId}")
    Promise<UserTFABackupCodeAttempt> get(@PathParam("userId") UserId userId,
             @PathParam("userTFABackupCodeAttemptId") UserTFABackupCodeAttemptId userTFABackupCodeAttemptId,
             @BeanParam UserTFABackupCodeAttemptGetOptions getOptions);

    @ApiOperation("Search user TFA backup attempt resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<UserTFABackupCodeAttempt>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserTFABackupCodeAttemptListOptions listOptions);
}
