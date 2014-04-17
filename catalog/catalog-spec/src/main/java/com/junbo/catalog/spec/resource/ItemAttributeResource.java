/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.item.ItemAttribute;
import com.junbo.catalog.spec.model.item.ItemAttributesGetOptions;
import com.junbo.common.id.ItemAttributeId;
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
@Api("item-attributes")
@Path("item-attributes")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ItemAttributeResource {

    @ApiOperation("Get an item attribute")
    @GET
    @Path("/{attributeId}")
    Promise<ItemAttribute> getItemAttribute(@PathParam("attributeId") ItemAttributeId attributeId);

    @ApiOperation("Get all item attributes")
    @GET
    @Path("/")
    Promise<Results<ItemAttribute>> getItemAttributes(@BeanParam ItemAttributesGetOptions options);

    @ApiOperation("Create an item attribute")
    @POST
    @Path("/")
    Promise<ItemAttribute> createItemAttribute(ItemAttribute attribute);

    @ApiOperation("Delete an item attribute")
    @DELETE
    @Path("/{attributeId}")
    Promise<Response> delete(@PathParam("attributeId") ItemAttributeId attributeId);
}
