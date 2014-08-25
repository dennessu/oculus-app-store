/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email.impl;

import com.junbo.common.id.EmailId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.email.spec.model.Email;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.email.EmailService;
/**
 * Created by Wei on 8/12/14.
 */
public class EmailServiceImpl extends HttpClientBase implements EmailService {

    private static EmailService instance;
    private static String emailUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "emails";
    private boolean isServiceScope = true;

    private EmailServiceImpl() {
        componentType = ComponentType.EMAIL;
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailServiceImpl();
        }
        return instance;
    }

    public Email postEmail(Email email) throws Exception {
        return this.postEmail(email, 200);
    }
    public Email postEmail(Email email, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, emailUrl, email, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Email>() {}, responseBody);
    }

    public Email getEmail(EmailId id) throws Exception {
        return this.getEmail(id, 200);
    }
    public Email getEmail(EmailId id, int expectedResponseCode) throws Exception {
        String emailGetUrl = emailUrl + "/" + id.toString();
        String responseBody = restApiCall(HTTPMethod.GET, emailGetUrl, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Email>() {}, responseBody);
    }

    public Email putEmail(EmailId id, Email email) throws Exception {
        return this.putEmail(id, email, 200);
    }
    public Email putEmail(EmailId id, Email email, int expectedResponseCode) throws Exception {
        String emailPutUrl = emailUrl + "/" + id.toString();
        String responseBody = restApiCall(HTTPMethod.PUT, emailPutUrl, email, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Email>() {}, responseBody);
    }

    public void deleteEmail(EmailId id) throws Exception {
        this.deleteEmail(id, 204);
    }
    public void deleteEmail(EmailId id, int expectedResponseCode) throws Exception {
        String emailDeleteUrl = emailUrl + "/" + id.toString();
        String responseBody = restApiCall(HTTPMethod.DELETE, emailDeleteUrl, null, expectedResponseCode, isServiceScope);
        if(responseBody != null) {
            Email email = new JsonMessageTranscoder().decode(new TypeReference<Email>() {}, responseBody);
        }
    }
}
