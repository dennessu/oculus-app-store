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
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @RouteBy(value = "userId", switchable = true)
    Promise<User> put(@PathParam("userId") UserId userId, User user);

    @PUT
    @Path("/slient-put/{userId}")
    @RouteBy(value = "userId", switchable = true)
    Promise<User> silentPut(@PathParam("userId") UserId userId, User user);

    @ApiOperation("Get one user")
    @GET
    @Path("/{userId}")
    @RouteBy(value = "userId", switchable = true)
    Promise<User> get(@PathParam("userId") UserId userId, @BeanParam UserGetOptions getOptions);

    @ApiOperation("Search users")
    @GET
    @RouteByAccessToken(switchable = true)
    Promise<Results<User>> list(@BeanParam UserListOptions listOptions);

    @ApiOperation("Delete one user")
    @DELETE
    @Path("/{userId}")
    @RouteBy(value = "userId", switchable = true)
    Promise<Response> delete(@PathParam("userId") UserId userId);

    @ApiOperation("Check whether username is valid")
    @POST
    @Path("/check-username/{username}")
    Promise<Void> checkUsername(@PathParam("username") String username);

    @ApiOperation("Check whether email is valid")
    @POST
    @Path("/check-email/{email}")
    Promise<Void> checkEmail(@PathParam("email") String email);

    @ApiOperation("Check whether email and username is occupied")
    @GET
    @Path("/check-legacy-username-email")
    Promise<Boolean> checkUsernameEmailBlocker(@QueryParam("username") String username,
                                               @QueryParam("email") String email);

    @ApiOperation("Get the email and username registration state, return will be []")
    @GET
    @Path("/get-legacy-username-email-occupy-state")
    Promise<String> getUsernameEmailOccupyState(@QueryParam("username") String username,
                                                @QueryParam("email") String email);
}
