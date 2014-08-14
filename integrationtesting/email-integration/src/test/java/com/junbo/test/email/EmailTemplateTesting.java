/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email;

import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.QueryParam;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.SystemRuntimeHelper;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.email.impl.EmailTemplateServiceImpl;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by Wei on 8/12/14.
 */
public class EmailTemplateTesting {

    private void prepareTestData(boolean isAdminScope) throws Exception {
        OAuthService oAuthService = OAuthServiceImpl.getInstance();
        if(isAdminScope) {
            oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.EMAILADMIN);
        } else {
            oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.EMAIL);
        }
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get /email-templates?action={action}&source={source}&locale={locale}",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Get an email template",
            steps = {
                    "1. get email template by action, source and locale" +
                            "/n 2. get email template by id"

            }
    )
    @Test
    public void testGetEmailTemplate() throws Exception {
        this.prepareTestData(false);
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        Results<EmailTemplate> templates = templateService.getEmailTemplates(this.buildQueryParam(), false);
        assertNotNull("email template can't be null", templates);
        assertEquals("email template count should be 1", 1, templates.getItems().size());
        this.prepareTestData(false);
        EmailTemplate template = templateService.getEmailTemplate(templates.getItems().get(0).getId(), false);
        assertNotNull("get email template by id failed, it can't be null", template);
        assertEquals("email template count should be 1", template.getLocale(), "en_US");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post /email-templates",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Post an email template",
            steps = {
                    "1. post an email template"
            }
    )
    @Test
    public void testPostEmailTemplate() throws Exception {
        this.prepareTestData(true);
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        EmailTemplate template = templateService.postEmailTemplate(this.buildEmailTemplate(), true);
        assertNotNull("email template can't be null", template);
        assertEquals("email template post failed", template.getSource(), "Integration");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Put /email-templates",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Update an email template",
            steps = {
                    "1. post an email template" +
                            "/n 2. update email template before posted" +
                            "/n 3. verify the update result"
            }
    )
     @Test
     public void testPutEmailTemplate() throws Exception {
        this.prepareTestData(true);
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        EmailTemplate template = templateService.postEmailTemplate(this.buildEmailTemplate(), true);
        assertNotNull("post email template failed, it can't be null", template);
        assertEquals("email template post failed",template.getSource(), "Integration");

        this.prepareTestData(true);
        EmailTemplate updatedTemplate = templateService.putEmailTemplate(template.getId(), updateEmailTemplate(template), true);
        assertNotNull("update template failed, it can't be null", updatedTemplate);
        assertEquals("update email template failed",updatedTemplate.getAction(), "TestUpdate");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete /email-templates",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Delete an email template",
            steps = {
                    "1. post an email template" +
                            "/n 2. then delete this email template" +
                            "/n 3. verify the delete result"
            }
    )
    @Test
    public void testDeleteEmailTemplate() throws Exception {
        this.prepareTestData(true);
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        EmailTemplate template = templateService.postEmailTemplate(this.buildEmailTemplate(), true);
        assertNotNull("post email template failed,it can't be null", template);
        assertEquals("email template post failed",template.getSource(), "Integration");

        this.prepareTestData(true);
        EmailTemplate updatedTemplate = templateService.putEmailTemplate(template.getId(), updateEmailTemplate(template), true);
        assertNotNull("delete email template failed, it can't be null", updatedTemplate);
        assertEquals("delete email template failed", updatedTemplate.getAction(), "TestUpdate");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post /email-templates, Put/email-templates",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Validate field of post an email template",
            steps = {
                    "1. post an email template" +
                            "/n 2. put an email template" +
                            "/n 3. validate email template field"
            }
    )
    @Test
    public void testValidateEmailTemplateField() throws Exception {
        this.prepareTestData(true);
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        try {
            EmailTemplate template = new EmailTemplate();
            template.setSource("Integration");
            template.setLocale("en_US");
            template.setProviderName("Mandrill");
            EmailTemplate result = templateService.postEmailTemplate(template, 400, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field is required")) {
                assertTrue("validate post email template required field failed",false);
            }
        }
        this.prepareTestData(true);
        try {
            EmailTemplate template = new EmailTemplate();
            template.setAction("Welcome");
            template.setSource("Oculus");
            template.setLocale("en_US");
            template.setProviderName("Mandrill");
            EmailTemplate result = templateService.postEmailTemplate(template, 409, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Email Template Already Exist")) {
                assertTrue("validate post email template duplicate name failed",false);
            }
        }
        this.prepareTestData(true);
        try {
            EmailTemplate template = new EmailTemplate();
            template.setAction("Test");
            template.setSource("Integration");
            template.setLocale("en_US");
            template.setProviderName("Mandrill");
            EmailTemplate result = templateService.putEmailTemplate(new EmailTemplateId("test"+System.currentTimeMillis()), template, 412, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Email Template Not Found")) {
                assertTrue("validate put email template field [id] failed",false);
            }
        }
        this.prepareTestData(true);
        try {
            EmailTemplate template = new EmailTemplate();
            template.setAction("Test");
            template.setSource("Integration");
            template.setLocale("en_US");
            template.setProviderName("Mandrill");
            template.setSubject("Hi, {TESTER}");
            EmailTemplate result = templateService.postEmailTemplate(template, 412, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Invalid Placeholder Names")) {
                assertTrue("validate post email template field [placeholder] failed",false);
            }
        }
        this.prepareTestData(true);
        try {
            EmailTemplate template = new EmailTemplate();
            template.setId(new EmailTemplateId("test"+System.currentTimeMillis()));
            template.setAction("Test");
            template.setSource("Integration");
            template.setLocale("en_US");
            template.setProviderName("Mandrill");
            template.setSubject("Hi, {TESTER}");
            EmailTemplate result = templateService.postEmailTemplate(template, 400, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field must be null")) {
                assertTrue("validate post email template field [id] must be null failed",false);
            }
        }
        this.prepareTestData(true);
        try {
            EmailTemplate result = templateService.postEmailTemplate(null, 400, true);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Request body is required")) {
                assertTrue("validate post email template body is null failed",false);
            }
        }
    }

    private QueryParam buildQueryParam() {
        QueryParam queryParam = new QueryParam();
        queryParam.setAction("Welcome");
        queryParam.setSource("Oculus");
        queryParam.setLocale("en_US");
        return queryParam;
    }

    private EmailTemplate buildEmailTemplate() {
        EmailTemplate template = new EmailTemplate();
        String action = "Test"+System.currentTimeMillis();
        template.setAction(action);
        template.setSource("Integration");
        template.setLocale("en_US");
        template.setProviderName("Mandrill");

        return template;
    }

    private EmailTemplate updateEmailTemplate(EmailTemplate template) {
        template.setId(null);
        template.setAction("TestUpdate");
        return template;
    }
}
