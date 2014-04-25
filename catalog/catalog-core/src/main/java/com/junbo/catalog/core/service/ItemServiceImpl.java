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
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.model.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
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
        if (!StringUtils.isEmpty(item.getRev())) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
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
        if (!StringUtils.isEmpty(revision.getRev())) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
        }
        if (!Status.DRAFT.equals(revision.getStatus())) {
            throw AppErrors.INSTANCE
                    .fieldNotCorrect("status", "status should be 'DRAFT' at item revision creation.").exception();
        }
        validateRevision(revision);
        return super.createRevision(revision);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        if (options.getTimestamp()!=null) {
            if (CollectionUtils.isEmpty(options.getItemIds())) {
                throw AppErrors.INSTANCE.validation("itemId must be specified when timestamp is present.").exception();
            }

            return itemRevisionRepo.getRevisions(options.getItemIds(), options.getTimestamp());

        } else {
            return itemRevisionRepo.getRevisions(options);
        }
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
        if (ItemType.DIGITAL.equals(item.getType())||ItemType.SUBSCRIPTION.equals(item.getType())) {
            EntitlementDefinition entitlementDef = new EntitlementDefinition();
            entitlementDef.setDeveloperId(item.getOwnerId());
            entitlementDef.setGroup(item.getItemId().toString());
            if (ItemType.DIGITAL.equals(item.getType())) {
                entitlementDef.setType(EntitlementType.DOWNLOAD.name());
            } else {
                entitlementDef.setType(EntitlementType.SUBSCRIPTION.name());
            }
            entitlementDef.setTag(item.getItemId().toString());
            Long entitlementDefId = entitlementDefService.createEntitlementDefinition(entitlementDef);
            item.setEntitlementDefId(entitlementDefId);
        }
    }

    private void validateItem(Item item) {
        checkFieldNotEmpty(item.getType(), "type");
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
            if (!item.getItemId().equals(item.getItemId())) {
                throw AppErrors.INSTANCE.validation("Current revision doesn't belong to this item").exception();
            }
            if (!Status.APPROVED.equals(revision.getStatus())) {
                throw AppErrors.INSTANCE.validation("Cannot set current revision to unapproved revision").exception();
            }
        }
    }

    private void validateRevision(ItemRevision revision) {
        checkFieldNotNull(revision.getItemId(), "item");
        checkFieldNotNull(revision.getOwnerId(), "developer");

        Item item = itemRepo.get(revision.getItemId());
        checkEntityNotNull(revision.getItemId(), item, "item");

        if (ItemType.DIGITAL.equals(item.getType())) {
            checkFieldNotNull(revision.getBinaries(), "binaries");
        } else if (ItemType.WALLET.equals(item.getType())) {
            checkFieldNotNull(revision.getWalletCurrencyType(), "walletCurrencyType");
            checkFieldNotNull(revision.getWalletCurrency(), "walletCurrency");
            checkFieldNotNull(revision.getWalletAmount(), "walletAmount");
        }

        checkFieldNotNull(revision.getLocales(), "locales");
        for (Map.Entry<String, ItemRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
            String locale = entry.getKey();
            ItemRevisionLocaleProperties properties = entry.getValue();
            // TODO: check locale is a valid locale
            checkFieldNotNull(properties, "Properties should not be null for locale " + locale);
            checkFieldNotNull(properties.getName(), locale + ".name");
        }

        if (revision.getMsrp()!=null) {
            checkPrice(revision.getMsrp());
        }
    }
}
