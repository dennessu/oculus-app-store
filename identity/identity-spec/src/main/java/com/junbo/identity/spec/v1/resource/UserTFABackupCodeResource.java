/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTFABackupCodeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTFABackupCode;
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions;
import com.junbo.identity.spec.v1.option.model.UserTFABackupCodeGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liangfu on 4/22/14.
 */
@Api(value = "users")
@RestResource
@InProcessCallable
@Path("/users/{userId}/tfa-backup")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTFABackupCodeResource {
    @ApiOperation("Create one user TFA backup resource")
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<UserTFABackupCode> create(@PathParam("userId") UserId userId, UserTFABackupCode userTFABackupCode);

    @ApiOperation("Get one user TFA backup code resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{userTFABackupCodeId}")
    Promise<UserTFABackupCode> get(@PathParam("userId") UserId userId,
                              @PathParam("userTFABackupCodeId") UserTFABackupCodeId userTFABackupCodeId,
                              @BeanParam UserTFABackupCodeGetOptions getOptions);

    @RouteBy(value = "userId", switchable = true)
    @POST
    @Path("/{userTFABackupCodeId}")
    Promise<UserTFABackupCode> patch(@PathParam("userId") UserId userId,
                                @PathParam("userTFABackupCodeId") UserTFABackupCodeId userTFABackupCodeId,
                                UserTFABackupCode userTFABackupCode);

    @ApiOperation("Update one user TFA backupCode resource")
    @RouteBy(value = "userId", switchable = true)
    @PUT
    @Path("/{userTFABackupCodeId}")
    Promise<UserTFABackupCode> put(@PathParam("userId") UserId userId,
                              @PathParam("userTFABackupCodeId") UserTFABackupCodeId userTFABackupCodeId,
                              UserTFABackupCode userTFABackupCode);

    @ApiOperation("Delete one user TFA backup resource")
    @RouteBy(value = "userId", switchable = true)
    @DELETE
    @Path("/{userTFAId}")
    Promise<Response> delete(@PathParam("userId") UserId userId,
                         @PathParam("userTFAId") UserTFABackupCodeId userTFABackupCodeId);

    @ApiOperation("Search user TFA backup resource")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<UserTFABackupCode>> list(@PathParam("userId") UserId userId,
                                        @BeanParam UserTFABackupCodeListOptions listOptions);
}
