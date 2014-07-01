/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.crypto.spec.model.ItemCryptoMessage;
import com.junbo.crypto.spec.model.ItemCryptoVerify;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 6/30/14.
 */
@RestResource
@InProcessCallable
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ItemCryptoResource {
    @POST
    @Path("crypto/{itemId}/sign")
    Promise<ItemCryptoMessage> sign(@PathParam("itemId") String itemId, ItemCryptoMessage rawMessage);

    @GET
    @Path("crypto/{itemId}/public-key")
    Promise<String> getPublicKey(@PathParam("itemId")String itemId);

    @POST
    @Path("crypto/{itemId}/verify")
    Promise<Boolean> verify(@PathParam("itemId") String itemId, ItemCryptoVerify message);
}
