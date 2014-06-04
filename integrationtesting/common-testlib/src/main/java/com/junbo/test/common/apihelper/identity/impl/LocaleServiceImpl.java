/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.LocaleService;

/**
 * @author Jason
 * time 4/29/2014
 * Locale related API helper, including get/post/put/delete locale.
 */
public class LocaleServiceImpl extends HttpClientBase implements LocaleService {

    private final String localeURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/locales";
    private static LocaleService instance;

    public static synchronized LocaleService instance() {
        if (instance == null) {
            instance = new LocaleServiceImpl();
        }
        return instance;
    }

    private LocaleServiceImpl() {
    }

    public Locale postDefaultLocale() throws Exception {
        Locale locale = new Locale();
        locale.setLocaleCode("en-US");
        locale.setLocaleName("US");
        locale.setLongName("en_US");
        locale.setShortName("US");

        return this.postLocale(locale);
    }

    public Locale postLocale(Locale locale) throws Exception {
        return postLocale(locale, 201);
    }

    public Locale postLocale(Locale locale, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, localeURL, locale, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Locale>() {}, responseBody);
    }

    public Results<Locale> getLocales() throws Exception {
        return getLocales(200);
    }

    public Results<Locale> getLocales(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, localeURL, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Locale>>() {}, responseBody);
    }

    public Locale getLocale(String localeId) throws Exception {
        return getLocale(localeId, 200);
    }

    public Locale getLocale(String localeId, int expectedResponseCode) throws Exception {
        String url = localeURL + "/" + localeId;
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Locale>() {}, responseBody);
    }

    public Locale updateLocale(Locale locale) throws Exception {
        return updateLocale(locale, 200);
    }

    public Locale updateLocale(Locale locale, int expectedResponseCode) throws Exception {
        String putUrl = localeURL + "/" + locale.getId().toString();
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, locale, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Locale>() {}, responseBody);
    }

    public void deleteLocale(String localeId) throws Exception {
        this.deleteLocale(localeId, 204);
    }

    public void deleteLocale(String localeId, int expectedResponseCode) throws Exception {
        String url = localeURL + "/" + localeId;
        restApiCall(HTTPMethod.DELETE, url, expectedResponseCode);
    }

}
