/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.ItemAttributeService;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.enums.ItemAttributeType;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Item attribute service implementation.
 */
public class ItemAttributeServiceImpl extends AttributeServiceSupport<ItemAttribute> implements ItemAttributeService {
    private ItemAttributeRepository attributeRepo;
    private ItemRepository itemRepo;
    private static final List<String> ATTRIBUTE_TYPES = new ArrayList<>();
    static {
        for (ItemAttributeType type : ItemAttributeType.values()) {
            ATTRIBUTE_TYPES.add(type.name());
        }
    }

    @Required
    public void setAttributeRepo(ItemAttributeRepository attributeRepo) {
        this.attributeRepo = attributeRepo;
    }

    @Required
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Override
    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        return attributeRepo.getAttributes(options);
    }

    @Override
    public void deleteAttribute(String attributeId) {
        ItemsGetOptions options = new ItemsGetOptions();
        options.setGenre(attributeId);
        itemRepo.getItems(options);
        if (options.getTotal() > 0) {
            throw AppErrors.INSTANCE.genreReferenced(getEntityType(), attributeId, options.getTotal(), "items").exception();
        }
        super.deleteAttribute(attributeId);
    }

    @Override
    protected ItemAttributeRepository getRepo() {
        return attributeRepo;
    }

    @Override
    protected List<String> getTypes() {
        return ATTRIBUTE_TYPES;
    }

    @Override
    protected String getEntityType() {
        return "item-attributes";
    }
}
