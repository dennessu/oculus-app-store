/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.enums.*;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Item service implementation.
 */
public class ItemServiceImpl extends BaseRevisionedServiceImpl<Item, ItemRevision> implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceImpl.class);

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
        validateRevisionCreation(revision);
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
            validateRevisionUpdate(revision, oldRevision);
            Item item = itemRepo.get(revision.getItemId());
            generateEntitlementDef(revision, item.getType());
            if (Status.APPROVED.is(revision.getStatus())) {
                revision.setTimestamp(Utils.currentTimestamp());

                item.setCurrentRevisionId(revisionId);
                item.setActiveRevision(revision);
                itemRepo.update(item, item);
            }
        } else {
            validateRevisionUpdateBasic(revision, oldRevision);
        }
        return itemRevisionRepo.update(revision, oldRevision);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        if (options.getTimestamp() != null) {
            if (CollectionUtils.isEmpty(options.getItemIds())) {
                AppErrorException exception =
                        AppCommonErrors.INSTANCE.parameterInvalid("itemId must be specified when timestamp is present.").exception();
                LOGGER.error("Error getting item-revisions. ", exception);
                throw exception;
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

    private void generateEntitlementDef(ItemRevision revision, String itemType) {
        if (revision.getEntitlementDefs() == null) {
            revision.setEntitlementDefs(new ArrayList<EntitlementDef>());
        }
        List<EntitlementDef> entitlementDefs = revision.getEntitlementDefs();
        if (ItemType.APP.is(itemType)) {
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

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item. ", exception);
            throw exception;
        }
    }

    private void validateItemCommon(Item item, List<AppError> errors) {
        if (item.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("developer"));
        }
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

    private void validateRevisionCreation(ItemRevision revision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (revision.getRevisionId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("self"));
        }
        if (revision.getRev() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", revision.getRev(), null));
        }
        if (revision.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("developer"));
        }
        if (!Status.DRAFT.is(revision.getStatus())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("status", "should be 'DRAFT'"));
        }
        if (revision.getItemId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("itemId"));
        } else {
            Item item = itemRepo.get(revision.getItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE.itemNotFound("itemId", revision.getItemId()));
            }
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, ItemRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                ItemRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale + ".name"));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating item-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionUpdateBasic(ItemRevision revision, ItemRevision oldRevision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getRev().equals(revision.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", revision.getRev(), oldRevision.getRev()));
        }
        if (revision.getStatus() != null && !Status.contains(revision.getStatus())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("status", Joiner.on(", ").join(Status.ALL)));
        }
        if (revision.getItemId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("itemId"));
        } else {
            Item item = itemRepo.get(revision.getItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE.itemNotFound("itemId", revision.getItemId()));
            }
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, ItemRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                ItemRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale + ".name"));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionUpdate(ItemRevision revision, ItemRevision oldRevision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getRev().equals(revision.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", revision.getRev(), oldRevision.getRev()));
        }
        if (revision.getStatus() == null || !Status.contains(revision.getStatus())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("status", Joiner.on(", ").join(Status.ALL)));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionCommon(ItemRevision revision, List<AppError> errors) {
        if (revision.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("developer"));
        }
        if (CollectionUtils.isEmpty(revision.getDistributionChannels())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("distributionChannel"));
        } else {
            int i;
            for (i=0; i < revision.getDistributionChannels().size(); i++) {
                if (!DistributionChannel.contains(revision.getDistributionChannels().get(i))) {
                    break;
                }
            }
            if (i < revision.getDistributionChannels().size()) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("distributionChannel", Joiner.on(", ").join(DistributionChannel.ALL)));
            }

            if (revision.getDistributionChannels().contains(DistributionChannel.INAPP)) {
                if (CollectionUtils.isEmpty(revision.getIapHostItemIds())) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("iapHostItems"));
                } else {
                    for (String itemId : revision.getIapHostItemIds()) {
                        if (itemId == null) {
                            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("iapHostItems", "should not contain null"));
                        } else {
                            Item item = itemRepo.get(itemId);
                            if (item == null) {
                                errors.add(AppErrors.INSTANCE.itemNotFound("iapHostItems", itemId));
                            }
                        }
                    }
                }
            }
        }
        if (revision.getItemId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("itemId"));
        } else {
            Item item = itemRepo.get(revision.getItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE.itemNotFound("itemId", revision.getItemId()));
            } else {
                if ((Status.APPROVED.is(revision.getStatus()) || Status.PENDING_REVIEW.is(revision.getStatus()))
                        && (ItemType.APP.is(item.getType()) || ItemType.DOWNLOADED_ADDITION.is(item.getType()))) {
                    if (CollectionUtils.isEmpty(revision.getBinaries())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("binaries"));
                    }
                    if (StringUtils.isEmpty(revision.getDownloadName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("downloadName"));
                    }
                }
                if (!(ItemType.APP.is(item.getType()) || ItemType.DOWNLOADED_ADDITION.is(item.getType()))) {
                    if (!CollectionUtils.isEmpty(revision.getBinaries())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("binaries"));
                    }
                    if (!StringUtils.isEmpty(revision.getDownloadName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("downloadName"));
                    }
                }
            }
            if (revision.getPackageName() != null) {
                boolean valid = itemRevisionRepo.checkPackageName(revision.getItemId(), revision.getPackageName());
                if (!valid) {
                    errors.add(AppErrors.INSTANCE.duplicatePackageName(revision.getPackageName()));
                }
            }
        }
        if (revision.getMsrp() != null) {
            checkPrice(revision.getMsrp(), errors);
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, ItemRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                ItemRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale + ".name"));
                    }
                }
            }
        }
        // TODO: check other properties
    }

}
