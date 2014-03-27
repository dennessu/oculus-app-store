/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemDraftRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item service implementation.
 */
public class ItemServiceImpl extends BaseServiceImpl<Item> implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemDraftRepository itemDraftRepository;
    @Autowired
    private EntitlementDefinitionService entitlementDefService;

    @Override
    public ItemRepository getEntityRepo() {
        return itemRepository;
    }

    @Override
    public ItemDraftRepository getEntityDraftRepo() {
        return itemDraftRepository;
    }

    @Override
    public Item create(Item item) {
        validateItem(item);
        item.setStatus(Status.DESIGN);
        Long itemId = itemDraftRepository.create(item);
        item.setId(itemId);

        if (ItemType.APP.equalsIgnoreCase(item.getType())) {
            EntitlementDefinition entitlementDef = new EntitlementDefinition();
            entitlementDef.setDeveloperId(item.getOwnerId());
            entitlementDef.setGroup(itemId.toString());
            entitlementDef.setType(EntitlementType.DOWNLOAD.name());
            entitlementDef.setTag(item.getName());
            Long entitlementDefId = entitlementDefService.createEntitlementDefinition(entitlementDef);
            item.setEntitlementDefId(entitlementDefId);
        }
        itemDraftRepository.update(item);

        return itemDraftRepository.get(itemId);
    }

    @Override
    public Item update(Long entityId, Item entity) {
        validateId(entityId, entity);
        return updateEntity(entityId, entity);
    }

    private void validateItem(Item item) {
        checkFieldNotEmpty(item.getType(), "type");
        checkFieldNotEmpty(item.getName(), "name");
        checkFieldNotNull(item.getOwnerId(), "developer");

        if (item.getEntitlementDefId() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("entitlementDefinition").exception();
        }
    }

    @Override
    protected String getEntityType() {
        return "Item";
    }
}
