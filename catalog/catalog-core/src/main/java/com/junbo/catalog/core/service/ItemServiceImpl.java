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
import com.junbo.catalog.spec.model.common.ExtensibleProperties;
import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.model.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

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
    public List<Item> getItems(ItemsGetOptions options) {
        return itemRepo.getItems(options);
    }

    @Override
    public ItemRevision createRevision(ItemRevision revision) {
        validateRevision(revision);
        return super.createRevision(revision);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        return itemRevisionRepo.getRevisions(options);
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
        checkFieldNotNull(item.getName(), "name");
        checkFieldNotNull(item.getOwnerId(), "developer");

        if (!ItemType.ALL_TYPES.contains(item.getType())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("type", "Valid item types: " + ItemType.ALL_TYPES).exception();
        }

        if (item.getEntitlementDefId() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("entitlementDefinition").exception();
        }

        if (item.getCurrentRevisionId() != null) {
            ItemRevision revision = itemRevisionRepo.get(item.getCurrentRevisionId());
            checkEntityNotNull(item.getCurrentRevisionId(), revision, "item-revision");
            if (!Status.APPROVED.equals(revision.getStatus())) {
                throw AppErrors.INSTANCE.validation("Cannot set current revision to unapproved revision").exception();
            }
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
            ExtensibleProperties digitalProps = revision.getDigitalProperties();
            if (digitalProps == null) {
                throw AppErrors.INSTANCE.missingField("digitalProperties").exception();
            }
            if (StringUtils.isEmpty(revision.getDigitalProperties().get("downloadLink"))) {
                throw AppErrors.INSTANCE.missingField("digitalProperties.downloadLink").exception();
            }
        }

        if (revision.getName()==null || CollectionUtils.isEmpty(revision.getName().getLocales())) {
            throw AppErrors.INSTANCE.missingField("displayName").exception();
        }
        if (StringUtils.isEmpty(revision.getName().locale(LocalizableProperty.DEFAULT))) {
            throw AppErrors.INSTANCE.validation("displayName should have value for 'DEFAULT' locale.").exception();
        }

        if (revision.getMsrp()!=null) {
            checkPrice(revision.getMsrp());
        }
    }
}
