/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Configuration;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.enums.*;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseRevisionedServiceImpl<Offer, OfferRevision> implements OfferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceImpl.class);
    private OfferRepository offerRepo;
    private OfferRevisionRepository offerRevisionRepo;
    private ItemRepository itemRepo;
    private OfferAttributeRepository offerAttributeRepo;

    @Autowired
    private Configuration config;

    @Required
    public void setOfferRepo(OfferRepository offerRepo) {
        this.offerRepo = offerRepo;
    }

    @Required
    public void setOfferRevisionRepo(OfferRevisionRepository offerRevisionRepo) {
        this.offerRevisionRepo = offerRevisionRepo;
    }

    @Required
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Required
    public void setOfferAttributeRepo(OfferAttributeRepository offerAttributeRepo) {
        this.offerAttributeRepo = offerAttributeRepo;
    }

    @Override
    public Offer createEntity(Offer offer) {
        validateOfferCreation(offer);
        fillDefaultValue(offer);
        return offerRepo.create(offer);
    }

    @Override
    public Offer updateEntity(String offerId, Offer offer) {
        Offer oldOffer = offerRepo.get(offerId);
        if (oldOffer == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound("offer", offerId).exception();
            LOGGER.error("Error updating offer. ", exception);
            throw exception;
        }
        validateOfferUpdate(offer, oldOffer);

        offer.setCurrentRevisionId(oldOffer.getCurrentRevisionId());
        offer.setApprovedRevisions(oldOffer.getApprovedRevisions());
        offer.setActiveRevision(oldOffer.getActiveRevision());
        fillDefaultValue(offer);
        Offer updatedOffer = offerRepo.update(offer, oldOffer);
        updatedOffer.setCurrentRevisionId(oldOffer.getCurrentRevisionId());
        return updatedOffer;
    }

    @Override
    public Offer getEntity(String entityId) {
        Offer offer = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, offer, getEntityType());
        offer.setCurrentRevisionId(getCurrentRevisionId(offer.getApprovedRevisions()));
        return offer;
    }

    @Override
    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers;
        if (options.getItemId() != null) {
            offers = getOffersByItemId(options.getItemId());
        } else {
            offers = offerRepo.getOffers(options);
        }

        for (Offer offer : offers) {
            if (offer.getCurrentRevisionId() == null) {
                offer.setCurrentRevisionId(getCurrentRevisionId(offer.getApprovedRevisions()));
            }
        }

        return offers;
    }

    private String getCurrentRevisionId(Map<String, RevisionInfo> approvedRevisions) {
        if (approvedRevisions == null) {
            return null;
        }
        Long timestamp = Utils.currentTimestamp();
        String revisionId = null;
        Long approvedTime = 0L;
        for (RevisionInfo revisionInfo : approvedRevisions.values()) {
            if (revisionInfo.getApprovedTime() > approvedTime
                    && revisionInfo.getStartTime() <= timestamp && revisionInfo.getEndTime() > timestamp) {
                revisionId = revisionInfo.getRevisionId();
                approvedTime = revisionInfo.getApprovedTime();
            }
        }

        return revisionId;
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        if (options.getTimestamp()!=null) {
            if (CollectionUtils.isEmpty(options.getOfferIds())) {
                AppErrorException exception = AppCommonErrors.INSTANCE.parameterInvalid("offerId must be specified when timeInMillis is present.").exception();
                LOGGER.error("Invalid request. ", exception);
                throw exception;
            }

            Set<String> offerIds = new HashSet<>();
            for (String offerId : options.getOfferIds()) {
                offerIds.add(offerId);
            }
            return offerRevisionRepo.getRevisions(offerIds, options.getTimestamp());
        } else {
            return offerRevisionRepo.getRevisions(options);
        }
    }

    @Override
    public OfferRevision createRevision(OfferRevision revision) {
        validateRevisionCreation(revision);
        generateEventActions(revision);
        return offerRevisionRepo.create(revision);
    }

    @Override
    public OfferRevision updateRevision(String revisionId, OfferRevision revision) {
        OfferRevision oldRevision = offerRevisionRepo.get(revisionId);
        if (oldRevision==null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound("offer-revision", revisionId).exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
        if (Status.APPROVED.is(oldRevision.getStatus())) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidOperation("Cannot update an approved revision").exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
        if (Status.APPROVED.is(revision.getStatus()) || Status.PENDING_REVIEW.is(revision.getStatus())) {
            validateRevisionUpdate(revision, oldRevision);
            generateEventActions(revision);

            if (Status.APPROVED.is(revision.getStatus())) {
                Long timestamp = Utils.currentTimestamp();
                revision.setTimestamp(timestamp);
                if (revision.getStartTime() == null || revision.getCreatedTime().getTime() < timestamp) {
                    revision.setStartTime(new Date(timestamp));
                }

                updateOfferForApprovedRevision(revision, timestamp);
            }
        } else {
            validateRevisionUpdateBasic(revision, oldRevision);
        }
        return offerRevisionRepo.update(revision, oldRevision);
    }

    private void updateOfferForApprovedRevision(OfferRevision revision, Long timestamp) {
        Offer offer = offerRepo.get(revision.getOfferId());
        offer.setPublished(true);

        if (revision.getStartTime().getTime() <= timestamp
                && revision.getEndTime() == null || revision.getEndTime().after(Utils.maxDate())) {
            offer.setCurrentRevisionId(revision.getRevisionId());
            offer.setActiveRevision(revision);
            if (offer.getApprovedRevisions() != null) {
                offer.getApprovedRevisions().clear();
            }
        } else {
            offer.setCurrentRevisionId(null);
        }
        if (revision.getStartTime().getTime() >= timestamp
                && revision.getEndTime() == null || revision.getEndTime().getTime() >= timestamp) {
            offer.setActiveRevision(revision);
        }
        if (offer.getApprovedRevisions() == null) {
            offer.setApprovedRevisions(new HashMap<String, RevisionInfo>());
        }
        offer.getApprovedRevisions().put(revision.getRevisionId(), getRevisionInfo(revision, timestamp));

        offerRepo.update(offer, offer);
    }

    private RevisionInfo getRevisionInfo(OfferRevision revision, Long timestamp) {
        RevisionInfo revisionInfo = new RevisionInfo();
        revisionInfo.setApprovedTime(timestamp);
        revisionInfo.setRevisionId(revision.getRevisionId());
        revisionInfo.setStartTime(revision.getStartTime().getTime());
        if (revision.getEndTime() != null) {
            revisionInfo.setEndTime(revision.getEndTime().getTime());
        } else {
            revisionInfo.setEndTime(Utils.maxDate().getTime());
        }

        return revisionInfo;
    }

    @Override
    protected OfferRepository getEntityRepo() {
        return offerRepo;
    }

    @Override
    protected OfferRevisionRepository getRevisionRepo() {
        return offerRevisionRepo;
    }

    @Override
    protected String getRevisionType() {
        return "offer-revision";
    }

    @Override
    protected String getEntityType() {
        return "offer";
    }

    private void fillDefaultValue(Offer offer) {
        if (offer.getDeveloperRatio() == null) {
            offer.setDeveloperRatio(config.getDeveloperRatio());
        }
    }

    private List<Offer> getOffersByItemId(String itemId) {
        List<OfferRevision> offerRevisions = offerRevisionRepo.getRevisions(itemId);
        if (CollectionUtils.isEmpty(offerRevisions)) {
            return Collections.emptyList();
        }

        Set<String> tempOfferIds = filterOfferIds(offerRevisions);
        Set<String> resultOfferIds = new HashSet<>(tempOfferIds);
        for (String offerId : tempOfferIds) {
            findOfferIdsBySubOfferId(offerId, resultOfferIds);
        }

        return offerRepo.getOffers(resultOfferIds);
    }

    private Set<String> filterOfferIds(List<OfferRevision> revisions) {
        Set<String> offerIds = new HashSet<>();
        Set<String> revisionIds = new HashSet<>();
        for (OfferRevision offerRevision : revisions) {
            offerIds.add(offerRevision.getOfferId());
            revisionIds.add(offerRevision.getRevisionId());
        }

        List<OfferRevision> offerRevisions = offerRevisionRepo.getRevisions(offerIds, Utils.currentTimestamp());

        Set<String> resultOfferIds = new HashSet<>();
        for (OfferRevision revision : offerRevisions) {
            if (revisionIds.contains(revision.getRevisionId())) {
                resultOfferIds.add(revision.getOfferId());
            }
        }

        return resultOfferIds;
    }

    private void findOfferIdsBySubOfferId(String subOfferId, Set<String> offerIds) {
        List<OfferRevision> offerRevisions = offerRevisionRepo.getRevisionsBySubOfferId(subOfferId);
        if (CollectionUtils.isEmpty(offerRevisions)) {
            return;
        }

        Set<String> tempOfferIds = filterOfferIds(offerRevisions);

        for (String offerId : tempOfferIds) {
            if (!offerIds.contains(offerId)) {
                offerIds.add(offerId);
                findOfferIdsBySubOfferId(offerId, offerIds);
            }
        }
    }

    private void validateOfferCreation(Offer offer) {
        checkRequestNotNull(offer);
        List<AppError> errors = new ArrayList<>();
        if (offer.getOfferId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("self"));
        }
        if (offer.getRev() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("rev"));
        }
        if (Boolean.TRUE.equals(offer.getPublished())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("isPublished", offer.getPublished(), Boolean.FALSE));
        }
        if (offer.getCurrentRevisionId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("currentRevision"));
        }

        validateOfferCommon(offer, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating offer. ", exception);
            throw exception;
        }
    }

    private void validateOfferUpdate(Offer offer, Offer oldOffer) {
        checkRequestNotNull(offer);
        List<AppError> errors = new ArrayList<>();
        if (!oldOffer.getOfferId().equals(offer.getOfferId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("self.id", offer.getOfferId(), oldOffer.getOfferId()));
        }
        if (!isEqual(offer.getCurrentRevisionId(), oldOffer.getCurrentRevisionId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("currentRevision", offer.getCurrentRevisionId(), oldOffer.getCurrentRevisionId()));
        }
        if (!oldOffer.getRev().equals(offer.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", offer.getRev(), oldOffer.getRev()));
        }

        validateOfferCommon(offer, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer. ", exception);
            throw exception;
        }
    }

    private void validateOfferCommon(Offer offer, List<AppError> errors) {
        if (offer.getOwnerId()==null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("publisher"));
        }
        if (!CollectionUtils.isEmpty(offer.getCategories())) {
            for (String categoryId : offer.getCategories()) {
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

    private void validateRevisionCreation(OfferRevision revision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (revision.getRevisionId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("self"));
        }
        if (revision.getRev() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", revision.getRev(), null));
        }
        if (revision.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("publisher"));
        }
        if (!Status.DRAFT.is(revision.getStatus())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("status", "should be 'DRAFT'"));
        }
        if (revision.getOfferId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("offerId"));
        } else {
            Offer offer = offerRepo.get(revision.getOfferId());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.offerNotFound("offerId", revision.getOfferId()));
            }
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, OfferRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                OfferRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
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
            LOGGER.error("Error creating offer-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionUpdateBasic(OfferRevision revision, OfferRevision oldRevision) {
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
        if (revision.getOfferId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("offerId"));
        } else {
            Offer offer = offerRepo.get(revision.getOfferId());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.offerNotFound("offerId", revision.getOfferId()));
            }
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, OfferRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                OfferRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
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
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionUpdate(OfferRevision revision, OfferRevision oldRevision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getRev().equals(revision.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("rev", revision.getRev(), oldRevision.getRev()));
        }
        if (revision.getStatus()==null || !Status.contains(revision.getStatus())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("status", Joiner.on(", ").join(Status.ALL)));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
    }

    private void validateRevisionCommon(OfferRevision revision, List<AppError> errors) {
        if (revision.getOwnerId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("publisher"));
        }
        if (revision.getOfferId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("offerId"));
        } else {
            Offer offer = offerRepo.get(revision.getOfferId());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE.offerNotFound("offerId", revision.getOfferId()));
            }
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
        }
        if (revision.getPrice() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("price"));
        } else {
            checkPrice(revision.getPrice(), errors);
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, OfferRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                OfferRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale + ".name"));
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(revision.getItems())) {
            validateItems(revision.getItems(), errors);
        }
        // TODO: check other properties
    }

    private void validateItems(List<ItemEntry> items, List<AppError> errors) {
        boolean hasSVItem = false;
        for (ItemEntry itemEntry : items) {
            if (itemEntry.getItemId() == null) {
                errors.add(AppCommonErrors.INSTANCE.fieldRequired("items.item.id"));
            } else {
                Item item = itemRepo.get(itemEntry.getItemId());
                if (item == null) {
                    errors.add(AppCommonErrors.INSTANCE.resourceNotFound("item", itemEntry.getItemId()));
                } else {
                    if (item.getType().equals(ItemType.STORED_VALUE)) {
                        hasSVItem = true;
                    }
                    if (itemEntry.getQuantity() == null) {
                        itemEntry.setQuantity(1);
                    } else if (itemEntry.getQuantity() <= 0) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items",
                                "Quantity should be greater than 0 for item " + itemEntry.getItemId()));
                    } else if (itemEntry.getQuantity() > 1) {
                        if (!(ItemType.PHYSICAL.is(item.getType()))) {
                            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items",
                                    "'quantity' should be 1 for " + item.getType() + " item " + itemEntry.getItemId()));
                        }
                    }
                }
            }
        }
        if (hasSVItem && items.size() > 1) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "STORED_VALUE item is mutually exclusive with other items in an offer"));
        }
    }

    private List<Action> preparePurchaseEvent(OfferRevision revision) {
        if (revision.getEventActions()==null) {
            revision.setEventActions(new HashMap<String, List<Action>>());
        }
        List<Action> purchaseActions = revision.getEventActions().get(EventType.PURCHASE.name());
        if (purchaseActions == null) {
            purchaseActions = new ArrayList<>();
            revision.getEventActions().put(EventType.PURCHASE.name(), purchaseActions);
        }

        return purchaseActions;
    }

    private Map<String, Set<String>> adjustActions(List<ItemEntry> items, List<Action> purchaseActions) {
        Map<String, Set<String>> result = new HashMap<>();
        for (ItemEntry itemEntry : items) {
            result.put(itemEntry.getItemId(), new HashSet<String>());
        }

        Iterator<Action> iterator = purchaseActions.iterator();
        while(iterator.hasNext()) {
            Action action = iterator.next();
            if (action.getItemId() == null) {
                continue;
            }
            if (!result.containsKey(action.getItemId())) {
                iterator.remove();
                continue;
            }
            result.get(action.getItemId()).add(action.getType());
        }
        return result;
    }

    private void generateEventActions(OfferRevision revision) {
        List<Action> purchaseActions = preparePurchaseEvent(revision);
        Map<String, Set<String>> definedActions = adjustActions(revision.getItems(), purchaseActions);
        for (ItemEntry itemEntry : revision.getItems()) {
            Item item = itemRepo.get(itemEntry.getItemId());
            if ((ItemType.APP.is(item.getType())
                    || ItemType.DOWNLOADED_ADDITION.is(item.getType())
                    || ItemType.PERMANENT_UNLOCK.is(item.getType())
                    || ItemType.CONSUMABLE_UNLOCK.is(item.getType())
                    || ItemType.SUBSCRIPTION.is(item.getType())
                    ) && !definedActions.get(itemEntry.getItemId()).contains(ActionType.GRANT_ENTITLEMENT.name())) {
                Action action = new Action();
                action.setType(ActionType.GRANT_ENTITLEMENT.name());
                action.setItemId(itemEntry.getItemId());
                purchaseActions.add(action);
            } /*else if (ItemType.STORED_VALUE.is(item.getType())) {
                Action action = new Action();
                action.setAutoGenerated(true);
                action.setType(ActionType.CREDIT_WALLET.name());
                action.setItemId(itemEntry.getItemId());
                action.setStoredValueCurrency();
                purchaseActions.add(action);
            } */
            else if (ItemType.PHYSICAL.is(item.getType()) &&
                    !definedActions.get(itemEntry.getItemId()).contains(ActionType.DELIVER_PHYSICAL_GOODS.name())) {
                Action action = new Action();
                action.setType(ActionType.DELIVER_PHYSICAL_GOODS.name());
                action.setItemId(itemEntry.getItemId());
                purchaseActions.add(action);
            }
        }
    }
}
