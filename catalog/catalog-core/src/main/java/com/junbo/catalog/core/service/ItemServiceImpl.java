/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.enums.ItemAttributeType;
import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.error.AppError;
import com.junbo.common.id.ItemId;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Item service implementation.
 */
public class ItemServiceImpl extends BaseRevisionedServiceImpl<Item, ItemRevision> implements ItemService {
    private ItemRepository itemRepo;
    private ItemRevisionRepository itemRevisionRepo;
    private ItemAttributeRepository itemAttributeRepo;
    private OfferRepository offerRepo;

    @Required
    public void setItemRevisionRepo(ItemRevisionRepository itemRevisionRepo) {
        this.itemRevisionRepo = itemRevisionRepo;
    }

    @Required
    public void setItemAttributeRepo(ItemAttributeRepository itemAttributeRepo) {
        this.itemAttributeRepo = itemAttributeRepo;
    }

    @Required
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Required
    public void setOfferRepo(OfferRepository offerRepo) {
        this.offerRepo = offerRepo;
    }

    @Override
    public Item createEntity(Item item) {
        validateItemCreation(item);

        return itemRepo.create(item);
    }

    @Override
    public Item updateEntity(Long itemId, Item item) {
        Item oldItem = itemRepo.get(itemId);
        if (oldItem == null) {
            throw AppErrors.INSTANCE.notFound("item", Utils.encodeId(itemId)).exception();
        }
        validateItemUpdate(item, oldItem);
        return itemRepo.update(item);
    }

    @Override
    public List<Item> getItems(ItemsGetOptions options) {
        if (!StringUtils.isEmpty(options.getQuery())) {
            // TODO
            return null;
        }else if (options.getHostItemId() != null) {
            List<ItemRevision> itemRevisions = itemRevisionRepo.getRevisions(options.getHostItemId().getValue());
            Set<Long> itemIds = filterItemIds(itemRevisions);

            return itemRepo.getItems(itemIds);
        } else {
            return itemRepo.getItems(options);
        }
    }

    private Set<Long> filterItemIds(List<ItemRevision> revisions) {
        if (CollectionUtils.isEmpty(revisions)) {
            return Collections.emptySet();
        }
        Set<Long> itemIds = new HashSet<>();
        Set<Long> revisionIds = new HashSet<>();
        for (ItemRevision itemRevision : revisions) {
            itemIds.add(itemRevision.getItemId());
            revisionIds.add(itemRevision.getRevisionId());
        }
        List<ItemRevision> itemRevisions = itemRevisionRepo.getRevisions(itemIds, System.currentTimeMillis());
        itemIds.clear();
        for (ItemRevision itemRevision : itemRevisions) {
            if (revisionIds.contains(itemRevision.getRevisionId())) {
                itemIds.add(itemRevision.getItemId());
            }
        }

        return itemIds;
    }

    @Override
    public ItemRevision createRevision(ItemRevision revision) {
        validateRevisionCreation(revision);
        generateEntitlementDef(revision);
        return itemRevisionRepo.create(revision);
    }

    @Override
    public ItemRevision updateRevision(Long revisionId, ItemRevision revision) {
        ItemRevision oldRevision = itemRevisionRepo.get(revisionId);
        if (oldRevision == null) {
            throw AppErrors.INSTANCE.notFound("item-revision", Utils.encodeId(revisionId)).exception();
        }
        if (Status.APPROVED.is(oldRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot update an approved revision").exception();
        }
        validateRevisionUpdate(revision, oldRevision);
        generateEntitlementDef(revision);
        return super.updateRevision(revisionId, revision);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        if (options.getTimestamp() != null) {
            if (CollectionUtils.isEmpty(options.getItemIds())) {
                throw AppErrors.INSTANCE.validation("itemId must be specified when timestamp is present.").exception();
            }
            Set<Long> itemIds = new HashSet<>();
            for (ItemId itemId : options.getItemIds()) {
                itemIds.add(itemId.getValue());
            }
            return itemRevisionRepo.getRevisions(itemIds, options.getTimestamp());
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

    private void generateEntitlementDef(ItemRevision revision) {
        Item item = itemRepo.get(revision.getItemId());
        if (revision.getEntitlementDefs() == null) {
            revision.setEntitlementDefs(new ArrayList<EntitlementDef>());
        }
        List<EntitlementDef> entitlementDefs = revision.getEntitlementDefs();
        if (ItemType.DIGITAL.is(item.getType())) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.DOWNLOAD, false);
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.RUN, false);
        }
    }

    private void addEntitlementIfNotExist(List<EntitlementDef> entitlementDefs,
                                          EntitlementType entitlementType, boolean consumable) {
        boolean exists = false;
        for (EntitlementDef entitlementDef : entitlementDefs) {
            if (entitlementType.is(entitlementDef.getType())) {
                exists = true;
            }
        }
        if (!exists) {
            EntitlementDef entitlementDef = new EntitlementDef();
            entitlementDef.setType(entitlementType.name());
            entitlementDef.setConsumable(consumable);
            entitlementDefs.add(entitlementDef);
        }
    }

    private void validateItemCreation(Item item) {
        checkRequestNotNull(item);
        List<AppError> errors = new ArrayList<>();
        if (item.getResourceAge() != null) {
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
        checkRequestNotNull(item);
        List<AppError> errors = new ArrayList<>();
        if (!oldItem.getItemId().equals(item.getItemId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", item.getItemId(), oldItem.getItemId()));
        }
        if (!isEqual(item.getCurrentRevisionId(), oldItem.getCurrentRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotCorrect("currentRevision", "The field can only be changed through revision approve"));
        }
        if (!oldItem.getResourceAge().equals(item.getResourceAge())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", item.getResourceAge(), oldItem.getResourceAge()));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateItemCommon(Item item, List<AppError> errors) {
        if (item.getOwnerId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("developer"));
        }
        if (item.getType() == null || !ItemType.contains(item.getType())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("type", "Valid types: " + Arrays.asList(ItemType.values())));
        }
        if (item.getDefaultOffer() != null) {
            Offer offer = offerRepo.get(item.getDefaultOffer());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.fieldNotCorrect("defaultOffer",
                        "Cannot find offer " + Utils.encodeId(item.getDefaultOffer())));
            }
        }
        if (!CollectionUtils.isEmpty(item.getGenres())) {
            for (Long genreId : item.getGenres()) {
                if (genreId == null) {
                    errors.add(AppErrors.INSTANCE.fieldNotCorrect("genres", "should not contain null"));
                } else {
                    ItemAttribute attribute = itemAttributeRepo.get(genreId);
                    if (attribute == null || !ItemAttributeType.GENRE.is(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE
                                .fieldNotCorrect("categories", "Cannot find genre " + Utils.encodeId(genreId)));
                    }
                }
            }
        }
    }

    private void validateRevisionCreation(ItemRevision revision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (revision.getResourceAge() != null) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", revision.getResourceAge(), null));
        }
        if (!Status.DRAFT.is(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("status", revision.getStatus(), Status.DRAFT));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }


    private void validateRevisionUpdate(ItemRevision revision, ItemRevision oldRevision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getResourceAge().equals(revision.getResourceAge())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("rev", revision.getResourceAge(), oldRevision.getResourceAge()));
        }
        if (revision.getStatus() == null || !Status.contains(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("status", "Valid statuses: " + Status.ALL));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateRevisionCommon(ItemRevision revision, List<AppError> errors) {
        if (revision.getOwnerId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("developer"));
        }
        if (revision.getItemId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("itemId"));
        } else {
            Item item = itemRepo.get(revision.getItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE
                        .fieldNotCorrect("itemId", "Cannot find item " + Utils.encodeId(revision.getItemId())));
            } else {
                if (ItemType.DIGITAL.is(item.getType())) {
                    if (CollectionUtils.isEmpty(revision.getBinaries())) {
                        errors.add(AppErrors.INSTANCE.missingField("binaries"));
                    }
                }
                if (!ItemType.DIGITAL.is(item.getType())) {
                    if (!CollectionUtils.isEmpty(revision.getBinaries())) {
                        errors.add(AppErrors.INSTANCE.unnecessaryField("binaries"));
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
                if (properties == null) {
                    errors.add(AppErrors.INSTANCE.missingField("locales." + locale));
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
