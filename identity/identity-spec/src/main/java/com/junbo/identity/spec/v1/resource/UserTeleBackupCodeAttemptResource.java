/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleBackupCodeAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt;
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeAttemptGetOptions;
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
@Path("/users/{userId}/tele-backup-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTeleBackupCodeAttemptResource {
    @ApiOperation("Create one user tele backup attempt resource")
    @POST
    Promise<UserTeleBackupCodeAttempt> create(@PathParam("userId") UserId userId,
                                              UserTeleBackupCodeAttempt userTeleBackupCodeAttempt);

    @ApiOperation("Get one user tele backup attempt resource")
    @GET
    @Path("/{userTeleBackupCodeAttemptId}")
    Promise<UserTeleBackupCodeAttempt> get(@PathParam("userId") UserId userId,
             @PathParam("userTeleBackupCodeAttemptId") UserTeleBackupCodeAttemptId userTeleBackupCodeAttemptId,
             @BeanParam UserTeleBackupCodeAttemptGetOptions getOptions);

    @ApiOperation("Search user tele backup attempt resource")
    @GET
    Promise<Results<UserTeleBackupCodeAttempt>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserTeleBackupCodeAttemptListOptions listOptions);
}
