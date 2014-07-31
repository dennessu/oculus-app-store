/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.validators;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.enums.DistributionChannel;
import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OfferRevisionValidator.
 */
public class OfferRevisionValidator extends ValidationSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationSupport.class);
    private OfferRepository offerRepo;
    private ItemRepository itemRepo;
    private static final Map<String, String> PRODUCT_CODE_MAP = new HashMap<>();
    static {
        PRODUCT_CODE_MAP.put(ItemType.PHYSICAL.name(), "PHYSICAL_GOODS");
        PRODUCT_CODE_MAP.put(ItemType.APP.name(), "DOWNLOADABLE_SOFTWARE");
        PRODUCT_CODE_MAP.put(ItemType.DOWNLOADED_ADDITION.name(), "DOWNLOADABLE_SOFTWARE");
        PRODUCT_CODE_MAP.put(ItemType.PERMANENT_UNLOCK.name(), "DIGITAL_CONTENT");
        PRODUCT_CODE_MAP.put(ItemType.CONSUMABLE_UNLOCK.name(), "DIGITAL_CONTENT");
        PRODUCT_CODE_MAP.put(ItemType.STORED_VALUE.name(), "GIFT_CARD");
        PRODUCT_CODE_MAP.put(ItemType.SUBSCRIPTION.name(), "SUBSCRIPTION");
    }

    @Required
    public void setOfferRepo(OfferRepository offerRepo) {
        this.offerRepo = offerRepo;
    }

    @Required
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    public void validateFull(OfferRevision revision, OfferRevision oldRevision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateNotWritable("self", revision.getRevisionId(), oldRevision.getRevisionId(), errors);
        validateNotWritable("rev", revision.getRev(), oldRevision.getRev(), errors);
        validateStatus(revision.getStatus(), errors);
        validateFieldNotNull("publisher", revision.getOwnerId(), errors);
        validateOffer(revision, errors);
        validateDistributedChannels(revision.getDistributionChannels(), errors);
        if(validateFieldNotNull("price", revision.getPrice(), errors)) {
            validatePrice(revision.getPrice(), errors);
        }
        validateLocales(revision.getLocales(), errors);
        validateSubOffers(revision, errors);
        validateItems(revision, errors);
        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
    }

    public void validateCreationBasic(OfferRevision revision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateFieldNull("self", revision.getRevisionId(), errors);
        validateFieldNull("rev", revision.getRev(), errors);
        validateFieldNotNull("publisher", revision.getOwnerId(), errors);
        validateFieldNull("createdTime", revision.getCreatedTime(), errors);
        validateFieldNull("updatedTime", revision.getUpdatedTime(), errors);
        validateFieldMatch("status", revision.getStatus(), Status.DRAFT.name(), errors);
        validateOffer(revision, errors);
        validateLocales(revision.getLocales(), errors);
        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);
        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating offer-revision. ", exception);
            throw exception;
        }
    }

    public void validateUpdateBasic(OfferRevision revision, OfferRevision oldRevision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateNotWritable("self", revision.getRevisionId(), oldRevision.getRevisionId(), errors);
        validateNotWritable("rev", revision.getRev(), oldRevision.getRev(), errors);
        validateStatus(revision.getStatus(), errors);
        validateFieldNotNull("publisher", revision.getOwnerId(), errors);
        validateOffer(revision, errors);
        validateLocales(revision.getLocales(), errors);

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
    }

    private void validateOffer(OfferRevision revision, List<AppError> errors) {
        if (validateFieldNotNull("offer", revision.getOfferId(), errors)) {
            Offer offer = offerRepo.get(revision.getOfferId());
            if (validateResourceExists("offer", revision.getOfferId(), offer, errors)) {
                if (revision.getOwnerId() != null && !revision.getOwnerId().equals(offer.getOwnerId())) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalid("offer", "offer should have same owner as offer-revision"));
                }
            }
        }
    }

    private void validateLocales(Map<String, OfferRevisionLocaleProperties> locales, List<AppError> errors) {
        if (validateMapNotEmpty("locales", locales, errors)) {
            for (Map.Entry<String, OfferRevisionLocaleProperties> entry : locales.entrySet()) {
                String locale = entry.getKey();
                OfferRevisionLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (validateFieldNotNull("locales." + locale, properties, errors)) {
                    validateStringNotEmpty("locales." + locale + ".name", properties.getName(), errors);
                }
            }
        }
    }


    private boolean validateDistributedChannels(List<String> distributedChannels, List<AppError> errors) {
        if (distributedChannels != null) {
            for (String channel : distributedChannels) {
                if (channel==null || !DistributionChannel.contains(channel)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("distributedChannel", Joiner.on(',').join(distributedChannels)));
                    return false;
                }
            }
            return true;
        } else {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("distributionChannel"));
            return false;
        }
    }

    private void validateSubOffers(OfferRevision revision, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(revision.getSubOffers())) {
            for (String subOfferId : revision.getSubOffers()) {
                Offer subOffer = offerRepo.get(subOfferId);
                if (validateResourceExists("offer", subOfferId, subOffer, errors)) {
                    if (revision.getOwnerId() != null && !revision.getOwnerId().equals(subOffer.getOwnerId())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("subOffers", "offer should only contains sub-offers of same owner"));
                    }
                    if (!Boolean.TRUE.equals(subOffer.getPublished())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("subOffers", "sub-offers should be published"));
                    }
                }
            }
        }
    }

    private void validateItems(OfferRevision revision, List<AppError> errors) {
        if (CollectionUtils.isEmpty(revision.getItems())) {
            return;
        }
        boolean hasSVItem = false;
        for (ItemEntry itemEntry : revision.getItems()) {
            if (validateFieldNotNull("items.item.id", itemEntry.getItemId(), errors)) {
                Item item = itemRepo.get(itemEntry.getItemId());
                if (validateResourceExists("item", itemEntry.getItemId(), item, errors)) {
                    if (revision.getOwnerId() != null && !revision.getOwnerId().equals(item.getOwnerId())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "offer should only contains items of same owner"));
                    }
                    if (ItemType.STORED_VALUE.is(item.getType())) {
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
            if (hasSVItem && revision.getItems().size() > 1) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "STORED_VALUE item is mutually exclusive with other items in an offer"));
            }
        }
    }
}
