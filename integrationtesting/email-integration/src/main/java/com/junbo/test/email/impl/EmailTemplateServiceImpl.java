/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email.impl;

import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.email.EmailTemplateService;

/**
 * Created by Wei on 8/12/14.
 */
public class EmailTemplateServiceImpl extends HttpClientBase implements EmailTemplateService {

    private static EmailTemplateService instance;
    private static String emailTemplateUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/email-templates";
    private boolean isServiceScope = true;

    private EmailTemplateServiceImpl() {
        componentType = ComponentType.EMAIL;
    }

    public static synchronized EmailTemplateService getInstance() {
        if (instance == null) {
            instance = new EmailTemplateServiceImpl();
        }
        return instance;
    }

    public Results<EmailTemplate> getEmailTemplates(com.junbo.email.spec.model.QueryParam queryParam, boolean isAdminScope) throws Exception {
        return this.getEmailTemplates(queryParam, 200, isAdminScope);
    }
    public Results<EmailTemplate> getEmailTemplates(com.junbo.email.spec.model.QueryParam queryParam, int expectedResponseCode, boolean isAdminScope) throws Exception {
        String emailTemplateGetUrl = emailTemplateUrl +
                "?action=" + queryParam.getAction() +
                "&source=" + queryParam.getSource() +
                "&locale=" + queryParam.getLocale();
        String responseBody = restApiCall(HttpClientBase.HTTPMethod.GET, emailTemplateGetUrl, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<EmailTemplate>>() {}, responseBody);
    }

    public EmailTemplate postEmailTemplate(EmailTemplate template, boolean isAdminScope) throws Exception {
        return postEmailTemplate(template, 200, isAdminScope);
    }
    public EmailTemplate postEmailTemplate(EmailTemplate template, int expectedResponseCode, boolean isAdminScope) throws Exception {
        if (isAdminScope) {
            this.componentType = ComponentType.EMAILADMIN;
        }
        String responseBody = restApiCall(HTTPMethod.POST, emailTemplateUrl, template, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<EmailTemplate>() {}, responseBody);
    }

    public EmailTemplate getEmailTemplate(EmailTemplateId id, boolean isAdminScope) throws Exception {
        return this.getEmailTemplate(id,200,isAdminScope);
    }
    public EmailTemplate getEmailTemplate(EmailTemplateId id, int expectedResponseCode, boolean isAdminScope) throws Exception {
        if (isAdminScope) {
            this.componentType = ComponentType.EMAILADMIN;
        }
        String emailTemplateGetUrl = emailTemplateUrl + "/" + id.toString();
        String responseBody = restApiCall(HTTPMethod.GET, emailTemplateGetUrl, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<EmailTemplate>() {}, responseBody);
    }

    public EmailTemplate putEmailTemplate(EmailTemplateId id, EmailTemplate template, boolean isAdminScope) throws Exception {
        return  this.putEmailTemplate(id,template,200,isAdminScope);
    }
    public EmailTemplate putEmailTemplate(EmailTemplateId id, EmailTemplate template, int expectedResponseCode, boolean isAdminScope) throws Exception {
        if (isAdminScope) {
            this.componentType = ComponentType.EMAILADMIN;
        }
        String emailTemplatePutUrl = emailTemplateUrl + '/' + id.toString();
        String responseBody = restApiCall(HTTPMethod.PUT, emailTemplatePutUrl, template, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<EmailTemplate>() {}, responseBody);
    }

    public void deleteEmailTemplate(EmailTemplateId id, boolean isAdminScope) throws Exception  {
        this.deleteEmailTemplate(id, 200, isAdminScope);
    }
    public void deleteEmailTemplate(EmailTemplateId id, int expectedResponseCode, boolean isAdminScope) throws Exception {
        if (isAdminScope) {
            this.componentType = ComponentType.EMAILADMIN;
        }
        String emailTemplateDeleteUrl = emailTemplateUrl + "/" + id.toString();
        restApiCall(HTTPMethod.DELETE, emailTemplateDeleteUrl, null, expectedResponseCode, isServiceScope);
    }
}
