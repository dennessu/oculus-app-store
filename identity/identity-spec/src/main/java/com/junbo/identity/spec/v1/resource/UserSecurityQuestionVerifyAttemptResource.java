/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt;
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "users")
@RestResource
@Path("/users/{userId}/security-question-verify-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionVerifyAttemptResource {

    @ApiOperation("Create one security question attempt")
    @POST
    Promise<UserSecurityQuestionVerifyAttempt> create(@PathParam("userId") UserId userId,
                                                UserSecurityQuestionVerifyAttempt userSecurityQuestionAttempt);

    @ApiOperation("Search security question attempt history")
    @GET
    Promise<Results<UserSecurityQuestionVerifyAttempt>> list(@PathParam("userId") UserId userId,
                                                   @BeanParam UserSecurityQuestionAttemptListOptions listOptions);
}
