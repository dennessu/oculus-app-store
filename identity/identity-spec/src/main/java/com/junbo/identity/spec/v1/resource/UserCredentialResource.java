/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.RateUserCredentialRequest;
import com.junbo.identity.spec.v1.model.RateUserCredentialResponse;
import com.junbo.identity.spec.v1.model.UserCredential;
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions;
import com.junbo.langur.core.InProcessCallable;
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
@InProcessCallable
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCredentialResource {
    @POST
    @Path("/{userId}/change-credentials")
    Promise<UserCredential> create(@PathParam("userId") UserId userId,
                                   UserCredential userCredential);

    @ApiOperation("Search credential history")
    @GET
    @Path("/{userId}/credentials")
    Promise<Results<UserCredential>> list(@PathParam("userId") UserId userId,
                                          @BeanParam UserCredentialListOptions listOptions);

    @POST
    @Path("/rate-credential")
    Promise<RateUserCredentialResponse> rateCredential(RateUserCredentialRequest request);

}
