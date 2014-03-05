/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Subledger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * Created by chriszhu on 2/10/14.
 */
@Path("subledgers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface SubledgerResource {

    @GET
    Promise<List<Subledger>> getSubledgers(
            @QueryParam("sellerId") String sellerId,
            @QueryParam("status") String status,
            @QueryParam("fromDate") Date fromDate,
            @QueryParam("toDate") Date toDate,
            @Context HttpHeaders headers);

}
