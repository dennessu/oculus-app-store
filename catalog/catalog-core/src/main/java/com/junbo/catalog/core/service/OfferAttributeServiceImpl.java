/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.OfferAttributeService;
import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.offer.OfferAttribute;
import com.junbo.catalog.spec.model.offer.OfferAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Offer attribute service implementation.
 */
public class OfferAttributeServiceImpl implements OfferAttributeService {
    @Autowired
    private OfferAttributeRepository attributeRepo;

    @Override
    public OfferAttribute getAttribute(Long attributeId) {
        OfferAttribute attribute = attributeRepo.get(attributeId);
        if (attribute == null) {
            throw AppErrors.INSTANCE.notFound("offer-attribute", attributeId).exception();
        }
        return attribute;
    }

    @Override
    public List<OfferAttribute> getAttributes(OfferAttributesGetOptions options) {
        return attributeRepo.getAttributes(options);
    }

    @Override
    public OfferAttribute create(OfferAttribute attribute) {
        Long attributeId = attributeRepo.create(attribute);
        return attributeRepo.get(attributeId);
    }

    @Override
    public OfferAttribute update(Long attributeId, OfferAttribute attribute) {
        if (!attributeId.equals(attribute.getId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", attribute.getId(), attributeId).exception();
        }
        if (StringUtils.isEmpty(attribute.getType())) {
            throw AppErrors.INSTANCE.missingField("type").exception();
        }
        OfferAttribute existingAttribute = attributeRepo.get(attributeId);
        if (existingAttribute==null) {
            throw AppErrors.INSTANCE.notFound("offer-attribute", attributeId).exception();
        }

        attributeRepo.update(attribute);

        return attributeRepo.get(attributeId);
    }

    @Override
    public void deleteAttribute(Long attributeId) {
        attributeRepo.delete(attributeId);
    }
}