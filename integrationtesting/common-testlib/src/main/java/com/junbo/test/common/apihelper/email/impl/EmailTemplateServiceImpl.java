/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.email.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.email.EmailTemplateService;
import com.junbo.test.common.libs.LogHelper;

/**
 * Created by jason on 5/14/2014.
 */
public class EmailTemplateServiceImpl extends HttpClientBase implements EmailTemplateService {

    private static String emailTemplateUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1") + "email-templates";

    private LogHelper logger = new LogHelper(EmailTemplateServiceImpl.class);

    private static EmailTemplateService instance;

    public static synchronized EmailTemplateService getInstance() {
        if (instance == null) {
            instance = new EmailTemplateServiceImpl();
        }
        return instance;
    }

    private EmailTemplateServiceImpl() {
    }

    public Results<EmailTemplate> getEmailTemplates(String source, String action, String locale) throws Exception {
        return getEmailTemplates(source, action, locale, 200);
    }

    public Results<EmailTemplate> getEmailTemplates(String source, String action, String locale, int expectedResponseCode) throws Exception {
        String url = emailTemplateUrl + "?";
        if (source != null && source.length() > 0) {
            url = url.concat(String.format("source=%s", source));
            url = url.concat("&");
        }
        if (action != null && action.length() > 0) {
            url = url.concat(String.format("action=%s", action));
            url = url.concat("&");
        }
        if (locale != null && locale.length() > 0) {
            url = url.concat(String.format("locale=%s", locale));
        }
        if (url.endsWith("?") || url.endsWith("&")) {
            url = url.substring(0, url.length());
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<EmailTemplate>>() {}, responseBody);

    }

}
