/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.OfferAttributeService;
import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.spec.enums.OfferAttributeType;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer attribute service implementation.
 */
public class OfferAttributeServiceImpl extends AttributeServiceSupport<OfferAttribute>
        implements OfferAttributeService {
    @Autowired
    private OfferAttributeRepository attributeRepo;

    private static final List<String> ATTRIBUTE_TYPES = new ArrayList<>();
    static {
        for (OfferAttributeType type : OfferAttributeType.values()) {
            ATTRIBUTE_TYPES.add(type.name());
        }
    }

    @Override
    public List<OfferAttribute> getAttributes(OfferAttributesGetOptions options) {
        return attributeRepo.getAttributes(options);
    }

    @Override
    protected OfferAttributeRepository getRepo() {
        return attributeRepo;
    }

    @Override
    protected List<String> getTypes() {
        return ATTRIBUTE_TYPES;
    }

    @Override
    protected String getEntityType() {
        return "offer-attributes";
    }
}
