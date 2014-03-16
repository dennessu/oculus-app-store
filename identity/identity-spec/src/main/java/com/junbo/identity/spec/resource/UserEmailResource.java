/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserEmailGetOption;
import com.junbo.identity.spec.model.users.UserEmail;
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
public interface UserEmailResource {
    @POST
    @Path("/{userId}/emails/")
    Promise<UserEmail> create(@PathParam("userId")UserId userId,
                              UserEmail userEmail);

    @PUT
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> update(@PathParam("userId")UserId userId,
                           @PathParam("userEmailId")UserEmailId userEmailId,
                           UserEmail userEmail);

    @POST
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> patch(@PathParam("userId")UserId userId,
                             @PathParam("userEmailId")UserEmailId userEmailId,
                             UserEmail userEmail);

    @GET
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> get(@PathParam("userId")UserId userId,
                           @PathParam("userEmailId")UserEmailId userEmailId);

    @GET
    @Path("/{userId}/emails/")
    Promise<ResultList<UserEmail>> list(@PathParam("userId")UserId userId,
                                        @BeanParam UserEmailGetOption getOption);
    @DELETE
    @Path("/{userId}/emails/{userEmailId}")
    Promise<UserEmail> delete(@PathParam("userId")UserId userId,
                              @PathParam("userEmailId")UserEmailId userEmailId);

    @GET
    @Path("/emails/")
    Promise<ResultList<UserEmail>> search(@BeanParam UserEmailGetOption getOption);
}
