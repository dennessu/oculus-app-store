/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.crypto.spec.model.CryptoMessage;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 5/6/14.
 */
@RestResource
@InProcessCallable
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CryptoResource {

    // Encrypt the object based on key,
    // It will use the latest version to encrypt
    @POST
    @Path("/crypto/{userId}/encrypt")
    Promise<CryptoMessage> encrypt(@PathParam("userId") UserId userId, CryptoMessage rawMessage);

    @POST
    @Path("/crypto/encrypt")
    Promise<CryptoMessage> encrypt(CryptoMessage rawMessage);

    // Decrypt the object based on key
    // It will use the latest version to decrypt, if unsuccessful, it will fall back to the older version
    @POST
    @Path("/crypto/{userId}/decrypt")
    Promise<CryptoMessage> decrypt(@PathParam("userId") UserId userId, CryptoMessage encryptMessage);

    @POST
    @Path("/crypto/decrypt")
    Promise<CryptoMessage> decrypt(CryptoMessage encryptMessage);

}

