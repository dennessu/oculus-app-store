/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt;
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserSecurityQuestionAttemptGetOptions;
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
@Path("/users/{userId}/security-question-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionVerifyAttemptResource {

    @ApiOperation("Create one security question attempt")
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<UserSecurityQuestionVerifyAttempt> create(@PathParam("userId") UserId userId,
                                                UserSecurityQuestionVerifyAttempt userSecurityQuestionAttempt);

    @ApiOperation("Get one security question attempt")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{UserSecurityQuestionVerifyAttemptId}")
    Promise<UserSecurityQuestionVerifyAttempt> get(@PathParam("userId") UserId userId,
                @PathParam("UserSecurityQuestionVerifyAttemptId")UserSecurityQuestionVerifyAttemptId id,
                @BeanParam UserSecurityQuestionAttemptGetOptions getOptions);

    @ApiOperation("Search security question attempt history")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<UserSecurityQuestionVerifyAttempt>> list(@PathParam("userId") UserId userId,
                                                   @BeanParam UserSecurityQuestionAttemptListOptions listOptions);
}
