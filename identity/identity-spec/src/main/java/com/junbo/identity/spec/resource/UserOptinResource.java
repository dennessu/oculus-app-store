/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserOptin;
import com.junbo.identity.spec.options.entity.UserOptinGetOptions;
import com.junbo.identity.spec.options.list.UserOptinListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserOptinResource.
 */
@Api("users")
@Path("/users/{userId}/opt-ins")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserOptinResource {
    @ApiOperation("Create one user optin")
    @POST
    @Path("/")
    Promise<UserOptin> create(@PathParam("userId") UserId userId,
                              UserOptin userOptin);

    @ApiOperation("Update one existing user optin")
    @PUT
    @Path("/{userOptinId}")
    Promise<UserOptin> put(@PathParam("userId") UserId userId,
                         @PathParam("userOptinId") UserOptinId userOptinId,
                         UserOptin userOptin);

    @ApiOperation("Patch update one existing user optin")
    @POST
    @Path("/{userOptinId}")
    Promise<UserOptin> patch(@PathParam("userId") UserId userId,
                           @PathParam("userOptinId") UserOptinId userOptinId,
                           UserOptin userOptin);

    @ApiOperation("Delete one existing user optin")
    @DELETE
    @Path("/{userOptinId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userOptinId") UserOptinId userOptinId);

    @ApiOperation("get one existing user optin")
    @GET
    @Path("/{userOptinId}")
    Promise<UserOptin> get(@PathParam("userId") UserId userId,
                         @PathParam("userOptinId") UserOptinId userOptinId,
                         @BeanParam UserOptinGetOptions getOptions);

    @ApiOperation("Search one user's user optins")
    @GET
    @Path("/")
    Promise<Results<UserOptin>> list(@PathParam("userId") UserId userId,
                                      @BeanParam UserOptinListOptions listOptions);
}

