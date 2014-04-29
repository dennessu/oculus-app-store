/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.common.model.Results;

/**
 * @author Jason
 * time 4/29/2014
 * Interface for User/Locale related API, including get/post/put/delete locale.
 */
public interface LocaleService {

    Locale postDefaultLocale() throws Exception;
    Locale postLocale(Locale locale) throws Exception;
    Locale postLocale(Locale locale, int expectedResponseCode) throws Exception;

    Results<Locale> getLocales() throws Exception;
    Results<Locale> getLocales(int expectedResponseCode) throws Exception;

    Locale getLocale(String localeId) throws Exception;
    Locale getLocale(String localeId, int expectedResponseCode) throws Exception;

    Locale updateLocale(Locale locale) throws Exception;
    Locale updateLocale(Locale locale, int expectedResponseCode) throws Exception;

    void deleteLocale(String localeId) throws Exception;
    void deleteLocale(String localeId, int expectedResponseCode) throws Exception;
}
