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
import com.junbo.common.id.ItemAttributeId;
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
    public Promise<ItemAttribute> getAttribute(ItemAttributeId attributeId) {
        return Promise.pure(attributeService.getAttribute(attributeId.getValue()));
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
        builder.queryParam("size", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextBookmark())) {
            builder.queryParam("bookmark", options.getNextBookmark());
        } else {
            builder.queryParam("start", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<ItemAttribute> createAttribute(ItemAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<ItemAttribute> update(ItemAttributeId attributeId, ItemAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId.getValue(), attribute));
    }

    @Override
    public Promise<Response> delete(ItemAttributeId attributeId) {
        attributeService.deleteAttribute(attributeId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
