/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.store.spec.model.external.sewer.SewerParam;
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.store.apihelper.CaseyService;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The CaseyServiceImpl class.
 */
public class CaseyServiceImpl  extends HttpClientBase implements CaseyService {

    private static CaseyService instance;

    private static ObjectMapper caseyObjectMapper;

    static {
        caseyObjectMapper = ObjectMapperProvider.createObjectMapper();
        caseyObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        caseyObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = super.getHeader(isServiceScope, headersToRemove);
        headers.put("X_QA_CALLED_BY_TEST", Collections.singletonList("true"));
        return headers;
    }

    public static synchronized CaseyService getInstance() {
        if (instance == null) {
            instance = new CaseyServiceImpl();
        }
        return instance;
    }

    @Override
    public CaseyResults<JsonNode> getCmsPage(String path, String label, SewerParam sewerParam) throws Exception {
        HashMap<String, List<String>> params = new HashMap<>();
        if (path != null) {
            params.put("path", Collections.singletonList(path));
        }
        if (label != null) {
            params.put("label", Collections.singletonList(label));
        }
        if (sewerParam.getExpand() != null) {
            params.put("expand", Collections.singletonList(sewerParam.getExpand()));
        }
        params.put("country", Collections.singletonList(sewerParam.getCountry()));
        params.put("locale", Collections.singletonList(sewerParam.getLocale()));

        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/cms-pages", null, 0, params, false);
        return caseyObjectMapper.readValue(responseBody, new TypeReference<CaseyResults<JsonNode>>() {});

    }

    @Override
    public CaseyResults<JsonNode> searchCmsOffers(String cmsPage, String cmsSlot, String platform, SewerParam sewerParam) throws Exception {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("cmsPage", Collections.singletonList(cmsPage));
        params.put("cmsSlot", Collections.singletonList(cmsSlot));
        params.put("platform", Collections.singletonList("ANDROID"));
        params.put("locale", Collections.singletonList(sewerParam.getLocale()));
        params.put("country", Collections.singletonList(sewerParam.getCountry()));

        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/search", null, 0, params, false);
        return caseyObjectMapper.readValue(responseBody, new TypeReference<CaseyResults<JsonNode>>() {});
    }

    @Override
    protected String getEndPointUrl() {
        return ConfigHelper.getSetting("caseyEndpoint");
    }
}
