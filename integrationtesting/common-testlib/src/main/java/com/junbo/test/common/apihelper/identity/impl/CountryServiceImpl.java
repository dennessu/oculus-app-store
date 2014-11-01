/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.CountryService;

/**
 * Created by xiali_000 on 2014/11/1.
 */
public class CountryServiceImpl extends HttpClientBase implements CountryService {
    private final String countriesUrl = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/countries";
    private static CountryService instance;

    public static synchronized CountryService instance() {
        if (instance == null) {
            instance = new CountryServiceImpl();
        }
        return instance;
    }

    private CountryServiceImpl() {
    }

    @Override
    public Results<Country> getAllCountries() throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, countriesUrl, 200);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Country>>() {}, responseBody);
    }
}
