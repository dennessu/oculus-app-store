/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 5/6/14.
 */
@Api(value = "crypto")
@RestResource
@Path("/crypto/{userId}")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CryptoResource {

    // Encrypt the object based on key,
    // It will use the latest version to encrypt
    @POST
    Promise<String> encrypt(@PathParam("userId") UserId userId, String str);

    // Decrypt the object based on key
    // It will use the latest version to decrypt, if unsuccessful, it will fall back to the older version
    @GET
    Promise<String> decrypt(@PathParam("userId") UserId userId, String encrypted);
}

