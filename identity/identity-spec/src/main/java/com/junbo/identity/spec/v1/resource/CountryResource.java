/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.option.list.CountryListOptions;
import com.junbo.identity.spec.v1.option.model.CountryGetOptions;
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
@Api(value = "countries")
@RestResource
@InProcessCallable
@Path("/countries")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CountryResource {

    @ApiOperation("Create a Country info")
    @POST
    Promise<Country> create(Country country);

    @ApiOperation("Update a country info")
    @PUT
    @Path("/{countryId}")
    Promise<Country> put(@PathParam("countryId") CountryId countryId, Country country);

    @ApiOperation("Get a country info")
    @GET
    @Path("/{countryId}")
    @AuthorizationNotRequired
    Promise<Country> get(@PathParam("countryId") CountryId countryId, @BeanParam CountryGetOptions getOptions);

    @ApiOperation("Get all countries")
    @GET
    @AuthorizationNotRequired
    Promise<Results<Country>> list(@BeanParam CountryListOptions listOptions);

    @ApiOperation("Delete a country")
    @DELETE
    @Path("/{countryId}")
    Promise<Response> delete(@PathParam("countryId") CountryId countryId);
}
