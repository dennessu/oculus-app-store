/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.validators;

import com.google.common.base.Joiner;
import com.junbo.catalog.clientproxy.OrganizationFacade;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.enums.*;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
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
    private OrganizationFacade organizationFacade;
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

    @Required
    public void setOrganizationFacade(OrganizationFacade organizationFacade) {
        this.organizationFacade = organizationFacade;
    }

    public void validateFull(OfferRevision revision, OfferRevision oldRevision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateNotWritable("self", revision.getRevisionId(), oldRevision.getRevisionId(), errors);
        validateNotWritable("rev", revision.getRev(), oldRevision.getRev(), errors);
        validateStatus(revision.getStatus(), errors);
        validateNotWritable("publisher", Utils.encodeId(revision.getOwnerId()), Utils.encodeId(oldRevision.getOwnerId()), errors);
        validateOffer(revision, errors);
        validateDistributedChannels(revision.getDistributionChannels(), errors);
        if(validateFieldNotNull("price", revision.getPrice(), errors)) {
            validatePrice(revision.getPrice(), errors);
        }
        validatePrice(revision.getPreOrderPrice(), errors);
        validateLocales(revision.getLocales(), errors);
        validateSubOffers(revision, errors);
        validateItems(revision, errors);
        validateCountries("regions", revision.getCountries(), revision.getPrice(), errors);
        validateEventAction(revision, errors);
        if (Status.APPROVED.is(revision.getStatus()) || Status.PENDING_REVIEW.is(revision.getStatus())) {
            if (revision.getStartTime() != null && revision.getEndTime() != null && revision.getStartTime().after(revision.getEndTime())) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("endTime", "endTime should be after startTime"));
            }
        }

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (revision.getRank() != null && revision.getRank() < 0) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("rank", "Should not be negative."));
        }

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
        if(validateFieldNotNull("publisher", revision.getOwnerId(), errors)) {
            if (organizationFacade.getOrganization(revision.getOwnerId()) == null) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("publisher", "Cannot find organization " + Utils.encodeId(revision.getOwnerId())));
            }
        }
        validateFieldNull("createdTime", revision.getCreatedTime(), errors);
        validateFieldNull("updatedTime", revision.getUpdatedTime(), errors);
        validateFieldMatch("status", revision.getStatus(), Status.DRAFT.name(), errors);
        validateOffer(revision, errors);
        validateLocales(revision.getLocales(), errors);
        validateEventAction(revision, errors);
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
        if (validateNotWritable("publisher", Utils.encodeId(revision.getOwnerId()), Utils.encodeId(oldRevision.getOwnerId()), errors)) {
            if (organizationFacade.getOrganization(revision.getOwnerId()) == null) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("publisher", "Cannot find organization " + Utils.encodeId(revision.getOwnerId())));
            }
        }
        validateOffer(revision, errors);
        validateLocales(revision.getLocales(), errors);
        validateEventAction(revision, errors);

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating offer-revision. ", exception);
            throw exception;
        }
    }

    protected void validateCountries(String fieldName, Map<String, CountryProperties> countries, Price price, List<AppError> errors) {
        if (countries == null) {
            return;
        }
        for (String countryCode : countries.keySet()) {
            if (!COUNTRY_CODES.contains(countryCode)) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, countryCode + " is not a valid country code"));
            }
            if (countries.get(countryCode) == null) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, "regions " + countryCode + " should not be null"));
                continue;
            }
            if (countries.get(countryCode).getIsPurchasable() == null) {
                countries.get(countryCode).setIsPurchasable(Boolean.FALSE);
            }
            if (countries.get(countryCode).getIsPurchasable() != Boolean.TRUE || price == null
                    || !PriceType.CUSTOM.is(price.getPriceType()) || price.getPrices() == null) {
                continue;
            }
            if (price != null && price.getPrices() != null && !price.getPrices().containsKey(countryCode)) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("regions",
                        "offer is purchasable in " + countryCode + ", but no price defined for that country."));
            }
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
                validateLocale("locales", locale, errors);
                if (validateFieldNotNull("locales." + locale, properties, errors)) {
                    validateStringNotEmpty("locales." + locale + ".name", properties.getName(), errors);
                }

                validateImages(properties.getImages(), errors);
                if (properties.getItems() != null) {
                    for (String itemId : properties.getItems().keySet()) {
                        ItemRevisionLocaleProperties itemProperties = properties.getItems().get(itemId);
                        validateOptionalEmail("supportEmail", itemProperties.getSupportEmail(), errors);
                        validateOptionalUrl("communityForumLink", itemProperties.getCommunityForumLink(), errors);
                        validateOptionalUrl("manualDocument", itemProperties.getManualDocument(), errors);
                        validateOptionalUrl("website", itemProperties.getWebsite(), errors);
                        validateImages(itemProperties.getImages(), errors);
                    }
                }
            }
        }
    }

    private boolean validateDistributedChannels(List<String> distributedChannels, List<AppError> errors) {
        if (distributedChannels != null) {
            for (String channel : distributedChannels) {
                if (channel==null || !DistributionChannel.contains(channel)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("distributionChannel", Joiner.on(',').join(distributedChannels)));
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
        String svItemId = null;
        for (ItemEntry itemEntry : revision.getItems()) {
            if (validateFieldNotNull("items.item.id", itemEntry.getItemId(), errors)) {
                Item item = itemRepo.get(itemEntry.getItemId());
                if (validateResourceExists("item", itemEntry.getItemId(), item, errors)) {
                    /*if (revision.getOwnerId() != null && !revision.getOwnerId().equals(item.getOwnerId())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "offer should only contains items of same owner"));
                    }*/
                    if (Status.APPROVED.is(revision.getStatus()) && item.getCurrentRevisionId() == null){
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "Item " + item.getItemId() + " does not have an approved revision"));
                    }
                    if (ItemType.STORED_VALUE.is(item.getType())) {
                        svItemId = item.getItemId();
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
            if (svItemId != null && revision.getItems().size() > 1) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid("items", "STORED_VALUE item is mutually exclusive with other items in an offer"));
            }
            validateWalletAction(svItemId, revision.getEventActions(), errors);
        }
    }

    private void validateWalletAction(String svItemId, Map<String, List<Action>> eventActions, List<AppError> errors) {
        if (svItemId == null) {
            return;
        }
        if (!validateMapNotEmpty("eventActions", eventActions, errors)) {
            return;
        }
        List<Action> purchaseActions = eventActions.get(EventType.PURCHASE.name());
        if (validateCollectionNotEmpty("eventActions.PURCHASE", purchaseActions, errors)) {
            for (Action action : purchaseActions) {
                if (ActionType.CREDIT_WALLET.is(action.getType())) {
                    validateFieldMatch("eventActions.PURCHASE.itemId.id", action.getItemId(), svItemId, errors);
                    validateStringNotEmpty("eventActions.PURCHASE.storedValueCurrency", action.getStoredValueCurrency(), errors);
                    if (action.getStoredValueAmount()==null || action.getStoredValueAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("eventActions.PURCHASE.storedValueAmount", "should be a positive value"));
                    }
                    return;
                }
            }
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("eventActions.PURCHASE", "CREDIT_WALLET action should be configured"));
        }
    }

    private void validateEventAction(OfferRevision revision, List<AppError> errors) {
        if (revision.getEventActions() == null) {
            return;
        }
        for (String event : revision.getEventActions().keySet()) {
            if (event == null || !EventType.contains(event)) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("eventActions[keys]", Joiner.on(",").join(EventType.values())));
                continue;
            }
            if (revision.getEventActions().get(event) == null) {
                continue;
            }
            for (Action action : revision.getEventActions().get(event)) {
                if (action == null) {
                    continue;
                }
                if (action.getType() == null || !ActionType.contains(action.getType())) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("eventActions." + event + ".type", Joiner.on(",").join(ActionType.values())));
                } else if (ActionType.GRANT_ENTITLEMENT.is(action.getType()) || ActionType.DELIVER_PHYSICAL_GOODS.is(action.getType())) {
                    if (action.getItemId()==null) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("eventActions." + event +".itemId", "cannot be null"));
                    }
                }
            }
        }
    }
}
