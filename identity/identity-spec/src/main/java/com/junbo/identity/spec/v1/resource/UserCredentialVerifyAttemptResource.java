/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserCredentialVerifyAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt;
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions;
import com.junbo.identity.spec.v1.option.model.UserCredentialAttemptGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api("credential-attempts")
@RestResource
@InProcessCallable
@Path("/credential-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCredentialVerifyAttemptResource {

    @RouteBy(value = "userCredentialAttempt.getUserId()", switchable = true)
    @POST
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt userCredentialAttempt);

    @ApiOperation("Get credential verify attempt.")
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{userCredentialVerifyAttemptId}")
    Promise<UserCredentialVerifyAttempt> get(
            @PathParam("userCredentialVerifyAttemptId")UserCredentialVerifyAttemptId id,
            @BeanParam UserCredentialAttemptGetOptions getOptions);

    @ApiOperation("Search login history")
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<UserCredentialVerifyAttempt>> list(@BeanParam UserCredentialAttemptListOptions listOptions);
}
