/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.clientproxy.impl;

import com.junbo.catalog.clientproxy.LocaleFacade;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.identity.spec.v1.option.list.LocaleListOptions;
import com.junbo.identity.spec.v1.resource.LocaleResource;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

/**
 * Locale facade implement.
 */
public class LocaleFacadeImpl implements LocaleFacade {
    private LocaleResource localeResource;
    private static final LocaleListOptions options = new LocaleListOptions();

    @Required
    public void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource;
    }

    @Override
    public Map<String, String> getLocaleRelations() {
        Map<String, String> result = new HashMap<>();
        Results<Locale> locales = localeResource.list(options).get();
        for (Locale locale : locales.getItems()) {
            if (locale.getFallbackLocale() == null) {
                continue;
            }
            result.put(locale.getId().getValue(), locale.getFallbackLocale().getValue());
        }
        return result;
    }
}
