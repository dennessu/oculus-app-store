/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item service implementation.
 */
public class ItemServiceImpl  extends BaseRevisionedServiceImpl<Item, ItemRevision> implements ItemService {
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private ItemRevisionRepository itemRevisionRepo;
    @Autowired
    private EntitlementDefinitionService entitlementDefService;

    @Override
    public Item createEntity(Item item) {
        if (Boolean.TRUE.equals(item.getCurated())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("curated", "Cannot create an item with curated true.").exception();
        }
        validateItem(item);

        Long itemId = itemRepo.create(item);
        item.setItemId(itemId);
        generateEntitlementDef(item);
        itemRepo.update(item);

        return itemRepo.get(itemId);
    }

    @Override
    protected ItemRepository getEntityRepo() {
        return itemRepo;
    }

    @Override
    protected ItemRevisionRepository getRevisionRepo() {
        return itemRevisionRepo;
    }

    @Override
    protected String getEntityType() {
        return "item";
    }

    @Override
    protected String getRevisionType() {
        return "item-revision";
    }

    private void generateEntitlementDef(Item item) {
        if (ItemType.APP.equalsIgnoreCase(item.getType())) {
            EntitlementDefinition entitlementDef = new EntitlementDefinition();
            entitlementDef.setDeveloperId(item.getOwnerId());
            entitlementDef.setGroup(item.getItemId().toString());
            entitlementDef.setType(EntitlementType.DOWNLOAD.name());
            entitlementDef.setTag(item.getItemId().toString());
            Long entitlementDefId = entitlementDefService.createEntitlementDefinition(entitlementDef);
            item.setEntitlementDefId(entitlementDefId);
        }
    }

    private void validateItem(Item item) {
        checkFieldNotEmpty(item.getType(), "type");
        checkFieldNotEmpty(item.getName(), "name");
        checkFieldNotNull(item.getOwnerId(), "developer");

        if (item.getEntitlementDefId() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("entitlementDefinition").exception();
        }
    }
}
