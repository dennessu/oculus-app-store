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
import com.junbo.common.id.OfferAttributeId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Attribute resource implementation.
 */
public class OfferAttributeResourceImpl implements OfferAttributeResource {
    @Autowired
    private OfferAttributeService attributeService;

    @Override
    public Promise<OfferAttribute> getAttribute(OfferAttributeId attributeId) {
        return Promise.pure(attributeService.getAttribute(attributeId.getValue()));
    }

    @Override
    public Promise<Results<OfferAttribute>> getAttributes(@BeanParam OfferAttributesGetOptions options) {
        List<OfferAttribute> attributes = attributeService.getAttributes(options);
        Results<OfferAttribute> resultList = new Results<>();
        resultList.setItems(attributes);
        return Promise.pure(resultList);
    }

    @Override
    public Promise<OfferAttribute> createAttribute(OfferAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<OfferAttribute> update(OfferAttributeId attributeId, OfferAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId.getValue(), attribute));
    }

    @Override
    public Promise<Response> delete(OfferAttributeId attributeId) {
        attributeService.deleteAttribute(attributeId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
