/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.resource;

import com.junbo.common.id.SubledgerId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.spec.model.SubledgerParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by chriszhu on 2/10/14.
 */
@Path("subledgers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface SubledgerResource {

    @PUT
    @Path("/{subledgerId}")
    @RouteBy("subledgerId")
    Promise<Subledger> putSubledger(@PathParam("subledgerId")SubledgerId subledgerId, Subledger subledger);

    @GET
    @Path("/{subledgerId}")
    @RouteBy("subledgerId")
    Promise<Subledger> getSubledger(@PathParam("subledgerId")SubledgerId subledgerId);

    @GET
    @RouteBy(value = "subledgerParam.getSellerId()")
    Promise<Results<Subledger>> getSubledgers(@BeanParam SubledgerParam subledgerParam, @BeanParam PageParam pageParam);

}
