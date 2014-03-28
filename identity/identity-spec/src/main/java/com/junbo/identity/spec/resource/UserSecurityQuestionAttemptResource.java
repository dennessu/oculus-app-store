/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.entity.UserSecurityQuestionAttemptGetOption;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/25/14.
 */
@Api("userSecurityQuestionAttempt")
@RestResource
@Path("/users/{userId}/security-question-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionAttemptResource {
    @ApiOperation("Attempt answer user security question")
    @POST
    @Path("/")
    Promise<UserSecurityQuestionAttempt> create(
            @PathParam("userId") UserId userId,
            UserSecurityQuestionAttempt userSecurityQuestionAttempt);

    @ApiOperation("Get user security question attempt info")
    @GET
    @Path("/{userSecurityQuestionAttemptId}")
    Promise<UserSecurityQuestionAttempt> get(
            @PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionAttemptId")UserSecurityQuestionAttemptId id,
            @BeanParam UserSecurityQuestionAttemptGetOption getOptions);

    @ApiOperation("Search one user's security question attempt info")
    @GET
    @Path("/")
    Promise<Results<UserSecurityQuestionAttempt>> list(
            @PathParam("userId") UserId userId,
            @BeanParam UserSecurityQuestionAttemptListOptions listOptions);
}
