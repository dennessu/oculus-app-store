/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.error.AppError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    @Autowired
    private ItemAttributeRepository itemAttributeRepo;
    @Autowired
    private OfferRepository offerRepo;

    @Override
    public Item createEntity(Item item) {
        validateItemCreation(item);

        Long itemId = itemRepo.create(item);
        item.setItemId(itemId);
        generateEntitlementDef(item);
        itemRepo.update(item);

        return itemRepo.get(itemId);
    }

    @Override
    public Item updateEntity(Long itemId, Item item) {
        Item oldItem = itemRepo.get(itemId);
        if (oldItem == null) {
            throw AppErrors.INSTANCE.notFound("item", Utils.encodeId(itemId)).exception();
        }
        validateItemUpdate(item, oldItem);
        itemRepo.update(item);
        return itemRepo.get(itemId);
    }

    @Override
    public List<Item> getItems(ItemsGetOptions options) {
        return itemRepo.getItems(options);
    }

    @Override
    public ItemRevision createRevision(ItemRevision revision) {
        validateRevisionCreation(revision);
        Long revisionId = itemRevisionRepo.create(revision);
        return itemRevisionRepo.get(revisionId);
    }

    @Override
    public ItemRevision updateRevision(Long revisionId, ItemRevision revision) {
        ItemRevision oldRevision = itemRevisionRepo.get(revisionId);
        if (oldRevision==null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", Utils.encodeId(revisionId)).exception();
        }
        if (Status.APPROVED.equals(oldRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot update an approved revision").exception();
        }
        validateRevisionUpdate(revision, oldRevision);
        return super.updateRevision(revisionId, revision);
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
            entitlementDef.setGroup(Utils.encodeId(item.getItemId()));
            if (ItemType.DIGITAL.equals(item.getType())) {
                entitlementDef.setType(EntitlementType.DOWNLOAD.name());
            } else {
                entitlementDef.setType(EntitlementType.SUBSCRIPTION.name());
            }
            entitlementDef.setTag(Utils.encodeId(item.getItemId()));
            Long entitlementDefId = entitlementDefService.createEntitlementDefinition(entitlementDef);
            item.setEntitlementDefId(entitlementDefId);
        }
    }

    private void validateItemCreation(Item item) {
        List<AppError> errors = new ArrayList<>();
        if (!StringUtils.isEmpty(item.getRev())) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("rev"));
        }
        if (item.getCurrentRevisionId() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("currentRevision"));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateItemUpdate(Item item, Item oldItem) {
        List<AppError> errors = new ArrayList<>();
        if (!oldItem.getItemId().equals(item.getItemId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", item.getItemId(), oldItem.getItemId()));
        }
        if (!isEqual(item.getCurrentRevisionId(), oldItem.getCurrentRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotCorrect("currentRevision", "The field can only be changed through revision approve"));
        }
        if (!oldItem.getRev().equals(item.getRev())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", item.getRev(), oldItem.getRev()));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateItemCommon(Item item, List<AppError> errors) {
        if (item.getOwnerId()==null) {
            errors.add(AppErrors.INSTANCE.missingField("developer"));
        }
        if (item.getType()==null || !ItemType.ALL_TYPES.contains(item.getType())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("type", "Valid types: " + ItemType.ALL_TYPES));
        }
        if (item.getDefaultOffer() != null) {
            Offer offer = offerRepo.get(item.getDefaultOffer());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.fieldNotCorrect("defaultOffer",
                        "Cannot find offer " + Utils.encodeId(item.getDefaultOffer())));
            }
        }
        if (item.getEntitlementDefId() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("entitlementDefId"));
        }
        if (!CollectionUtils.isEmpty(item.getGenres())) {
            for (Long genreId : item.getGenres()) {
                if (genreId == null) {
                    errors.add(AppErrors.INSTANCE.fieldNotCorrect("genres", "should not contain null"));
                } else {
                    ItemAttribute attribute = itemAttributeRepo.get(genreId);
                    if (attribute == null || !"GENRE".equals(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE
                                .fieldNotCorrect("categories", "Cannot find genre " + Utils.encodeId(genreId)));
                    }
                }
            }
        }
    }

    private void validateRevisionCreation(ItemRevision revision) {
        List<AppError> errors = new ArrayList<>();
        if (!StringUtils.isEmpty(revision.getRev())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", revision.getRev(), null));
        }
        if (!Status.DRAFT.equals(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("status", revision.getStatus(), Status.DRAFT));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }


    private void validateRevisionUpdate(ItemRevision revision, ItemRevision oldRevision) {
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getRev().equals(revision.getRev())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", revision.getRev(), oldRevision.getRev()));
        }
        if (revision.getStatus()==null || !Status.ALL_STATUSES.contains(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("status", "Valid statuses: " + Status.ALL_STATUSES));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateRevisionCommon(ItemRevision revision, List<AppError> errors) {
        if (revision.getOwnerId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("publisher"));
        }
        if (revision.getItemId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("itemId"));
        } else {
            Item item = itemRepo.get(revision.getItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE
                        .fieldNotCorrect("itemId", "Cannot find item " + Utils.encodeId(revision.getItemId())));
            } else {
                if (ItemType.DIGITAL.equals(item.getType())) {
                    if (CollectionUtils.isEmpty(revision.getBinaries())) {
                        errors.add(AppErrors.INSTANCE.missingField("binaries"));
                    }
                } else if (ItemType.WALLET.equals(item.getType())) {
                    if (!"REAL_CURRENCY".equals(revision.getWalletCurrencyType())) {
                        errors.add(AppErrors.INSTANCE.fieldNotMatch("walletCurrencyType",
                                revision.getWalletCurrencyType(), "REAL_CURRENCY"));
                    }
                    if (StringUtils.isEmpty(revision.getWalletCurrency())) {
                        errors.add(AppErrors.INSTANCE.missingField("walletCurrency"));
                    }
                    if (revision.getWalletAmount()==null) {
                        errors.add(AppErrors.INSTANCE.missingField("walletAmount"));
                    } else if (revision.getWalletAmount().compareTo(BigDecimal.ZERO)<0) {
                        errors.add(AppErrors.INSTANCE.fieldNotCorrect("walletAmount", "Should not less than 0"));
                    }
                }
            }
        }
        if (revision.getMsrp() != null) {
            checkPrice(revision.getMsrp(), errors);
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppErrors.INSTANCE.missingField("locales"));
        } else {
            for (Map.Entry<String, ItemRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                ItemRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
                    errors.add(AppErrors.INSTANCE.fieldNotCorrect("locales." + locale, "Should not be null"));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppErrors.INSTANCE.missingField("locales." + locale + ".name"));
                    }
                }
            }
        }
        // TODO: check other properties
    }

}
