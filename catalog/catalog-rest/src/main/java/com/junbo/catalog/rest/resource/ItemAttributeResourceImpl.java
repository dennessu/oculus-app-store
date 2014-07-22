/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemAttributeService;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.catalog.spec.resource.ItemAttributeResource;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * Attribute resource implementation.
 */
public class ItemAttributeResourceImpl implements ItemAttributeResource {
    @Autowired
    private ItemAttributeService attributeService;

    @Override
    public Promise<ItemAttribute> getAttribute(String attributeId) {
        return Promise.pure(attributeService.getAttribute(attributeId));
    }

    @Override
    public Promise<Results<ItemAttribute>> getAttributes(@BeanParam ItemAttributesGetOptions options) {
        List<ItemAttribute> attributes = attributeService.getAttributes(options);
        Results<ItemAttribute> results = new Results<>();
        results.setItems(attributes);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private String buildNextUrl(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-attributes");
        if (options.getAttributeType() != null) {
            builder.queryParam("type", options.getAttributeType());
        }
        builder.queryParam("count", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<ItemAttribute> createAttribute(ItemAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<ItemAttribute> update(String attributeId, ItemAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId, attribute));
    }

    @Override
    public Promise<Response> delete(String attributeId) {
        attributeService.deleteAttribute(attributeId);
        return Promise.pure(Response.status(204).build());
    }
}
