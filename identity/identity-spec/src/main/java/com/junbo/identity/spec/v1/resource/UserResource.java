/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.option.list.UserListOptions;
import com.junbo.identity.spec.v1.option.model.UserGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "users")
@RestResource
@InProcessCallable
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserResource {

    @ApiOperation("Create one user")
    @POST
    Promise<User> create(User user);

    @ApiOperation("Update one user")
    @PUT
    @Path("/{userId}")
    @RouteBy("userId")
    Promise<User> put(@PathParam("userId") UserId userId, User user);

    @PUT
    @Path("/slient-put/{userId}")
    @RouteBy("userId")
    Promise<User> silentPut(@PathParam("userId") UserId userId, User user);

    @POST
    @Path("/{userId}")
    @RouteBy("userId")
    Promise<User> patch(@PathParam("userId") UserId userId, User user);

    @ApiOperation("Get one user")
    @GET
    @Path("/{userId}")
    @RouteBy("userId")
    Promise<User> get(@PathParam("userId") UserId userId, @BeanParam UserGetOptions getOptions);

    @ApiOperation("Search users")
    @GET
    Promise<Results<User>> list(@BeanParam UserListOptions listOptions);

    @ApiOperation("Delete one user")
    @DELETE
    @Path("/{userId}")
    @RouteBy("userId")
    Promise<Void> delete(@PathParam("userId") UserId userId);

    @POST
    @Path("/check-username/{username}")
    Promise<Void> checkUsername(@PathParam("username") String username);

    @POST
    @Path("/check-email/{email}")
    Promise<Void> checkEmail(@PathParam("email") String email);
}
