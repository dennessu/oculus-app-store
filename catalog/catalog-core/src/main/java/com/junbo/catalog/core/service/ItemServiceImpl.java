/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.core.validators.ItemRevisionValidator;
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
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Item service implementation.
 */
public class ItemServiceImpl extends BaseRevisionedServiceImpl<Item, ItemRevision> implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceImpl.class);

    private ItemRepository itemRepo;
    private ItemRevisionRepository itemRevisionRepo;
    private ItemAttributeRepository itemAttributeRepo;
    private OfferRepository offerRepo;
    private ItemRevisionValidator revisionValidator;

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

    @Required
    public void setRevisionValidator(ItemRevisionValidator revisionValidator) {
        this.revisionValidator = revisionValidator;
    }

    @Override
    public Item createEntity(Item item) {
        validateItemCreation(item);

        return itemRepo.create(item);
    }

    @Override
    public Item updateEntity(String itemId, Item item) {
        Item oldItem = itemRepo.get(itemId);
        if (oldItem == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound("item", itemId).exception();
            LOGGER.error("Error updating item. ", exception);
            throw exception;
        }
        validateItemUpdate(item, oldItem);

        item.setActiveRevision(oldItem.getActiveRevision());
        return itemRepo.update(item, oldItem);
    }

    @Override
    public List<Item> getItems(ItemsGetOptions options) {
        return itemRepo.getItems(options);
    }

    @Override
    public ItemRevision createRevision(ItemRevision revision) {
        revisionValidator.validateCreationBasic(revision);
        return itemRevisionRepo.create(revision);
    }

    @Override
    public ItemRevision updateRevision(String revisionId, ItemRevision revision) {
        ItemRevision oldRevision = itemRevisionRepo.get(revisionId);
        if (oldRevision == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound("item-revision", revisionId).exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
        if (Status.APPROVED.is(oldRevision.getStatus())) {
            AppErrorException exception =
                    AppCommonErrors.INSTANCE.invalidOperation("Cannot update an approved revision").exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
        if (Status.APPROVED.is(revision.getStatus()) || Status.PENDING_REVIEW.is(revision.getStatus())) {
            revisionValidator.validateFull(revision, oldRevision);
            Item item = itemRepo.get(revision.getItemId());
            generateEntitlementDef(revision, item.getType());
            if (Status.APPROVED.is(revision.getStatus())) {
                revision.setTimestamp(Utils.currentTimestamp());

                item.setCurrentRevisionId(revisionId);
                item.setActiveRevision(revision);
                itemRepo.update(item, item);
            }
        } else {
            revisionValidator.validateUpdateBasic(revision, oldRevision);
        }
        return itemRevisionRepo.update(revision, oldRevision);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        if (options.getTimestamp() != null) {
            if (CollectionUtils.isEmpty(options.getItemIds())) {
                AppErrorException exception =
                        AppCommonErrors.INSTANCE.parameterInvalid("itemId", "itemId must be specified when timestamp is present.").exception();
                LOGGER.error("Error getting item-revisions. ", exception);
                throw exception;
            }
            List<ItemRevision> revisions = itemRevisionRepo.getRevisions(options.getItemIds(), options.getTimestamp());
            options.setTotal(Long.valueOf(revisions.size()));
            return revisions;
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

    private void generateEntitlementDef(ItemRevision revision, String itemType) {
        if (revision.getEntitlementDefs() == null) {
            revision.setEntitlementDefs(new ArrayList<EntitlementDef>());
        }
        List<EntitlementDef> entitlementDefs = revision.getEntitlementDefs();
        if (ItemType.APP.is(itemType) || ItemType.VIDEO.is(itemType) || ItemType.PHOTO.is(itemType)) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.DOWNLOAD, false);
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.RUN, false);
        } else if (ItemType.DOWNLOADED_ADDITION.is(itemType)) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.DOWNLOAD, false);
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.ALLOW_IN_APP, false);
        } else if (ItemType.PERMANENT_UNLOCK.is(itemType)) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.ALLOW_IN_APP, false);
        } else if (ItemType.CONSUMABLE_UNLOCK.is(itemType)) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.ALLOW_IN_APP, true);
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
        if (item.getId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("self"));
        }
        if (item.getRev() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("rev"));
        }
        if (item.getCurrentRevisionId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("currentRevision"));
        }
        if (item.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("developer"));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating item. ", exception);
            throw exception;
        }
    }

    private void validateItemUpdate(Item item, Item oldItem) {
        checkRequestNotNull(item);
        List<AppError> errors = new ArrayList<>();
        if (!oldItem.getItemId().equals(item.getItemId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("self.id", item.getItemId(), oldItem.getItemId()));
        }
        if (!isEqual(item.getCurrentRevisionId(), oldItem.getCurrentRevisionId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("currentRevision", item.getCurrentRevisionId(), oldItem.getCurrentRevisionId()));
        }
        if (!oldItem.getRev().equals(item.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", item.getRev(), oldItem.getRev()));
        }
        if (!oldItem.getType().equals(item.getType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("type", item.getType(), oldItem.getType()));
        }
        if (!oldItem.getOwnerId().equals(item.getOwnerId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("developer", Utils.encodeId(item.getOwnerId()), Utils.encodeId(oldItem.getOwnerId())));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item. ", exception);
            throw exception;
        }
    }

    private void validateItemCommon(Item item, List<AppError> errors) {
        if (item.getType() == null || !ItemType.contains(item.getType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("type", Joiner.on(", ").join(ItemType.values())));
        }
        if (item.getDefaultOffer() != null) {
            Offer offer = offerRepo.get(item.getDefaultOffer());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.offerNotFound("defaultOffer", item.getDefaultOffer()));
            }
        }
        if (!CollectionUtils.isEmpty(item.getGenres())) {
            for (String genreId : item.getGenres()) {
                if (genreId == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("genres"));
                } else {
                    ItemAttribute attribute = itemAttributeRepo.get(genreId);
                    if (attribute == null || !ItemAttributeType.GENRE.is(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE.genreNotFound("genres", genreId));
                    }
                }
            }
        }
    }
}
