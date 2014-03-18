/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.UserPhoneNumberGetOptions;
import com.junbo.identity.spec.options.UserPhoneNumberListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/phone-numbers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPhoneNumberResource {

    @POST
    @Path("/")
    Promise<UserPhoneNumber> create(@PathParam("userId") UserId userId,
                                    UserPhoneNumber userPhoneNumber);

    @PUT
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> put(@PathParam("userId") UserId userId,
                                 @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                 UserPhoneNumber userPhoneNumber);

    @POST
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> patch(@PathParam("userId") UserId userId,
                                   @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                   UserPhoneNumber userPhoneNumber);

    @DELETE
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> delete(@PathParam("userId") UserId userId,
                                    @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId);

    @GET
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> get(@PathParam("userId") UserId userId,
                                 @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                 @BeanParam UserPhoneNumberGetOptions getOptions);

    @GET
    @Path("/")
    Promise<ResultList<UserPhoneNumber>> list(@PathParam("userId") UserId userId,
                                              @BeanParam UserPhoneNumberListOptions listOptions);
}
