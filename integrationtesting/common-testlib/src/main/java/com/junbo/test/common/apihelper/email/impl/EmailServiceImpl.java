/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.email.impl;

import com.junbo.test.common.apihelper.email.TempName;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.email.spec.model.Email;

/**
 @author Jason
  * Time: 5/14/2014
  * The implementation for email related APIs
 */
public class EmailServiceImpl extends HttpClientBase implements TempName {

    private static String emailUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE) + "emails";

    private LogHelper logger = new LogHelper(EmailServiceImpl.class);

    private static TempName instance;

    public static synchronized TempName getInstance() {
        if (instance == null) {
            instance = new EmailServiceImpl();
        }
        return instance;
    }

    private EmailServiceImpl() {
    }

    public Email postEmail(Email email) throws Exception {
        return postEmail(email, 200);
    }

    public Email postEmail(Email email, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, emailUrl, email, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Email>() {}, responseBody);
    }
}
