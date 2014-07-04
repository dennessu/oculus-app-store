/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.resource;

import com.junbo.common.id.CsrLogId;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrLog;
import com.junbo.csr.spec.option.list.CsrLogListOptions;
import com.junbo.csr.spec.option.model.CsrLogGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by haomin on 14-7-4.
 */
@RestResource
@InProcessCallable
@Path("/csr-logs")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CsrLogResource {
    @POST
    Promise<CsrLog> create(CsrLog csrLog);

    @PUT
    @Path("/{csrLogId}")
    Promise<CsrLog> put(@PathParam("csrLogId") CsrLogId csrLogId, CsrLog csrLog);

    @POST
    @Path("/{csrLogId}")
    Promise<CsrLog> patch(@PathParam("csrLogId") CsrLogId csrLogId, CsrLog csrLog);

    @GET
    @Path("/{csrLogId}")
    Promise<CsrLog> get(@PathParam("csrLogId") CsrLogId csrLogId, @BeanParam CsrLogGetOptions getOptions);

    @GET
    Promise<Results<CsrLog>> list(@BeanParam CsrLogListOptions listOptions);

    @DELETE
    @Path("/{csrLogId}")
    Promise<Void> delete(@PathParam("csrLogId") CsrLogId csrLogId);
}
