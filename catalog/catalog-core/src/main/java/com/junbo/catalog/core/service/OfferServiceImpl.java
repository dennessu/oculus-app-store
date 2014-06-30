/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

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
import com.junbo.common.error.AppError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseRevisionedServiceImpl<Offer, OfferRevision> implements OfferService {
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
            throw AppErrors.INSTANCE.notFound("offer", Utils.encodeId(offerId)).exception();
        }
        validateOfferUpdate(offer, oldOffer);

        offer.setCurrentRevisionId(oldOffer.getCurrentRevisionId());
        offer.setApprovedRevisions(oldOffer.getApprovedRevisions());
        offer.setActiveRevision(oldOffer.getActiveRevision());
        fillDefaultValue(offer);
        Offer updatedOffer = offerRepo.update(offer);
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
                throw AppErrors.INSTANCE
                        .validation("offerId must be specified when timeInMillis is present.").exception();
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
            throw AppErrors.INSTANCE.notFound("offer-revision", Utils.encodeId(revisionId)).exception();
        }
        if (Status.APPROVED.is(oldRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot update an approved revision").exception();
        }
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

        return offerRevisionRepo.update(revision);
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

        offerRepo.update(offer);
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
        if (offer.getRev() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("rev"));
        }
        if (Boolean.TRUE.equals(offer.getPublished())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("isPublished", "isPublished should be false."));
        }
        if (offer.getCurrentRevisionId() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("currentRevision"));
        }

        validateOfferCommon(offer, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateOfferUpdate(Offer offer, Offer oldOffer) {
        checkRequestNotNull(offer);
        List<AppError> errors = new ArrayList<>();
        if (!oldOffer.getOfferId().equals(offer.getOfferId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", offer.getOfferId(), oldOffer.getOfferId()));
        }
        /*if (!isEqual(offer.getCurrentRevisionId(), oldOffer.getCurrentRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotCorrect("currentRevision", "The field can only be changed through revision approve"));
        }*/
        if (!oldOffer.getRev().equals(offer.getRev())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", offer.getRev(), oldOffer.getRev()));
        }

        validateOfferCommon(offer, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateOfferCommon(Offer offer, List<AppError> errors) {
        if (offer.getOwnerId()==null) {
            errors.add(AppErrors.INSTANCE.missingField("publisher"));
        }
        if (!CollectionUtils.isEmpty(offer.getCategories())) {
            for (String categoryId : offer.getCategories()) {
                if (categoryId == null) {
                    errors.add(AppErrors.INSTANCE.fieldNotCorrect("categories", "should not contain null"));
                } else {
                    OfferAttribute attribute = offerAttributeRepo.get(categoryId);
                    if (attribute == null || !OfferAttributeType.CATEGORY.is(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE
                                .fieldNotCorrect("categories", "Cannot find category " + Utils.encodeId(categoryId)));
                    }
                }
            }
        }
    }

    private void validateRevisionCreation(OfferRevision revision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (revision.getRev() != null) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", revision.getRev(), null));
        }
        if (!Status.DRAFT.is(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("status", revision.getStatus(), Status.DRAFT));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateRevisionUpdate(OfferRevision revision, OfferRevision oldRevision) {
        checkRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        if (!oldRevision.getRevisionId().equals(revision.getRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("revisionId", revision.getRevisionId(), oldRevision.getRevisionId()));
        }
        if (!oldRevision.getRev().equals(revision.getRev())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("rev", revision.getRev(), oldRevision.getRev()));
        }
        if (revision.getStatus()==null || !Status.contains(revision.getStatus())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("status", "Valid statuses: " + Status.ALL));
        }

        validateRevisionCommon(revision, errors);

        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateRevisionCommon(OfferRevision revision, List<AppError> errors) {
        if (revision.getOwnerId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("publisher"));
        }
        if (revision.getOfferId() == null) {
            errors.add(AppErrors.INSTANCE.missingField("offerId"));
        } else {
            Offer offer = offerRepo.get(revision.getOfferId());
            if (offer == null) {
                errors.add(AppErrors.INSTANCE
                        .fieldNotCorrect("offerId", "Cannot find offer " + Utils.encodeId(revision.getOfferId())));
            }
        }
        if (revision.getPrice() == null) {
            errors.add(AppErrors.INSTANCE.missingField("price"));
        } else {
            checkPrice(revision.getPrice(), errors);
        }
        if (CollectionUtils.isEmpty(revision.getLocales())) {
            errors.add(AppErrors.INSTANCE.missingField("locales"));
        } else {
            for (Map.Entry<String, OfferRevisionLocaleProperties> entry : revision.getLocales().entrySet()) {
                String locale = entry.getKey();
                OfferRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
                    errors.add(AppErrors.INSTANCE.missingField("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppErrors.INSTANCE.missingField("locales." + locale + ".name"));
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
                errors.add(AppErrors.INSTANCE.fieldNotCorrect("items.item.id", "should not be null"));
            } else {
                Item item = itemRepo.get(itemEntry.getItemId());
                if (item == null) {
                    errors.add(AppErrors.INSTANCE.notFound("item", Utils.encodeId(itemEntry.getItemId())));
                } else {
                    if (item.getType().equals(ItemType.STORED_VALUE)) {
                        hasSVItem = true;
                    }
                    if (itemEntry.getQuantity() == null) {
                        itemEntry.setQuantity(1);
                    } else if (itemEntry.getQuantity() <= 0) {
                        errors.add(AppErrors.INSTANCE.fieldNotCorrect("items",
                                "Quantity should be greater than 0 for item " + Utils.encodeId(itemEntry.getItemId())));
                    } else if (itemEntry.getQuantity() > 1) {
                        if (!(ItemType.VIRTUAL.is(item.getType()) || ItemType.PHYSICAL.is(item.getType()))) {
                            errors.add(AppErrors.INSTANCE.fieldNotCorrect("items",
                                    "'quantity' should be 1 for " + item.getType()
                                            + " item " + Utils.encodeId(itemEntry.getItemId())));
                        }
                    }
                }
            }
        }
        if (hasSVItem && items.size() > 1) {
            errors.add(AppErrors.INSTANCE
                    .validation("STORED_VALUE item is mutually exclusive with other items in an offer"));
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
            if (ActionType.CREDIT_WALLET.is(action.getType()) || action.getItemId() == null) {
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
            if ((ItemType.DIGITAL.is(item.getType())
                    || ItemType.APP.is(item.getType())
                    || ItemType.DOWNLOADED_ADDITION.is(item.getType())
                    || ItemType.IN_APP_UNLOCK.is(item.getType())
                    || ItemType.IN_APP_CONSUMABLE.is(item.getType())
                    || ItemType.SUBSCRIPTION.is(item.getType())
                    || ItemType.VIRTUAL.is(item.getType())
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
