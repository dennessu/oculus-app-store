/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.options.entity.UserLoginAttemptGetOptions;
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions;
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
@Path("/users/login-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserLoginAttemptResource {
    @ApiOperation("Login")
    @POST
    @Path("/")
    Promise<UserLoginAttempt> create(UserLoginAttempt loginAttempt);

    @ApiOperation("Get Login attempt information")
    @GET
    @Path("/{userLoginAttemptId}")
    Promise<UserLoginAttempt> get(@PathParam("userLoginAttemptId") UserLoginAttemptId userLoginAttemptId,
                                  @BeanParam UserLoginAttemptGetOptions getOptions);

    @ApiOperation("Search login attempt information")
    @GET
    @Path("/")
    Promise<Results<UserLoginAttempt>> list(@BeanParam UserLoginAttemptListOptions listOptions);
}
