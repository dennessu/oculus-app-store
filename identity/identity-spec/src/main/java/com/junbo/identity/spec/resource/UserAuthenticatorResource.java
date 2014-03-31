/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import com.junbo.identity.spec.options.entity.UserAuthenticatorGetOptions;
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("users")
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserAuthenticatorResource {

    @ApiOperation("Create an user authenticator")
    @POST
    @Path("/{userId}/authenticators")
    Promise<UserAuthenticator> create(@PathParam("userId") UserId userId,
                                      UserAuthenticator userAuthenticator);

    @ApiOperation("Update an existing user authenticator")
    @PUT
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> put(@PathParam("userId") UserId userId,
                                   @PathParam("userAuthenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   UserAuthenticator userAuthenticator);

    @ApiOperation("Patch update an existing user authenticator")
    @POST
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> patch(@PathParam("userId") UserId userId,
                                     @PathParam("userAuthenticatorId") UserAuthenticatorId userAuthenticatorId,
                                     UserAuthenticator userAuthenticator);

    @ApiOperation("Delete an existing user authenticator")
    @DELETE
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userAuthenticatorId") UserAuthenticatorId userAuthenticatorId);

    @ApiOperation("Get an existing user authenticator")
    @GET
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> get(@PathParam("userId") UserId userId,
                                   @PathParam("userAuthenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   @BeanParam UserAuthenticatorGetOptions getOptions);

    @ApiOperation("Search one user's authenticators attached")
    @GET
    @Path("/{userId}/authenticators")
    Promise<Results<UserAuthenticator>> list(@PathParam("userId") UserId userId,
                                                @BeanParam UserAuthenticatorListOptions listOptions);

    @ApiOperation("Search user based on authenticator")
    @GET
    @Path("/authenticators")
    Promise<Results<UserAuthenticator>> list(@BeanParam UserAuthenticatorListOptions listOptions);
}
