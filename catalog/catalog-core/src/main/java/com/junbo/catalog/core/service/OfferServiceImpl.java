/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.EntityType;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.entity.ItemOfferRelationsEntity;
import com.junbo.catalog.db.repo.ItemOfferRelationsRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
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
    private ItemService itemService;
    @Autowired
    private ItemOfferRelationsRepository relationsRepo;

    @Override
    public Offer createEntity(Offer offer) {
        if (!StringUtils.isEmpty(offer.getRev())) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
        }
        if (Boolean.TRUE.equals(offer.getPublished())) {
            throw AppErrors.INSTANCE
                    .fieldNotCorrect("isPublished", "Cannot create an offer with isPublished true.").exception();
        }
        validateOffer(offer);
        return super.createEntity(offer);
    }

    @Override
    public Offer updateEntity(Long offerId, Offer offer) {
        validateId(offerId, offer.getOfferId());
        validateOffer(offer);
        return super.updateEntity(offerId, offer);
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
                throw AppErrors.INSTANCE.validation("offerId must be specified when timestamp is present.").exception();
            }

            return offerRevisionRepo.getRevisions(options.getOfferIds(), options.getTimestamp());
        } else {
            return offerRevisionRepo.getRevisions(options);
        }
    }

    @Override
    public OfferRevision createRevision(OfferRevision revision) {
        if (!StringUtils.isEmpty(revision.getRev())) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
        }
        if (!Status.DRAFT.equals(revision.getStatus())) {
            throw AppErrors.INSTANCE.fieldNotMatch("status", revision.getStatus(), Status.DRAFT).exception();
        }
        validateRevision(revision);
        generateEventActions(revision);
        return super.createRevision(revision);
    }

    @Override
    public OfferRevision updateRevision(Long revisionId, OfferRevision revision) {
        validateId(revisionId, revision.getRevisionId());
        validateRevision(revision);
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
                throw AppErrors.INSTANCE.notFound("offer-revision", lastRevisionId).exception();
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

    private void validateOffer(Offer offer) {
        checkFieldNotNull(offer.getOwnerId(), "publisher");
        if (offer.getCurrentRevisionId() != null) {
            OfferRevision revision = offerRevisionRepo.get(offer.getCurrentRevisionId());
            checkEntityNotNull(offer.getCurrentRevisionId(), revision, "offer-revision");
            if (!Status.APPROVED.equals(revision.getStatus())) {
                throw AppErrors.INSTANCE.validation("Cannot set current revision to unapproved revision").exception();
            }
        }
    }

    private void validateRevision(OfferRevision revision) {
        checkFieldNotNull(revision.getOwnerId(), "publisher");
        checkFieldNotNull(revision.getOfferId(), "offer");
        checkFieldNotNull(revision.getPrice(), "price");
        checkPrice(revision.getPrice());

        Offer offer = offerRepo.get(revision.getOfferId());
        checkEntityNotNull(revision.getOfferId(), offer, "offer");

        if (!Status.ALL_STATUSES.contains(revision.getStatus())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("status", "Valid statuses: " + Status.ALL_STATUSES).exception();
        }
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
            Item item = itemService.getEntity(itemEntry.getItemId());
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
