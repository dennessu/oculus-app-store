/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.validators;

import com.google.common.base.Joiner;
import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.common.*;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * ValidationSupport.
 */
public abstract class ValidationSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationSupport.class);
    private static final List<String> COUNTRY_CODES = Collections.unmodifiableList(Arrays.asList(Locale.getISOCountries()));

    protected void validateRequestNotNull(BaseModel model) {
        if (model == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidJson("cause", "Json is null").exception();
            LOGGER.error("Invalid json.", exception);
            throw exception;
        }
    }

    protected <T> boolean validateFieldNotNull(String fieldName, T value, List<AppError> errors) {
        if (value == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired(fieldName));
            return false;
        }
        return true;
    }

    protected <T> boolean validateFieldNull(String fieldName, T value, List<AppError> errors) {
        if (value != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateCollectionEmpty(String fieldName, Collection<?> collection, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(collection)) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateCollectionNotEmpty(String fieldName, Collection<?> collection, List<AppError> errors) {
        if (CollectionUtils.isEmpty(collection)) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateMapEmpty(String fieldName, Map<?, ?> map, List<AppError> errors) {
        if (!CollectionUtils.isEmpty(map)) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateMapNotEmpty(String fieldName, Map<?, ?> map, List<AppError> errors) {
        if (CollectionUtils.isEmpty(map)) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateStringEmpty(String fieldName, String value, List<AppError> errors) {
        if (!StringUtils.isEmpty(value)) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull(fieldName));
            return false;
        }
        return true;
    }

    protected boolean validateStringNotEmpty(String fieldName, String value, List<AppError> errors) {
        if (StringUtils.isEmpty(value)) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired(fieldName));
            return false;
        }
        return true;
    }

    protected <T> boolean validateNotWritable(String fieldName, T actual, T expected, List<AppError> errors) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable(fieldName, actual, expected));
            return false;
        }
        return true;
    }

    protected boolean validateOptionalEnum(String fieldName, String actual, List<String> enumValues, List<AppError> errors) {
        if (actual != null && !enumValues.contains(actual)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum(fieldName, Joiner.on(',').join(enumValues)));
            return false;
        }
        return true;
    }

    protected boolean validateFieldMatch(String fieldName, String actual, String expected, List<AppError> errors) {
        if (!expected.equals(actual)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, "should be " + expected));
            return false;
        }
        return true;
    }

    protected boolean validateStatus(String status, List<AppError> errors) {
        if (status == null || !Status.contains(status)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("status", Joiner.on(',').join(Status.ALL)));
            return false;
        }
        return true;
    }

    protected boolean validateRequiredEnum(String fieldName, String actual, List<String> enumValues, List<AppError> errors) {
        if (actual == null || !enumValues.contains(actual)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum(fieldName, Joiner.on(',').join(enumValues)));
            return false;
        }
        return true;
    }

    protected boolean validateOptionalEmail(String fieldName, String email, List<AppError> errors) {
        if (!StringUtils.isEmpty(email) && !EmailValidator.getInstance().isValid(email)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, "invalid email format"));
            return false;
        }
        return true;
    }

    protected boolean validateOptionalUrl(String fieldName, String url, List<AppError> errors) {
        if (!StringUtils.isEmpty(url) && !UrlValidator.getInstance().isValid(url)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, "invalid url format"));
            return false;
        }
        return true;
    }

    protected void validatePrice(Price price, List<AppError> errors) {
        if (!PriceType.contains(price.getPriceType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("priceType", Joiner.on(", ").join(PriceType.ALL)));
        }

        if (PriceType.TIERED.is(price.getPriceType())) {
            validateMapEmpty("prices", price.getPrices(), errors);
            validateFieldNotNull("priceTier", price.getPriceTier(), errors);
        } else if (PriceType.FREE.is(price.getPriceType())) {
            validateFieldNull("priceTier", price.getPriceTier(), errors);
            validateMapEmpty("prices", price.getPrices(), errors);
        } else if (PriceType.CUSTOM.is(price.getPriceType())) {
            validateFieldNull("priceTier", price.getPriceTier(), errors);
            validateFieldNotNull("prices", price.getPrices(), errors);
        }
    }

    protected void validateImages(Images images, List<AppError> errors) {
        if (images == null) {
            return;
        }
        validateImage(images.getBackground(), "images.background[", errors);
        validateImage(images.getFeatured(), "images.featured[", errors);
        validateImage(images.getMain(), "images.main[", errors);
        validateImage(images.getThumbnail(), "images.thumbnail[", errors);

        if (images.getGallery() != null) {
            for (ImageGalleryEntry galleryEntry : images.getGallery()) {
                validateImage(galleryEntry.getThumbnail(), "images.gallery.thumbnail[", errors);
                validateImage(galleryEntry.getFull(), "images.gallery.full[", errors);
            }
        }
    }

    private void validateImage(Map<String, Image> images, String imageName, List<AppError> errors) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }
        for (String resolution : images.keySet()) {
            validateOptionalUrl(imageName + resolution + "].href", images.get(resolution).getHref(), errors);
            validateOptionalUrl(imageName + resolution + "].hrefOriginal",  images.get(resolution).getHrefOriginal(), errors);
        }
    }

    protected boolean validateResourceExists(String resourceName, String resourceId, BaseModel model, List<AppError> errors) {
        if (model == null) {
            errors.add(AppCommonErrors.INSTANCE.resourceNotFound(resourceName, resourceId));
            return false;
        }
        return true;
    }

    protected void validateCountryCodes(String fieldName, Collection<String> countryCodes, List<AppError> errors) {
        if (countryCodes == null) {
            return;
        }
        for (String countryCode : countryCodes) {
            if (!COUNTRY_CODES.containsAll(Arrays.asList(countryCode))) {
                errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, countryCode + " is not a valid country code"));
            }
        }
    }

    protected void validateLocale(String fieldName, String locale, List<AppError> errors) {
        if (!ValidationUtils.isValidLocale(locale)) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid(fieldName, "invalid locale code: " + locale));
        }
    }
}
