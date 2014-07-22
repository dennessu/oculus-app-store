/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.resource;

import com.junbo.store.spec.model.login.*;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * The wrapper api for store front.
 */
@Path("/storeapi/id")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface LoginResource {

    @POST
    @Path("/namecheck")
    Promise<UserNameCheckResponse> checkUserName(UserNameCheckRequest userNameCheckRequest);

    @POST
    @Path("/ratecredential")
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest userCredentialRateRequest);

    @POST
    @Path("/create")
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest, @Context ContainerRequestContext request);

    @POST
    @Path("/signIn")
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest);

    @POST
    @Path("/checkcredential")
    Promise<UserCredentialCheckResponse> checkUserCredential(UserCredentialCheckRequest userCredentialCheckRequest);

    @POST
    @Path("/changecredential")
    Promise<UserCredentialChangeResponse> changeUserCredential(UserCredentialChangeRequest userCredentialChangeRequest);

    @POST
    @Path("/token")
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest);
}
