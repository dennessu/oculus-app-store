/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.google.common.base.Joiner;
import com.junbo.catalog.clientproxy.OrganizationFacade;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.core.validators.ItemRevisionValidator;
import com.junbo.catalog.db.repo.*;
import com.junbo.catalog.spec.enums.*;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
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
    private static final Integer MAX_NUMBER_OF_PURCHASE = 1000000000;

    private ItemRepository itemRepo;
    private ItemRevisionRepository itemRevisionRepo;
    private ItemAttributeRepository itemAttributeRepo;
    private OfferAttributeRepository offerAttributeRepo;
    private OfferRepository offerRepo;
    private ItemRevisionValidator revisionValidator;
    private OrganizationFacade organizationFacade;
    private OfferRevisionRepository offerRevisionRepo;

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

    @Required
    public void setOrganizationFacade(OrganizationFacade organizationFacade) {
        this.organizationFacade = organizationFacade;
    }

    @Required
    public void setOfferAttributeRepo(OfferAttributeRepository offerAttributeRepo) {
        this.offerAttributeRepo = offerAttributeRepo;
    }

    @Required
    public void setOfferRevisionRepo(OfferRevisionRepository offerRevisionRepo) {
        this.offerRevisionRepo = offerRevisionRepo;
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
    public Item getEntity(String entityId) {
        Item item = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, item, getEntityType());
        return normalizeLegacy(item);
    }

    @Override
    public List<Item> getItems(ItemsGetOptions options) {
        List<Item> items = itemRepo.getItems(options);
        for (Item item : items) {
            normalizeLegacy(item);
        }
        return items;
    }

    @Override
    public ItemRevision createRevision(ItemRevision revision) {
        revisionValidator.validateCreationBasic(revision);
        normalizeRequiredSpace(revision);
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
            generateEntitlementDef(revision, item);
            if (Status.APPROVED.is(revision.getStatus())) {
                revision.setTimestamp(Utils.currentTimestamp());

                item.setCurrentRevisionId(revisionId);
                item.setActiveRevision(revision);
                itemRepo.update(item, item);
            }
        } else {
            revisionValidator.validateUpdateBasic(revision, oldRevision);
        }
        normalizeRequiredSpace(revision);
        return itemRevisionRepo.update(revision, oldRevision);
    }

    @Override
    public ItemRevision getRevision(String revisionId) {
        ItemRevision revision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, revision, getRevisionType());
        normalizeRequiredSpace(revision);
        return revision;
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> revisions;
        if (options.getTimestamp() != null) {
            if (CollectionUtils.isEmpty(options.getItemIds())) {
                AppErrorException exception =
                        AppCommonErrors.INSTANCE.parameterInvalid("itemId", "itemId must be specified when timestamp is present.").exception();
                LOGGER.error("Error getting item-revisions. ", exception);
                throw exception;
            }
            revisions = itemRevisionRepo.getRevisions(options.getItemIds(), options.getTimestamp());
            options.setTotal(Long.valueOf(revisions.size()));
        } else {
            revisions = itemRevisionRepo.getRevisions(options);
        }

        for (ItemRevision revision : revisions) {
            normalizeRequiredSpace(revision);
        }

        return revisions;
    }

    @Override
    public void deleteEntity(String entityId) {
        List<OfferRevision> offerRevisions = offerRevisionRepo.getRevisions(entityId);
        if (offerRevisions.size() > 0) {
            throw AppErrors.INSTANCE.itemReferenced(getEntityType(), entityId, (long)(offerRevisions.size()), "offer-revisions").exception();
        }
        super.deleteEntity(entityId);
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

    private Item normalizeLegacy(Item item) {
        if (item==null) {
            return null;
        }
        if (ItemType.APP.is(item.getType())) {
            if (item.getIsDownloadable()==null) {
                item.setIsDownloadable(true);
            }
            if (item.getMaxNumberOfPurchase()==null) {
                item.setMaxNumberOfPurchase(1);
            }
        } else if (ItemType.PHYSICAL.is(item.getType())) {
            if (item.getIsDownloadable()==null) {
                item.setIsDownloadable(false);
            }
            if (item.getMaxNumberOfPurchase()==null) {
                item.setMaxNumberOfPurchase(MAX_NUMBER_OF_PURCHASE);
            }
        }
        return item;
    }

    private void generateEntitlementDef(ItemRevision revision, Item item) {
        if (revision.getEntitlementDefs() == null) {
            revision.setEntitlementDefs(new ArrayList<EntitlementDef>());
        }
        List<EntitlementDef> entitlementDefs = revision.getEntitlementDefs();
        String itemType = item.getType(), itemSubtype = item.getSubtype();
        if (ItemType.APP.is(itemType) || ItemType.ADDITIONAL_CONTENT.is(itemType) && (ItemSubtype.PHOTO.is(itemSubtype) || ItemSubtype.VIDEO.is(itemSubtype))) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.DOWNLOAD, false);
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.RUN, false);
        } else if (ItemType.ADDITIONAL_CONTENT.is(itemType) && ItemSubtype.DOWNLOADABLE_ADDITION.is(itemSubtype)) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.DOWNLOAD, false);
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.ALLOW_IN_APP, false);
        } else if (ItemType.ADDITIONAL_CONTENT.is(itemType) && itemSubtype == null && item.getMaxNumberOfPurchase()==1) {
            addEntitlementIfNotExist(entitlementDefs, EntitlementType.ALLOW_IN_APP, false);
        } else if (ItemType.ADDITIONAL_CONTENT.is(itemType) && itemSubtype == null && item.getMaxNumberOfPurchase()!=1) {
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
        } else if (organizationFacade.getOrganization(item.getOwnerId()) == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("developer", "Cannot find organization " + Utils.encodeId(item.getOwnerId())));
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
        } else if (organizationFacade.getOrganization(item.getOwnerId()) == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("developer", "Cannot find organization " + Utils.encodeId(item.getOwnerId())));
        }

        validateItemCommon(item, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item. ", exception);
            throw exception;
        }
    }

    private void validateItemCommon(Item item, List<AppError> errors) {
        if (!ItemType.contains(item.getType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("type", Joiner.on(", ").join(ItemType.values())));
        }
        if (ItemType.ADDITIONAL_CONTENT.is(item.getType())) {
            if (item.getSubtype() != null && !ItemSubtype.contains(item.getSubtype())) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("subtype", Joiner.on(", ").join(ItemSubtype.values()) + ", null"));
            }
        } else if (item.getSubtype() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("subtype"));
        }
        if (ItemType.APP.is(item.getType()) || ItemType.ADDITIONAL_CONTENT.is(item.getType()) && ItemSubtype.contains(item.getSubtype())) {
            if (item.getIsDownloadable() != Boolean.TRUE) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("isDownloadable", "Expected value is true."));
            }
            if (item.getMaxNumberOfPurchase()==null || item.getMaxNumberOfPurchase() != 1) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("maxNumberOfPurchase", "Expected value is 1"));
            }
        } else if (item.getIsDownloadable() != Boolean.FALSE) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("isDownloadable", "Expected value is false"));
        }
        if (ItemType.PHYSICAL.is(item.getType()) || ItemType.SUBSCRIPTION.is(item.getType()) || ItemType.EWALLET.is(item.getType())) {
            if (item.getMaxNumberOfPurchase()==null || item.getMaxNumberOfPurchase()<MAX_NUMBER_OF_PURCHASE) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("maxNumberOfPurchase", "should be greater than or equals to " + MAX_NUMBER_OF_PURCHASE));
            }
        }
        if (ItemType.ADDITIONAL_CONTENT.is(item.getType()) && item.getSubtype() == null) {
            if (item.getMaxNumberOfPurchase() == null || item.getMaxNumberOfPurchase() !=1 && item.getMaxNumberOfPurchase() < MAX_NUMBER_OF_PURCHASE) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("maxNumberOfPurchase", "should be either 1 or not less than " + MAX_NUMBER_OF_PURCHASE));
            }
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
        if (!CollectionUtils.isEmpty(item.getCategories())) {
            for (String categoryId : item.getCategories()) {
                if (categoryId == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("categories"));
                } else {
                    OfferAttribute attribute = offerAttributeRepo.get(categoryId);
                    if (attribute == null || !OfferAttributeType.CATEGORY.is(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE.categoryNotFound("categories", categoryId));
                    }
                }
            }
        }
    }

    private void normalizeRequiredSpace(ItemRevision revision) {
        if (revision.getBinaries() == null) {
            return;
        }
        for (Binary binary : revision.getBinaries().values()) {
            if (binary.getSize() == null) {
                binary.setSize(0L);
            }
            if (binary.getRequiredSpace() == null) {
                binary.setRequiredSpace(binary.getSize() * 2);
            }
        }
    }
}
