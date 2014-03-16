/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserAuthenticatorResource {
    @POST
    @Path("/{userId}/authenticators")
    Promise<UserAuthenticator> create(@PathParam("userId")UserId userId,
                                      UserAuthenticator userAuthenticator);

    @PUT
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> update(@PathParam("userId")UserId userId,
                                      @PathParam("userAuthenticatorId")UserAuthenticatorId userAuthenticatorId,
                                      UserAuthenticator userAuthenticator);

    @POST
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> patch(@PathParam("userId")UserId userId,
                                     @PathParam("userAuthenticatorId")UserAuthenticatorId userAuthenticatorId,
                                     UserAuthenticator userAuthenticator);

    @GET
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<UserAuthenticator> get(@PathParam("userId")UserId userId,
                                   @PathParam("userAuthenticatorId")UserAuthenticatorId userAuthenticatorId);

    @GET
    @Path("/{userId}/authenticators")
    Promise<ResultList<UserAuthenticator>> list(@PathParam("userId")UserId userId,
                                                @BeanParam UserAuthenticatorGetOption getOption);

    @DELETE
    @Path("/{userId}/authenticators/{userAuthenticatorId}")
    Promise<Void> delete(@PathParam("userId")UserId userId,
                         @PathParam("userAuthenticatorId")UserAuthenticatorId userAuthenticatorId);

    @GET
    @Path("/authenticators")
    Promise<ResultList<UserAuthenticator>> search(@BeanParam UserAuthenticatorGetOption getOption);
}
