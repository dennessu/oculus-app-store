/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.enumid.DeviceTypeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.DeviceType;
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions;
import com.junbo.identity.spec.v1.option.model.DeviceTypeGetOptions;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@Api(value = "device-types")
@RestResource
@InProcessCallable
@Path("/device-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface DeviceTypeResource {

    @ApiOperation("Create one DeviceType info")
    @POST
    Promise<DeviceType> create(DeviceType deviceType);

    @ApiOperation("Update one deviceType info")
    @PUT
    @Path("/{deviceTypeId}")
    Promise<DeviceType> put(@PathParam("deviceTypeId") DeviceTypeId deviceTypeId, DeviceType deviceType);

    @POST
    @Path("/{deviceTypeId}")
    Promise<DeviceType> patch(@PathParam("deviceTypeId") DeviceTypeId deviceTypeId, DeviceType deviceType);

    @ApiOperation("Get one deviceType info")
    @GET
    @Path("/{deviceTypeId}")
    @AuthorizationNotRequired
    Promise<DeviceType> get(@PathParam("deviceTypeId") DeviceTypeId deviceTypeId, @BeanParam DeviceTypeGetOptions getOptions);

    @ApiOperation("Search deviceType info")
    @GET
    @AuthorizationNotRequired
    Promise<Results<DeviceType>> list(@BeanParam DeviceTypeListOptions listOptions);

    @ApiOperation("Delete a device type")
    @DELETE
    @Path("/{deviceTypeId}")
    Promise<Response> delete(@PathParam("deviceTypeId") DeviceTypeId deviceTypeId);
}
