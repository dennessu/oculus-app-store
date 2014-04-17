/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt;
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api("credential-verify-attempts")
@RestResource
@Path("/credential-verify-attempts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCredentialVerifyAttemptResource {

    @ApiOperation("Try to login")
    @POST
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt userCredentialAttempt);

    @ApiOperation("Search login history")
    @GET
    Promise<Results<UserCredentialVerifyAttempt>> list(@BeanParam UserCredentialAttemptListOptions listOptions);
}
