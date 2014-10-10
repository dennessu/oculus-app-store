/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.external.casey.CaseyResults;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.store.apihelper.CaseyEmulatorService;
import org.apache.http.client.utils.URIBuilder;

/**
 * The CaseyEmulatorServiceImpl class.
 */
public class CaseyEmulatorServiceImpl extends HttpClientBase  implements CaseyEmulatorService {

    private static String baseUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/emulator/casey";

    private static CaseyEmulatorServiceImpl instance;

    public static synchronized CaseyEmulatorService getInstance() {
        if (instance == null) {
            instance = new CaseyEmulatorServiceImpl();
        }
        return instance;
    }

    @Override
    public CaseyEmulatorData postEmulatorData(CaseyEmulatorData caseyEmulatorData) throws Exception {
        String responseBody = restApiCall(HttpClientBase.HTTPMethod.POST, baseUrl + "/data", caseyEmulatorData, 200);
        CaseyEmulatorData data = new JsonMessageTranscoder().decode(new TypeReference<CaseyEmulatorData>() {
            }, responseBody);
            return data;
    }

    @Override
    public CaseyResults<CmsPage> getCmsPages(String path, String label, int expectedResponseCode) throws Exception {
        String url = getEndPointUrl() + "/emulator/casey/cms-pages?";
        URIBuilder builder = new URIBuilder(getEndPointUrl() + "/emulator/casey/cms-pages");
        if (path != null) {
            builder.addParameter("path", "\"" + path + "\"");
        }
        if (label != null) {
            builder.addParameter("label", "\"" + label + "\"");
        }

        String responseBody = restApiCall(HTTPMethod.GET, builder.build().toString(), expectedResponseCode);

        if (expectedResponseCode == 200) {
            CaseyResults<CmsPage> response = new JsonMessageTranscoder().decode(new TypeReference<CaseyResults<CmsPage>>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    private String appendQuery(String url, String name, Object val) {
        if (val != null) {
            return url + (url.endsWith("?") ? "" : "&") + name + "=" + val.toString();
        }
        return url;
    }
}
