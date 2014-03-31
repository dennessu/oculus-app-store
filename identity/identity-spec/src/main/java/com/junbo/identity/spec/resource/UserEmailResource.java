/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.options.entity.UserEmailGetOptions;
import com.junbo.identity.spec.options.list.UserEmailListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("userEmail")
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserEmailResource {
    @ApiOperation("Create an user email")
    @POST
    @Path("/{userId}/emails/")
    Promise<UserEmail> create(@PathParam("userId") UserId userId,
                              UserEmail userEmail);

    @ApiOperation("Updating an existing user email")
    @PUT
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> put(@PathParam("userId") UserId userId,
                           @PathParam("userEmailId") UserEmailId userEmailId,
                           UserEmail userEmail);

    @ApiOperation("Patch updating an existing user email")
    @POST
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> patch(@PathParam("userId") UserId userId,
                             @PathParam("userEmailId") UserEmailId userEmailId,
                             UserEmail userEmail);

    @ApiOperation("Delete an existing user email")
    @DELETE
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> delete(@PathParam("userId") UserId userId,
                              @PathParam("userEmailId") UserEmailId userEmailId);

    @ApiOperation("Get an existing user email")
    @GET
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> get(@PathParam("userId") UserId userId,
                           @PathParam("userEmailId") UserEmailId userEmailId,
                           @BeanParam UserEmailGetOptions getOptions);

    @ApiOperation("Search one user's emails")
    @GET
    @Path("/{userId}/emails/")
    Promise<Results<UserEmail>> list(@PathParam("userId") UserId userId,
                                        @BeanParam UserEmailListOptions listOptions);

    @ApiOperation("Search user based on email")
    @GET
    @Path("/emails/")
    Promise<Results<UserEmail>> list(@BeanParam UserEmailListOptions listOptions);
}
