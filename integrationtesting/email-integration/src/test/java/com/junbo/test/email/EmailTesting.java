/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email;

import com.junbo.common.id.EmailId;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.QueryParam;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;

import com.junbo.test.email.impl.EmailServiceImpl;
import com.junbo.test.email.impl.EmailTemplateServiceImpl;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

/**
 * Created by Wei on 8/12/14.
 */
public class EmailTesting extends TestClass {
    private LogHelper logger = new LogHelper(EmailTesting.class);
    private String defaultUserId;
    private User defaultUser;

    private void prepareUserData() throws Exception {
        UserService userService = UserServiceImpl.instance();
        defaultUserId = userService.PostUser();
        defaultUser = Master.getInstance().getUser(defaultUserId);
    }

    private void prepareAccessData() throws Exception {
        OAuthService oAuthService = OAuthServiceImpl.getInstance();
        oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.EMAIL);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post /emails",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Post an email",
            steps = {
                    "1. post a user" +
                            "/n 2. get email template by action, source and locale" +
                            "/n 3. post an email by user info and email template info"
            }
    )
     @Test
     public void testPostEmail() throws Exception {
        this.prepareUserData();
        Email email = this.buildEmail();
        this.prepareAccessData();
        EmailService emailService = EmailServiceImpl.getInstance();
        Email result = emailService.postEmail(email);
        assertNotNull("post email failed",result);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get /emails",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Post an email",
            steps = {
                    "1. post an email" +
                            "/n 2. then, get this email"
            }
    )
    @Test
    public void testGetEmail() throws Exception {
        this.prepareUserData();
        Email email = this.buildEmail();
        this.prepareAccessData();
        EmailService emailService = EmailServiceImpl.getInstance();
        Email result = emailService.postEmail(email);
        assertNotNull(result);
        this.prepareAccessData();
        Email getEmail = emailService.getEmail(result.getId());
        assertNotNull("get email failed", getEmail);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Put /emails",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Put an email",
            steps = {
                    "1. post an email" +
                            "/n 2. then, update this email"
            }
    )
    @Test
    public void testPutEmail() throws Exception {
        this.prepareUserData();
        Email email = this.buildEmail();
        email.setScheduleTime(new Date(System.currentTimeMillis()+100000));
        this.prepareAccessData();
        EmailService emailService = EmailServiceImpl.getInstance();
        Email result = emailService.postEmail(email);
        assertNotNull("post email failed during put email test",result);
        this.prepareAccessData();
        EmailId id = result.getId();
        Date scheduleTime = result.getScheduleTime();
        Email updatedEmail = this.buildUpdateEmail(result);
        Email updated = emailService.putEmail(id, updatedEmail);
        assertNotNull("update email failed", updated);
        assertNotSame("update email failed", updated.getScheduleTime(), scheduleTime);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete /emails",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Delete an email",
            steps = {
                    "1. post an email" +
                            "/n 2. then, delete this email"
            }
    )
    @Test
    public void testDeleteEmail() throws Exception {
        this.prepareUserData();
        Email email = this.buildEmail();
        email.setScheduleTime(new Date(System.currentTimeMillis()+100000));
        this.prepareAccessData();
        EmailService emailService = EmailServiceImpl.getInstance();
        Email result = emailService.postEmail(email);
        assertNotNull("post email failed during delete email test",result);
        this.prepareAccessData();
        emailService.deleteEmail(result.getId());
        this.prepareAccessData();
        Email deleteEmail = emailService.getEmail(result.getId());
        assertNull("delete email failed", deleteEmail);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post /emails",
            component = Component.Email,
            owner = "WeiJiang",
            status = Status.Enable,
            description = "Validate field of post an email",
            steps = {
                    "1. post an email" +
                            "/n 2.validate email field"
            }
    )
    @Test
    public void testValidateEmailField() throws Exception {
        EmailService emailService = EmailServiceImpl.getInstance();
        this.prepareAccessData();
        try {
            Email email = new Email();
            email.setId(new EmailId("test"+System.currentTimeMillis()));
            Email result = emailService.postEmail(email, 400);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field must be null")) {
                assertTrue("validate post email field [id] must be null failed",false);
            }
        }
        this.prepareAccessData();
        try {
            Email email = new Email();
            Email result = emailService.postEmail(email, 400);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field is required")) {
                assertTrue("validate post email required field failed",false);
            }
        }
        this.prepareAccessData();
        this.prepareUserData();
        try {
            Email email = this.buildEmail();
            email.setUserId(new UserId(System.currentTimeMillis()));
            Email result = emailService.postEmail(email, 412);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("User Not Found")) {
                assertTrue("validate post email field [user] failed",false);
            }
        }
        this.prepareAccessData();
        this.prepareUserData();
        try {
            Email email = this.buildEmail();
            email.setTemplateId(new EmailTemplateId("test" + System.currentTimeMillis()));
            Email result = emailService.postEmail(email, 412);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Email Template Not Found")) {
                assertTrue("validate post email field [template] failed",false);
            }
        }
        this.prepareAccessData();
        this.prepareUserData();
        try {
            Email email = this.buildEmail();
            email.setScheduleTime(new Date(System.currentTimeMillis()-10000));
            Email result = emailService.postEmail(email, 400);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field value is invalid")) {
                assertTrue("validate post email field [schedule] failed",false);
            }
        }
        this.prepareAccessData();
        this.prepareUserData();
        try {
            Email email = this.buildEmail();
            List<String> recipients = new ArrayList<>();
            recipients.add("example#.com");
            email.setRecipients(recipients);
            Email result = emailService.postEmail(email, 400);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Field value is invalid")) {
                assertTrue("validate post email field [recipients] format failed",false);
            }
        }
        this.prepareAccessData();
        this.prepareUserData();
        try {
            Email email = this.buildEmail();
            Map<String, String> replacements = new HashMap();
            replacements.put("invalidreplacements","tester");
            email.setReplacements(replacements);
            Email result = emailService.postEmail(email, 412);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Invalid Replacements")) {
                assertTrue("validate post email field [replacements] failed",false);
            }
        }
        this.prepareAccessData();
        try {
            emailService.deleteEmail(new EmailId("test" + System.currentTimeMillis()), 412);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if(!errorMsg.contains("Email Schedule Not Found")) {
                assertTrue("validate delete email field [id] invalid failed",false);
            }
        }
    }

    private Email buildEmail()  throws Exception {
        Email email = new Email();
        email.setUserId(defaultUser.getId());
        EmailTemplateId templateId = this.getEmailTemplateId();
        assertNotNull("get email template failed", templateId);
        email.setTemplateId(templateId);
        Map<String, String> replacements = new HashMap();
        replacements.put("accountname","tester");
        email.setReplacements(replacements);
        return email;
    }

    private Email buildUpdateEmail(Email email) {
        email.setId(null);
        email.setStatus(null);
        email.setScheduleTime(new Date(System.currentTimeMillis()+100000));
        email.setCreatedTime(null);
        email.setCreatedBy(null);
        return email;
    }

    private EmailTemplateId getEmailTemplateId() throws Exception {
        this.prepareAccessData();
        EmailTemplateService templateService = EmailTemplateServiceImpl.getInstance();
        QueryParam queryParam = new QueryParam();
        queryParam.setAction("Welcome");
        queryParam.setSource("Oculus");
        queryParam.setLocale("en_US");

        Results<EmailTemplate> templates = templateService.getEmailTemplates(queryParam,false);

        if(templates != null && templates.getItems().size() > 0 ) {
            return templates.getItems().get(0).getId();
        }
        return null;
    }
}
