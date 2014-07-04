/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.resource;

import com.junbo.common.id.CsrUpdateId;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrUpdate;
import com.junbo.csr.spec.option.list.CsrUpdateListOptions;
import com.junbo.csr.spec.option.model.CsrUpdateGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java code for CsrUpdateResource.
 */
@Path("/csr-updates")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CsrUpdateResource {
    @POST
    Promise<CsrUpdate> create(CsrUpdate csrUpdate);

    @PUT
    @Path("/{csrUpdateId}")
    Promise<CsrUpdate> put(@PathParam("csrUpdateId") CsrUpdateId csrUpdateId, CsrUpdate csrUpdate);

    @POST
    @Path("/{csrUpdateId}")
    Promise<CsrUpdate> patch(@PathParam("csrUpdateId") CsrUpdateId csrUpdateId, CsrUpdate csrUpdate);

    @GET
    @Path("/{csrUpdateId}")
    Promise<CsrUpdate> get(@PathParam("csrUpdateId") CsrUpdateId csrUpdateId, @BeanParam CsrUpdateGetOptions getOptions);

    @GET
    Promise<Results<CsrUpdate>> list(@BeanParam CsrUpdateListOptions listOptions);

    @DELETE
    @Path("/{csrUpdateId}")
    Promise<Void> delete(@PathParam("csrUpdateId") CsrUpdateId csrUpdateId);
}
