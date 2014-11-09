/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.spec.enums.LocaleAccuracy;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Common utils.
 */
public abstract class ResourceSupport {
    protected String calLocaleAccuracy(SimpleLocaleProperties properties) {
        if (properties == null || properties.getName() == null && properties.getDescription() == null) {
            return LocaleAccuracy.LOW.name();
        }
        if (properties.getName() != null && properties.getDescription() != null) {
            return LocaleAccuracy.HIGH.name();
        }
        return LocaleAccuracy.MEDIUM.name();
    }

    protected SimpleLocaleProperties getLocaleProperties(Map<String, SimpleLocaleProperties> localePropertiesMap, String locale, Map<String, String> localeRelations) {
        if (CollectionUtils.isEmpty(localePropertiesMap) || StringUtils.isEmpty(locale)) {
            return new SimpleLocaleProperties();
        }

        SimpleLocaleProperties result = localePropertiesMap.get(locale);
        if (result == null) {
            result = new SimpleLocaleProperties();
        }

        String fallbackLocale = locale;
        while (result.getName() == null || result.getDescription() == null) {
            if (localeRelations.get(fallbackLocale) == null) {
                break;
            }
            fallbackLocale = localeRelations.get(fallbackLocale);
            SimpleLocaleProperties fallbackProperties = localePropertiesMap.get(fallbackLocale);
            if (fallbackProperties != null) {
                if (result.getName() == null) {
                    result.setName(fallbackProperties.getName());
                }
                if (result.getDescription() == null) {
                    result.setDescription(fallbackProperties.getDescription());
                }
            }
        }

        return result;
    }
}
