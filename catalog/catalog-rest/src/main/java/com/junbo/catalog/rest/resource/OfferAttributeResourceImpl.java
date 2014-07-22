/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferAttributeService;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import com.junbo.catalog.spec.resource.OfferAttributeResource;
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
public class OfferAttributeResourceImpl implements OfferAttributeResource {
    @Autowired
    private OfferAttributeService attributeService;

    @Override
    public Promise<OfferAttribute> getAttribute(String attributeId) {
        return Promise.pure(attributeService.getAttribute(attributeId));
    }

    @Override
    public Promise<Results<OfferAttribute>> getAttributes(@BeanParam OfferAttributesGetOptions options) {
        List<OfferAttribute> attributes = attributeService.getAttributes(options);
        Results<OfferAttribute> results = new Results<>();
        results.setItems(attributes);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private String buildNextUrl(OfferAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offer-attributes");
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
    public Promise<OfferAttribute> createAttribute(OfferAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<OfferAttribute> update(String attributeId, OfferAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId, attribute));
    }

    @Override
    public Promise<Response> delete(String attributeId) {
        attributeService.deleteAttribute(attributeId);
        return Promise.pure(Response.status(204).build());
    }
}
