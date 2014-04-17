/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.offer.OfferAttribute;
import com.junbo.catalog.spec.model.offer.OfferAttributesGetOptions;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Attribute resource.
 */
@Api("offer-attributes")
@Path("offer-attributes")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OfferAttributeResource {

    @ApiOperation("Get an offer attribute")
    @GET
    @Path("/{attributeId}")
    Promise<OfferAttribute> getOfferAttribute(@PathParam("attributeId") OfferAttributeId attributeId);

    @ApiOperation("Get all offer attributes")
    @GET
    @Path("/")
    Promise<Results<OfferAttribute>> getOfferAttributes(@BeanParam OfferAttributesGetOptions options);

    @ApiOperation("Create an offer attribute")
    @POST
    @Path("/")
    Promise<OfferAttribute> createOfferAttribute(OfferAttribute attribute);

    @ApiOperation("Put an item attribute")
    @PUT
    @Path("/{attributeId}")
    Promise<OfferAttribute> update(@PathParam("attributeId") OfferAttributeId attributeId, OfferAttribute attribute);


    @ApiOperation("Delete an offer attribute")
    @DELETE
    @Path("/{attributeId}")
    Promise<Response> delete(@PathParam("attributeId") OfferAttributeId attributeId);
}
