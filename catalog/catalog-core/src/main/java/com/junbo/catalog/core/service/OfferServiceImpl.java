/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.EntityType;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.entity.ItemOfferRelationsEntity;
import com.junbo.catalog.db.repo.*;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.error.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseRevisionedServiceImpl<Offer, OfferRevision> implements OfferService {
    @Autowired
    private OfferRepository offerRepo;
    @Autowired
    private OfferRevisionRepository offerRevisionRepo;
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private ItemOfferRelationsRepository relationsRepo;
    @Autowired
    private OfferAttributeRepository offerAttributeRepo;

    @Override
    public Offer createEntity(Offer offer) {
        validateOfferCreation(offer);
        Long offerId = offerRepo.create(offer);
        return offerRepo.get(offerId);
    }

    @Override
    public Offer updateEntity(Long offerId, Offer offer) {
        Offer oldOffer = offerRepo.get(offerId);
        if (oldOffer == null) {
            throw AppErrors.INSTANCE.notFound("offer", Utils.encodeId(offerId)).exception();
        }
        validateOfferUpdate(offer, oldOffer);
        offerRepo.update(offer);
        return offerRepo.get(offerId);
    }

    @Override
    public List<Offer> getOffers(OffersGetOptions options) {
        if (options.getItemId() != null) {
            return getOffersByItemId(options.getItemId().getValue());
        } else {
            return offerRepo.getOffers(options);
        }
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        if (options.getTimestamp()!=null) {
            if (CollectionUtils.isEmpty(options.getOfferIds())) {
                throw AppErrors.INSTANCE
                        .validation("offerId must be specified when timeInMillis is present.").exception();
            }

            return offerRevisionRepo.getRevisions(options.getOfferIds(), options.getTimestamp());
        } else {
            return offerRevisionRepo.getRevisions(options);
        }
    }

    @Override
    public OfferRevision createRevision(OfferRevision revision) {
        validateRevisionCreation(revision);
        generateEventActions(revision);
        Long revisionId = offerRevisionRepo.create(revision);
        return offerRevisionRepo.get(revisionId);
    }

    @Override
    public OfferRevision updateRevision(Long revisionId, OfferRevision revision) {
        OfferRevision oldRevision = offerRevisionRepo.get(revisionId);
        if (oldRevision==null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", Utils.encodeId(revisionId)).exception();
        }
        if (Status.APPROVED.equals(oldRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot update an approved revision").exception();
        }
        validateRevisionUpdate(revision, oldRevision);
        generateEventActions(revision);

        return super.updateRevision(revisionId, revision);
    }

    @Override
    protected void postApproveActions(OfferRevision currentRevision, Long lastRevisionId) {
        List<Long> subOffers = safeWrap(currentRevision.getSubOffers());
        List<Long> items = convert(currentRevision.getItems());
        if (lastRevisionId!=null) {
            OfferRevision lastRevision = offerRevisionRepo.get(lastRevisionId);
            if (lastRevision == null) {
                throw AppErrors.INSTANCE.notFound("offer-revision", Utils.encodeId(lastRevisionId)).exception();
            }
            List<Long> lastSubOffers = safeWrap(lastRevision.getSubOffers());
            List<Long> lastItems = convert(lastRevision.getItems());

            createRelations(EntityType.OFFER, subtract(subOffers, lastSubOffers), currentRevision.getOfferId());
            createRelations(EntityType.ITEM, subtract(items, lastItems), currentRevision.getOfferId());
            removeRelations(EntityType.OFFER, subtract(lastSubOffers, subOffers), currentRevision.getOfferId());
            removeRelations(EntityType.ITEM, subtract(lastItems, items), currentRevision.getOfferId());
        } else {
            createRelations(EntityType.OFFER, subOffers, currentRevision.getOfferId());
            createRelations(EntityType.ITEM, items, currentRevision.getOfferId());
        }
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

    private List<Offer> getOffersByItemId(Long itemId) {
        Set<Long> offerIds = new HashSet<>();
        List<ItemOfferRelationsEntity> relationsEntities = relationsRepo.getRelations(itemId, EntityType.ITEM);
        if (CollectionUtils.isEmpty(relationsEntities)) {
            return Collections.emptyList();
        }
        for (ItemOfferRelationsEntity relationsEntity : relationsEntities) {
            offerIds.add(relationsEntity.getParentOfferId());
            getOfferIds(relationsEntity.getParentOfferId(), offerIds);
        }

        return offerRepo.getOffers(offerIds);
    }

    private void getOfferIds(Long offerId, Set<Long> offerIds) {
        List<ItemOfferRelationsEntity> relationsEntities = relationsRepo.getRelations(offerId, EntityType.OFFER);
        if (CollectionUtils.isEmpty(relationsEntities)) {
            return;
        }

        for (ItemOfferRelationsEntity relationsEntity : relationsEntities) {
            if (offerIds.contains(relationsEntity.getParentOfferId())) {
                continue;
            }
            offerIds.add(relationsEntity.getParentOfferId());
            getOfferIds(relationsEntity.getParentOfferId(), offerIds);
        }
    }

    private List<Long> convert(List<ItemEntry> itemEntries) {
        List<Long> items = new ArrayList<>();
        if (itemEntries != null) {
            for (ItemEntry itemEntry : itemEntries) {
                items.add(itemEntry.getItemId());
            }
        }

        return items;
    }

    private List<Long> safeWrap(List<Long> list) {
        if (list != null) {
            return new ArrayList<>(list);
        }
        return Collections.emptyList();
    }

    private void removeRelations(EntityType entityType, List<Long> entityIds, Long parentOfferId) {
        if (!CollectionUtils.isEmpty(entityIds)) {
            for (Long entityId : entityIds) {
                relationsRepo.delete(entityType, entityId, parentOfferId);
            }
        }
    }

    private void createRelations(EntityType entityType, List<Long> entityIds, Long parentOfferId) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return;
        }
        for (Long entityId : entityIds) {
            ItemOfferRelationsEntity relationsEntity = new ItemOfferRelationsEntity();
            relationsEntity.setEntityType(entityType.getValue());
            relationsEntity.setEntityId(entityId);
            relationsEntity.setParentOfferId(parentOfferId);
            relationsRepo.create(relationsEntity);
        }
    }

    private void validateOfferCreation(Offer offer) {
        List<AppError> errors = new ArrayList<>();
        if (!StringUtils.isEmpty(offer.getRev())) {
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
        List<AppError> errors = new ArrayList<>();
        if (!oldOffer.getOfferId().equals(offer.getOfferId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", offer.getOfferId(), oldOffer.getOfferId()));
        }
        if (!isEqual(offer.getCurrentRevisionId(), oldOffer.getCurrentRevisionId())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotCorrect("currentRevision", "The field can only be changed through revision approve"));
        }
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
        if (offer.getIapItemId() != null) {
            Item item = itemRepo.get(offer.getIapItemId());
            if (item == null) {
                errors.add(AppErrors.INSTANCE.fieldNotCorrect("soldWithinItem", "Item not found"));
            }
        }
        if (!CollectionUtils.isEmpty(offer.getCategories())) {
            for (Long categoryId : offer.getCategories()) {
                if (categoryId == null) {
                    errors.add(AppErrors.INSTANCE.fieldNotCorrect("categories", "should not contain null"));
                } else {
                    OfferAttribute attribute = offerAttributeRepo.get(categoryId);
                    if (attribute == null || !"CATEGORY".equals(attribute.getType())) {
                        errors.add(AppErrors.INSTANCE
                                .fieldNotCorrect("categories", "Cannot find category " + Utils.encodeId(categoryId)));
                    }
                }
            }
        }
    }

    private void validateRevisionCreation(OfferRevision revision) {
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

    private void validateRevisionUpdate(OfferRevision revision, OfferRevision oldRevision) {
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
        // TODO: check other properties
    }

    private List<Long> subtract(List<Long> list1, List<Long> list2) {
        List<Long> result = new ArrayList<>(list1);
        result.removeAll(list2);
        return result;
    }

    private List<Action> preparePurchaseEvent(OfferRevision revision) {
        if (revision.getEventActions()==null) {
            revision.setEventActions(new HashMap<String, List<Action>>());
        }
        List<Action> purchaseActions = revision.getEventActions().get(EventType.PURCHASE);
        if (purchaseActions == null) {
            purchaseActions = new ArrayList<>();
            revision.getEventActions().put(EventType.PURCHASE, purchaseActions);
        }

        removeAutoGeneratedActions(purchaseActions);

        return purchaseActions;
    }

    private void removeAutoGeneratedActions(List<Action> purchaseActions) {
        List<Action> autoGeneratedActions = new ArrayList<>();
        for (Action action : purchaseActions) {
            if (Boolean.TRUE.equals(action.isAutoGenerated())) {
                autoGeneratedActions.add(action);
            }
        }
        purchaseActions.removeAll(autoGeneratedActions);
    }

    private void generateEventActions(OfferRevision revision) {
        List<Action> purchaseActions = preparePurchaseEvent(revision);

        for (ItemEntry itemEntry : revision.getItems()) {
            Item item = itemRepo.get(itemEntry.getItemId());
            checkEntityNotNull(itemEntry.getItemId(), item, "item");
            if (ItemType.DIGITAL.equals(item.getType()) || ItemType.SUBSCRIPTION.equals(item.getType())) {
                Action action = new Action();
                action.setAutoGenerated(true);
                action.setType(ActionType.GRANT_ENTITLEMENT);
                action.setEntitlementDefId(item.getEntitlementDefId());
                purchaseActions.add(action);
            } else if (ItemType.WALLET.equals(item.getType())) {
                Action action = new Action();
                action.setAutoGenerated(true);
                action.setType(ActionType.CREDIT_WALLET);
                action.setItemId(itemEntry.getItemId());
                purchaseActions.add(action);
            } else if (ItemType.PHYSICAL.equals(item.getType())) {
                Action action = new Action();
                action.setAutoGenerated(true);
                action.setType(ActionType.DELIVER_PHYSICAL_GOODS);
                action.setItemId(itemEntry.getItemId());
                purchaseActions.add(action);
            }
        }
    }
}
