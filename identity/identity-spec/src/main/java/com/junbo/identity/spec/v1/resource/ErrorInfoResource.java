/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.ErrorIdentifier;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.ErrorInfo;
import com.junbo.identity.spec.v1.option.list.ErrorInfoListOptions;
import com.junbo.identity.spec.v1.option.model.ErrorInfoGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 7/22/14.
 */
@Api("error-info")
@RestResource
@InProcessCallable
@Path("/error-info")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ErrorInfoResource {
    @ApiOperation("Create one error info")
    @POST
    Promise<ErrorInfo> create(ErrorInfo errorInfo);

    @ApiOperation("Update one error info")
    @PUT
    @Path("/{errorInfoId}")
    Promise<ErrorInfo> put(@PathParam("errorInfoId") ErrorIdentifier errorIdentifier, ErrorInfo errorInfo);

    @POST
    @Path("/{errorInfoId}")
    Promise<ErrorInfo> patch(@PathParam("errorInfoId") ErrorIdentifier errorIdentifier, ErrorInfo errorInfo);

    @ApiOperation("Get one error info")
    @GET
    @Path("/{errorInfoId}")
    Promise<ErrorInfo> get(@PathParam("errorInfoId") ErrorIdentifier errorIdentifier, @BeanParam ErrorInfoGetOptions getOptions);

    @ApiOperation("Search error info")
    @GET
    Promise<Results<ErrorInfo>> list(@BeanParam ErrorInfoListOptions listOptions);

    @ApiOperation("Delete one error info")
    @DELETE
    @Path("/{errorInfoId}")
    Promise<Void> delete(@PathParam("errorInfoId") ErrorIdentifier errorIdentifier);
}
