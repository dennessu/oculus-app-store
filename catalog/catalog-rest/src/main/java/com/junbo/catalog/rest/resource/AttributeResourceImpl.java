/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.AttributeService;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.attribute.AttributesGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.resource.AttributeResource;
import com.junbo.common.id.AttributeId;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;
import java.util.List;

/**
 * Attribute resource implementation.
 */
public class AttributeResourceImpl implements AttributeResource {
    @Autowired
    private AttributeService attributeService;

    @Override
    public Promise<Attribute> getAttribute(AttributeId attributeId) {
        return Promise.pure(attributeService.getAttribute(attributeId.getValue()));
    }

    @Override
    public Promise<ResultList<Attribute>> getAttributes(@BeanParam AttributesGetOptions options) {
        List<Attribute> attributes = attributeService.getAttributes(options);
        ResultList<Attribute> resultList = new ResultList<>();
        resultList.setResults(attributes);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Attribute> createAttribute(Attribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }
}
