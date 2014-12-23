/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.DeviceId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Device;
import com.junbo.identity.spec.v1.option.list.DeviceListOptions;
import com.junbo.identity.spec.v1.option.model.DeviceGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "devices")
@RestResource
@InProcessCallable
@Path("/devices")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface DeviceResource {

    @ApiOperation("Create a device info")
    @POST
    Promise<Device> create(Device device);

    @ApiOperation("Update a device info")
    @PUT
    @Path("/{deviceId}")
    Promise<Device> put(@PathParam("deviceId") DeviceId deviceId, Device device);

    @ApiOperation("Get a device info")
    @GET
    @Path("/{deviceId}")
    Promise<Device> get(@PathParam("deviceId") DeviceId deviceId, @BeanParam DeviceGetOptions getOptions);

    @ApiOperation("Search device info")
    @GET
    Promise<Results<Device>> list(@BeanParam DeviceListOptions listOptions);

    @ApiOperation("Delete a device")
    @DELETE
    @Path("/{deviceId}")
    Promise<Response> delete(@PathParam("deviceId") DeviceId deviceId);
}
