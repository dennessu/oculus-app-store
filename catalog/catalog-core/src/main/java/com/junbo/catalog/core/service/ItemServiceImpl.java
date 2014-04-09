/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Locale;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

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
    public ItemRevision createRevision(ItemRevision revision) {
        validateRevision(revision);
        return super.createRevision(revision);
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
        if (ItemType.DIGITAL.equalsIgnoreCase(item.getType())) {
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

        if (!ItemType.ALL_TYPES.contains(item.getType())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("type", "Valid item types: " + ItemType.ALL_TYPES).exception();
        }

        if (item.getEntitlementDefId() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("entitlementDefinition").exception();
        }
    }

    private void validateRevision(ItemRevision revision) {
        checkFieldNotEmpty(revision.getType(), "type");
        checkFieldNotNull(revision.getItemId(), "item");
        checkFieldNotNull(revision.getOwnerId(), "developer");

        Item item = itemRepo.get(revision.getItemId());
        checkEntityNotNull(revision.getItemId(), item, "item");
        if (!revision.getType().equals(item.getType())) {
            throw AppErrors.INSTANCE.fieldNotMatch("type", revision.getType(), item.getType()).exception();
        }

        if (ItemType.DIGITAL.equals(revision.getType())) {
            Map<String, Object> digitalProps = revision.getDigitalProperties();
            if (digitalProps == null) {
                throw AppErrors.INSTANCE.missingField("digitalProperties").exception();
            }
            if (StringUtils.isEmpty(digitalProps.get("downloadLink"))) {
                throw AppErrors.INSTANCE.missingField("digitalProperties.downloadLink").exception();
            }
        }

        if (CollectionUtils.isEmpty(revision.getDisplayName())) {
            throw AppErrors.INSTANCE.missingField("displayName").exception();
        }
        if (StringUtils.isEmpty(revision.getDisplayName().get(Locale.DEFAULT))) {
            throw AppErrors.INSTANCE.validation("displayName should have value for 'DEFAULT' locale.").exception();
        }
    }
}
