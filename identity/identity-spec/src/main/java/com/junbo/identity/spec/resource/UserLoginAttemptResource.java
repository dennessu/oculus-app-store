/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.options.entity.LoginAttemptGetOptions;
import com.junbo.identity.spec.options.list.LoginAttemptListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/login-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserLoginAttemptResource {

    @POST
    @Path("/")
    Promise<User> create(@PathParam("userId") UserId userId,
                         UserLoginAttempt loginAttempt);

    @GET
    @Path("/{userLoginAttemptId}")
    Promise<UserLoginAttempt> get(@PathParam("userId") UserId userId,
                              @PathParam("userLoginAttemptId") UserLoginAttemptId userLoginAttemptId,
                              @BeanParam LoginAttemptGetOptions getOptions);

    @GET
    @Path("/")
    Promise<Results<UserLoginAttempt>> list(@PathParam("userId") UserId userId,
                                           @BeanParam LoginAttemptListOptions listOptions);
}
