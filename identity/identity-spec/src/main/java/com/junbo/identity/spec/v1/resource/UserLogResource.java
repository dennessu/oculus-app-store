/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserLogId;
import com.junbo.common.model.Results;
import com.junbo.common.userlog.UserLog;
import com.junbo.common.userlog.UserLogListOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * UserLog Resource.
 */
@RestResource
@InProcessCallable
@Path("/user-logs")
@Produces({MediaType.APPLICATION_JSON})
public interface UserLogResource {
    @GET
    @Path("/{userLogId}")
    Promise<UserLog> get(@PathParam("userLogId") UserLogId userLogId);

    @GET
    Promise<Results<UserLog>> list(@BeanParam UserLogListOptions listOptions);
}
