/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.AttributeService;
import com.junbo.catalog.db.repo.AttributeRepository;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.attribute.AttributesGetOptions;
import com.junbo.common.id.AttributeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Attribute service implementation.
 */
public class AttributeServiceImpl implements AttributeService {
    @Autowired
    private AttributeRepository attributeRepo;

    @Override
    public Attribute getAttribute(Long attributeId) {
        return attributeRepo.get(attributeId);
    }

    @Override
    public List<Attribute> getAttributes(AttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<Attribute> attributes = new ArrayList<>();

            for (AttributeId attributeId : options.getAttributeIds()) {
                Attribute attribute = attributeRepo.get(attributeId.getValue());

                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else {
            options.ensurePagingValid();
            return attributeRepo.getAttributes(options.getStart(), options.getSize(), options.getAttributeType());
        }
    }

    @Override
    public Attribute create(Attribute attribute) {
        Long attributeId = attributeRepo.create(attribute);
        return attributeRepo.get(attributeId);
    }
}
