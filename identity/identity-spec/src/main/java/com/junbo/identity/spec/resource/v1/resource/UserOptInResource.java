/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptInId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserOptInGetOption;
import com.junbo.identity.spec.v1.model.users.UserOptin;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/optIns")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserOptInResource {
    @POST
    @Path("/")
    Promise<UserOptin> create(@PathParam("userId")UserId userId,
                              UserOptin userOptIn);

    @PUT
    @Path("/{userOptInId}")
    Promise<UserOptin> update(@PathParam("userId")UserId userId,
                              @PathParam("userOptInId")UserOptInId userOptInId,
                              UserOptin userOptIn);
    @POST
    @Path("/{userOptInId}")
    Promise<UserOptin> patch(@PathParam("userId")UserId userId,
                             @PathParam("userOptInId")UserOptInId userOptInId,
                             UserOptin userOptIn);

    @GET
    @Path("/{userOptInId}")
    Promise<UserOptin> get(@PathParam("userId")UserId userId,
                           @PathParam("userOptInId")UserOptInId userOptInId);
    @DELETE
    @Path("/{userOptInId}")
    Promise<UserOptin> delete(@PathParam("userId")UserId userId,
                              @PathParam("userOptInId")UserOptInId userOptInId);

    @GET
    @Path("/")
    Promise<ResultList<UserOptin>> list(@PathParam("userId")UserId userId,
                                        @BeanParam UserOptInGetOption getOption);
}
