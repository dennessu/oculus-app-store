/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.enumid.LocaleId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.option.list.LocaleListOptions;
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Locale;

/**
 * Created by liangfu on 4/24/14.
 */
@Api(value = "locales")
@RestResource
@Path("/locales")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface LocaleResource {
    @ApiOperation("Create a Locale info")
    @POST
    Promise<Locale> create(Locale locale);

    @ApiOperation("Update a Locale info")
    @PUT
    @Path("/{localeId}")
    Promise<Locale> put(@PathParam("localeId") LocaleId localeId, Locale locale);

    @POST
    @Path("/{localeId}")
    Promise<Locale> patch(@PathParam("localeId") LocaleId localeId, Locale locale);

    @ApiOperation("Get a Locale info")
    @GET
    @Path("/{LocaleId}")
    Promise<Locale> get(@PathParam("LocaleId") LocaleId localeId, @BeanParam LocaleGetOptions getOptions);

    @ApiOperation("Search Locale info")
    @GET
    Promise<Results<Locale>> list(@BeanParam LocaleListOptions listOptions);
}
