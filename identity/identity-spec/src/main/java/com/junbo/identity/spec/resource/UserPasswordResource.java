/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.entity.UserPasswordGetOptions;
import com.junbo.identity.spec.options.list.UserPasswordListOption;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/passwords")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPasswordResource {

    @POST
    @Path("/")
    Promise<UserPassword> create(@PathParam("userId") UserId userId,
                                 UserPassword userPassword);

    @GET
    @Path("/{userPasswordId}")
    Promise<UserPassword> get(@PathParam("userId") UserId userId,
                              @PathParam("userPasswordId") UserPasswordId userPasswordId,
                              @BeanParam UserPasswordGetOptions getOptions);

    @GET
    @Path("/")
    Promise<Results<UserPassword>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserPasswordListOption listOptions);

}
