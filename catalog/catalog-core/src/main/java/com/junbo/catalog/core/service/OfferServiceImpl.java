/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.clientproxy.OrganizationFacade;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.core.validators.OfferRevisionValidator;
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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

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
    private OfferRevisionValidator revisionValidator;
    private OrganizationFacade organizationFacade;

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

    @Required
    public void setRevisionValidator(OfferRevisionValidator revisionValidator) {
        this.revisionValidator = revisionValidator;
    }

    @Required
    public void setOrganizationFacade(OrganizationFacade organizationFacade) {
        this.organizationFacade = organizationFacade;
    }

    @Override
    public Offer createEntity(Offer offer) {
        validateOfferCreation(offer);
        fillDefaultValue(offer);
        return offerRepo.create(offer);
    }

    @Override
    public Offer updateEntity(String offerId, Offer offer) {
        Offer oldOffer = getEntityRepo().get(offerId);
        checkEntityNotNull(offerId, offer, getEntityType());
        offer.setCurrentRevisionId(oldOffer.getCurrentRevisionId());
        validateOfferUpdate(offer, oldOffer);

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
        if (offer.getCurrentRevisionId() == null){
            offer.setCurrentRevisionId(getCurrentRevisionId(offer.getApprovedRevisions()));
        }
        return offer;
    }

    @Override
    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers;
        if (options.getItemId() != null) {
            offers = getOffersByItemId(options.getItemId(), options);
            options.setTotal(Long.valueOf(offers.size()));
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
    public OfferRevision getRevision(String revisionId) {
        OfferRevision revision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, revision, getRevisionType());
        setDefaultRankIfAbsent(revision);
        return revision;
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> revisions;
        if (options.getTimestamp()!=null) {
            if (CollectionUtils.isEmpty(options.getOfferIds())) {
                AppErrorException exception = AppCommonErrors.INSTANCE.parameterInvalid("offerId", "offerId must be specified when timeInMillis is present.").exception();
                LOGGER.error("Invalid request. ", exception);
                throw exception;
            }

            Set<String> offerIds = new HashSet<>();
            for (String offerId : options.getOfferIds()) {
                offerIds.add(offerId);
            }
            revisions = offerRevisionRepo.getRevisions(offerIds, options.getTimestamp());
        } else {
            revisions = offerRevisionRepo.getRevisions(options);
        }

        for (OfferRevision revision : revisions) {
            setDefaultRankIfAbsent(revision);
        }
        return revisions;
    }

    @Override
    public OfferRevision createRevision(OfferRevision revision) {
        revisionValidator.validateCreationBasic(revision);
        setDefaultRankIfAbsent(revision);
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

        // obsolete is the final status
        if (Status.OBSOLETE.is(oldRevision.getStatus())) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidOperation("Cannot update an obsolete revision").exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
            // approved status can only be changed to obsolete
        } else if (Status.APPROVED.is(oldRevision.getStatus()) && !Status.OBSOLETE.is(revision.getStatus())) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidOperation("Cannot update an approved revision").exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }

        if (Status.OBSOLETE.is(revision.getStatus())) {
            return obsoleteRevision(oldRevision, revision);
        }

        if (Status.APPROVED.is(revision.getStatus()) || Status.PENDING_REVIEW.is(revision.getStatus())) {
            revisionValidator.validateFull(revision, oldRevision);
            generateEventActions(revision);

            if (Status.APPROVED.is(revision.getStatus())) {
                Long timestamp = Utils.currentTimestamp();
                revision.setTimestamp(timestamp);
                if (revision.getStartTime() == null || revision.getStartTime().getTime() < timestamp) {
                    revision.setStartTime(new Date(timestamp));
                }

                updateOfferForApprovedRevision(revision, timestamp);
            }
        } else {
            revisionValidator.validateUpdateBasic(revision, oldRevision);
        }

        setDefaultRankIfAbsent(revision);
        return offerRevisionRepo.update(revision, oldRevision);
    }

    private OfferRevision obsoleteRevision(OfferRevision oldRevision, OfferRevision revision) {
        if (!Status.APPROVED.is(oldRevision.getStatus())) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidOperation("Cannot obsolete an non-approved revision").exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
        revisionValidator.validateFull(revision, oldRevision);
        revision = oldRevision;
        revision.setStatus(Status.OBSOLETE.name());
        Long timestamp = Utils.currentTimestamp();
        if (revision.getEndTime() == null || revision.getEndTime().getTime() > timestamp) {
            revision.setEndTime(new Date(timestamp));
            updateOfferForObsoleteRevision(revision, timestamp);
        }

        return offerRevisionRepo.update(revision, oldRevision);
    }

    private void updateOfferForObsoleteRevision(OfferRevision revision, Long timestamp) {
        Offer offer = getEntity(revision.getOfferId());
        if (revision.getRevisionId().equals(offer.getCurrentRevisionId())) {
            offer.setCurrentRevisionId(null);
            offer.setActiveRevision(null);
        }
        if (offer.getApprovedRevisions() != null) {
            RevisionInfo revisionInfo = offer.getApprovedRevisions().get(revision.getRevisionId());
            if (revisionInfo != null) {
                revisionInfo.setEndTime(timestamp);
            }
        }
        offerRepo.update(offer, offer);
    }

    private void updateOfferForApprovedRevision(OfferRevision revision, Long timestamp) {
        Offer offer = offerRepo.get(revision.getOfferId());
        //offer.setPublished(true);

        // revision has no endTime, so the currentRevisionId is determined and no need to resolve in every GET
        if (revision.getStartTime().getTime() <= timestamp
                && (revision.getEndTime() == null || revision.getEndTime().after(Utils.maxDate()))) {
            offer.setCurrentRevisionId(revision.getRevisionId());
            offer.setActiveRevision(revision);
            if (offer.getApprovedRevisions() != null) {
                offer.getApprovedRevisions().clear();
            }
        } else {
            // dynamic resolve currentRevision
            offer.setCurrentRevisionId(null);
        }
        // set activeRevision for index
        if (revision.getStartTime().getTime() <= timestamp
                && (revision.getEndTime() == null || revision.getEndTime().getTime() > timestamp)) {
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
        if (offer.getPublished() == null) {
            offer.setPublished(Boolean.FALSE);
        }
    }

    private List<Offer> getOffersByItemId(String itemId, OffersGetOptions options) {
        List<OfferRevision> offerRevisions = offerRevisionRepo.getRevisions(itemId);
        if (CollectionUtils.isEmpty(offerRevisions)) {
            return Collections.emptyList();
        }

        Set<String> tempOfferIds = filterOfferIds(offerRevisions);
        Set<String> resultOfferIds = new HashSet<>(tempOfferIds);
        for (String offerId : tempOfferIds) {
            findOfferIdsBySubOfferId(offerId, resultOfferIds);
        }
        options.setOfferIds(resultOfferIds);
        return offerRepo.getOffers(options);
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
        if (offer.getCurrentRevisionId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("currentRevision"));
        }
        if (offer.getOwnerId()==null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("publisher"));
        } else if (organizationFacade.getOrganization(offer.getOwnerId()) == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("publisher", "Cannot find organization " + Utils.encodeId(offer.getOwnerId())));
        }
        if (Boolean.TRUE.equals(offer.getPublished())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("isPublished", "The offer does not have currentRevision"));
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
        if (!oldOffer.getOwnerId().equals(offer.getOwnerId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("publisher", Utils.encodeId(offer.getOwnerId()), Utils.encodeId(oldOffer.getOwnerId())));
        } else if (organizationFacade.getOrganization(offer.getOwnerId()) == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("publisher", "Cannot find organization " + Utils.encodeId(offer.getOwnerId())));
        }
        if (Boolean.TRUE.equals(offer.getPublished()) && !Boolean.TRUE.equals(oldOffer.getPublished()) && oldOffer.getCurrentRevisionId() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("isPublished", "The offer does not have currentRevision"));
        }

        validateOfferCommon(offer, errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer. ", exception);
            throw exception;
        }
    }

    private void validateOfferCommon(Offer offer, List<AppError> errors) {
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
                    || ItemType.VIDEO.is(item.getType())
                    || ItemType.PHOTO.is(item.getType())
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

    private void setDefaultRankIfAbsent(OfferRevision revision) {
        if (revision.getRank()==null) {
            revision.setRank(0d);
        }
    }
}
