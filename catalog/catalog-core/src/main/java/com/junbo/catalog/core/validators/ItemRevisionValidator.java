/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.validators;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.spec.enums.*;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ItemRevisionValidator.
 */
public class ItemRevisionValidator extends ValidationSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationSupport.class);
    private ItemRepository itemRepo;
    private ItemRevisionRepository itemRevisionRepo;
    private static final List<String> ALL_STATUS = Arrays.asList("DRAFT", "PENDING_REVIEW", "APPROVED", "REJECTED");

    @Required
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Required
    public void setItemRevisionRepo(ItemRevisionRepository itemRevisionRepo) {
        this.itemRevisionRepo = itemRevisionRepo;
    }

    public void validateFull(ItemRevision revision, ItemRevision oldRevision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateNotWritable("self", revision.getRevisionId(), oldRevision.getRevisionId(), errors);
        validateNotWritable("rev", revision.getRev(), oldRevision.getRev(), errors);
        validateStatus(revision.getStatus(), errors);
        validateNotWritable("developer", Utils.encodeId(revision.getOwnerId()), Utils.encodeId(oldRevision.getOwnerId()), errors);
        validateItem(revision, errors);
        validateChannelsAndHostItems(revision.getDistributionChannels(), revision.getIapHostItemIds(), errors);
        if(validateFieldNotNull("msrp", revision.getMsrp(), errors)) {
            validatePrice(revision.getMsrp(), errors);
        }
        validatePlatforms(revision.getPlatforms(), errors);
        validateUserInteractionModes(revision.getUserInteractionModes(), errors);
        validateSupportedInputDevices(revision.getSupportedInputDevices(), errors);
        validatePackageName(revision.getPackageName(), revision.getItemId(), errors);
        validateLocales(revision.getLocales(), errors);
        validateCountryCodes("countries", revision.getCountries().keySet(), errors);

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
    }

    public void validateCreationBasic(ItemRevision revision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateFieldNull("self", revision.getRevisionId(), errors);
        validateFieldNull("rev", revision.getRev(), errors);
        validateFieldNull("createdTime", revision.getCreatedTime(), errors);
        validateFieldNull("updatedTime", revision.getUpdatedTime(), errors);
        validateFieldNull("localeAccuracy", revision.getLocaleAccuracy(), errors);
        validateFieldNotNull("developer", revision.getOwnerId(), errors);
        validateFieldMatch("status", revision.getStatus(), Status.DRAFT.name(), errors);
        validateItem(revision, errors);

        validatePlatforms(revision.getPlatforms(), errors);
        validateUserInteractionModes(revision.getUserInteractionModes(), errors);
        validateSupportedInputDevices(revision.getSupportedInputDevices(), errors);
        validatePackageName(revision.getPackageName(), revision.getItemId(), errors);
        validateLocales(revision.getLocales(), errors);

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating item-revision. ", exception);
            throw exception;
        }
    }

    public void validateUpdateBasic(ItemRevision revision, ItemRevision oldRevision) {
        validateRequestNotNull(revision);
        List<AppError> errors = new ArrayList<>();
        validateNotWritable("self", revision.getRevisionId(), oldRevision.getRevisionId(), errors);
        validateNotWritable("rev", revision.getRev(), oldRevision.getRev(), errors);
        validateStatus(revision.getStatus(), errors);
        validateNotWritable("developer", Utils.encodeId(revision.getOwnerId()), Utils.encodeId(oldRevision.getOwnerId()), errors);
        validateItem(revision, errors);

        validatePlatforms(revision.getPlatforms(), errors);
        validateUserInteractionModes(revision.getUserInteractionModes(), errors);
        validateSupportedInputDevices(revision.getSupportedInputDevices(), errors);
        validatePackageName(revision.getPackageName(), revision.getItemId(), errors);
        validateLocales(revision.getLocales(), errors);

        validateMapEmpty("futureExpansion", revision.getFutureExpansion(), errors);

        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating item-revision. ", exception);
            throw exception;
        }
    }

    @Override
    protected boolean validateStatus(String status, List<AppError> errors) {
        if (status == null || !ALL_STATUS.contains(status)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("status", Joiner.on(',').join(ALL_STATUS)));
            return false;
        }
        return true;
    }

    private void validateItem(ItemRevision revision, List<AppError> errors) {
        if (validateFieldNotNull("item", revision.getItemId(), errors)) {
            Item item = itemRepo.get(revision.getItemId());
            if (validateResourceExists("item", revision.getItemId(), item, errors)) {
                if (revision.getOwnerId() != null && !revision.getOwnerId().equals(item.getOwnerId())) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalid("item", "item should have same owner as item-revision"));
                }
                if (Status.PENDING_REVIEW.is(revision.getStatus()) || Status.APPROVED.is(revision.getStatus())) {
                    validateBinariesAndDownloadName(revision.getBinaries(), revision.getDownloadName(), item.getType(), errors);
                }
            }
        }
    }

    private void validateLocales(Map<String, ItemRevisionLocaleProperties> locales, List<AppError> errors) {
        if (validateMapNotEmpty("locales", locales, errors)) {
            for (Map.Entry<String, ItemRevisionLocaleProperties> entry : locales.entrySet()) {
                String locale = entry.getKey();
                ItemRevisionLocaleProperties properties = entry.getValue();
                validateLocale("locales", locale, errors);
                if (validateFieldNotNull("locales." + locale, properties, errors)) {
                    validateStringNotEmpty("locales." + locale + ".name", properties.getName(), errors);
                }
                validateOptionalEmail("supportEmail", properties.getSupportEmail(), errors);
                validateOptionalUrl("communityForumLink", properties.getCommunityForumLink(), errors);
                validateOptionalUrl("manualDocument", properties.getManualDocument(), errors);
                validateOptionalUrl("website", properties.getWebsite(), errors);
                validateImages(properties.getImages(), errors);
            }
        }
    }

    private void validateChannelsAndHostItems(List<String> distributedChannels, List<String> hostItemIds, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(distributedChannels)) {
            for (String channel : distributedChannels) {
                if (channel==null || !DistributionChannel.contains(channel)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("distributedChannel", Joiner.on(',').join(DistributionChannel.values())));
                }
            }
            if (distributedChannels.contains(DistributionChannel.INAPP.name())) {
                validateHostItems(hostItemIds, errors);
            }
        } else {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("distributionChannel"));
        }
    }

    private void validateHostItems(List<String> hostItemIds, List<AppError> errors) {
        if (validateCollectionNotEmpty("iapHostItems", hostItemIds, errors)) {
            for (String itemId : hostItemIds) {
                if (itemId == null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalid("iapHostItems", "should not contain null"));
                } else {
                    Item item = itemRepo.get(itemId);
                    validateResourceExists("item", itemId, item, errors);
                }
            }
        }
    }

    private void validateBinariesAndDownloadName(Map<String, Binary> binaries, String downloadName, String itemType, List<AppError> errors) {
        if (ItemType.APP.is(itemType) || ItemType.DOWNLOADED_ADDITION.is(itemType) || ItemType.VIDEO.is(itemType) || ItemType.PHOTO.is(itemType)) {
            if (validateMapNotEmpty("binaries", binaries, errors)) {
                for (String key : binaries.keySet()) {
                    if (!Platforms.contains(key)) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("binaries", "binary platform should be one of " + Joiner.on(',').join(Platforms.values())));
                    }
                    Binary binary = binaries.get(key);
                    if (!UrlValidator.getInstance().isValid(binary.getHref())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("binaries", "invalid href for " + key));
                    }
                    if (!StringUtils.isEmpty(binary.getMd5()) && !Utils.isValidMd5(binary.getMd5())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldInvalid("binaries", "invalid md5 for " + key));
                    }
                }
            }
            validateStringNotEmpty("downloadName", downloadName, errors);
        } else {
            validateMapEmpty("binaries", binaries, errors);
            validateStringEmpty("downloadName", downloadName, errors);
        }
    }

    private void validatePlatforms(List<String> platforms, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(platforms)) {
            for (String platform : platforms) {
                if (!Platforms.contains(platform)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("platforms", Joiner.on(',').join(Platforms.values())));
                    return;
                }
            }
        }
    }

    private void validateUserInteractionModes(List<String> modes, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(modes)) {
            for (String mode : modes) {
                if (!InteractionModes.contains(mode)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("userInteractionModes", Joiner.on(',').join(InteractionModes.values())));
                    return;
                }
            }
        }
    }

    private void validateSupportedInputDevices(List<String> devices, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(devices)) {
            for (String device : devices) {
                if (!InputDevices.contains(device)) {
                    errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("supportedInputDevices", Joiner.on(',').join(InputDevices.values())));
                }
            }
        }
    }

    private void validatePackageName(String packageName, String itemId, List<AppError> errors) {
        if (!StringUtils.isEmpty(packageName) && !StringUtils.isEmpty(itemId)) {
            boolean valid = itemRevisionRepo.checkPackageName(itemId, packageName);
            if (!valid) {
                errors.add(AppErrors.INSTANCE.duplicatePackageName(packageName));
            }
        }
    }
}
