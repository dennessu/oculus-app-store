/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
import com.junbo.identity.spec.model.users.LoginAttempt;
import com.junbo.identity.spec.model.users.User;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/loginAttempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserLoginAttemptResource {

    @POST
    @Path("/")
    Promise<User> create(LoginAttempt loginAttempt);

    @GET
    @Path("/{userLoginAttemptId}")
    Promise<LoginAttempt> get(@PathParam("userId")UserId userId,
                              @PathParam("userLoginAttemptId")UserLoginAttemptId userLoginAttemptId);

    @GET
    @Path("/")
    Promise<ResultList<LoginAttempt>> get(@PathParam("userId")UserId userId,
                                          @BeanParam UserLoginAttemptGetOption getOption);
}
