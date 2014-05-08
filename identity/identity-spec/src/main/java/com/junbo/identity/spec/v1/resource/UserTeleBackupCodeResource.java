/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleBackupCodeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTeleBackupCode;
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions;
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeGetOptions;
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
@Path("/users/{userId}/tele-backup")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTeleBackupCodeResource {
    @ApiOperation("Create one user tele backup resource")
    @POST
    Promise<UserTeleBackupCode> create(@PathParam("userId") UserId userId, UserTeleBackupCode userTeleBackupCode);

    @ApiOperation("Get one user tele backup code resource")
    @GET
    @Path("/{userTeleBackupCodeId}")
    Promise<UserTeleBackupCode> get(@PathParam("userId") UserId userId,
                              @PathParam("userTeleBackupCodeId") UserTeleBackupCodeId userTeleBackupCodeId,
                              @BeanParam UserTeleBackupCodeGetOptions getOptions);

    @POST
    @Path("/{userTeleBackupCodeId}")
    Promise<UserTeleBackupCode> patch(@PathParam("userId") UserId userId,
                                @PathParam("userTeleBackupCodeId") UserTeleBackupCodeId userTeleBackupCodeId,
                                UserTeleBackupCode userTeleBackupCode);

    @ApiOperation("Update one user tele backupCode resource")
    @PUT
    @Path("/{userTeleBackupCodeId}")
    Promise<UserTeleBackupCode> put(@PathParam("userId") UserId userId,
                              @PathParam("userTeleBackupCodeId") UserTeleBackupCodeId userTeleBackupCodeId,
                              UserTeleBackupCode userTeleBackupCode);

    @ApiOperation("Delete one user tele backup resource")
    @DELETE
    @Path("/{userTeleId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userTeleId") UserTeleBackupCodeId userTeleBackupCodeId);

    @ApiOperation("Search user tele backup resource")
    @GET
    Promise<Results<UserTeleBackupCode>> list(@PathParam("userId") UserId userId,
                                        @BeanParam UserTeleBackupCodeListOptions listOptions);
}
