/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.ItemAttributeService;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.ItemAttribute;
import com.junbo.catalog.spec.model.item.ItemAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Item attribute service implementation.
 */
public class ItemAttributeServiceImpl implements ItemAttributeService {
    @Autowired
    private ItemAttributeRepository attributeRepo;

    @Override
    public ItemAttribute getAttribute(Long attributeId) {
        ItemAttribute attribute = attributeRepo.get(attributeId);
        if (attribute == null) {
            throw AppErrors.INSTANCE.notFound("item-attribute", attributeId).exception();
        }
        return attribute;
    }

    @Override
    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        return attributeRepo.getAttributes(options);
    }

    @Override
    public ItemAttribute create(ItemAttribute attribute) {
        Long attributeId = attributeRepo.create(attribute);
        return attributeRepo.get(attributeId);
    }

    @Override
    public ItemAttribute update(Long attributeId, ItemAttribute attribute) {
        if (!attributeId.equals(attribute.getId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", attribute.getId(), attributeId).exception();
        }
        if (StringUtils.isEmpty(attribute.getType())) {
            throw AppErrors.INSTANCE.missingField("type").exception();
        }
        ItemAttribute existingAttribute = attributeRepo.get(attributeId);
        if (existingAttribute==null) {
            throw AppErrors.INSTANCE.notFound("item-attribute", attributeId).exception();
        }

        attributeRepo.update(attribute);

        return attributeRepo.get(attributeId);
    }

    @Override
    public void deleteAttribute(Long attributeId) {
        attributeRepo.delete(attributeId);
    }
}
