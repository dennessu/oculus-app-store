/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTFAId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTFA;
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions;
import com.junbo.identity.spec.v1.option.model.UserTFAGetOptions;
import com.junbo.langur.core.InProcessCallable;
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
@InProcessCallable
@Path("/users/{userId}/tfa")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTFAResource {
    @ApiOperation("Create one user tfa resource")
    @POST
    Promise<UserTFA> create(@PathParam("userId") UserId userId, UserTFA userTFA);

    @ApiOperation("Get one user tfa resource")
    @GET
    @Path("/{userTFAId}")
    Promise<UserTFA> get(@PathParam("userId") UserId userId,
                          @PathParam("userTFAId") UserTFAId userTFAId,
                          @BeanParam UserTFAGetOptions getOptions);

    @POST
    @Path("/{userTFAId}")
    Promise<UserTFA> patch(@PathParam("userId") UserId userId,
                           @PathParam("userTFAId") UserTFAId userTFAId,
                           UserTFA userTFA);

    @ApiOperation("Update one user TFA resource")
    @PUT
    @Path("/{userTFAId}")
    Promise<UserTFA> put(@PathParam("userId") UserId userId,
                          @PathParam("userTFAId") UserTFAId userTFAId,
                          UserTFA userTFA);

    @ApiOperation("Delete one user TFA resource")
    @DELETE
    @Path("/{userTFAId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userTFAId") UserTFAId userTFAId);

    @ApiOperation("Search user TFA resource")
    @GET
    Promise<Results<UserTFA>> list(@PathParam("userId") UserId userId,
                                    @BeanParam UserTFAListOptions listOptions);
}
