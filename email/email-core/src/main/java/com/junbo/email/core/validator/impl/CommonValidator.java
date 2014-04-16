/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl;

import com.junbo.email.db.repo.EmailTemplateRepository;
import com.junbo.email.spec.error.AppErrors;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.Model;
import com.junbo.identity.spec.v1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Common Validator.
 */
@Component("emailCommonValidator")
public class CommonValidator {
    @Autowired
    protected EmailTemplateRepository emailTemplateRepository;

    public void validateUser(User user, Long userId) {
        if(user == null) {
            throw AppErrors.INSTANCE.invalidUserId(userId.toString()).exception();
        }
        if(!user.getActive()) {
            throw AppErrors.INSTANCE.invalidUserStatus(userId.toString()).exception();
        }
    }

    protected void validateCommonField(Email email) {
        if(email == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception();
        }
        if(email.getUserId() == null && email.getRecipient() == null) {
            throw AppErrors.INSTANCE.missingField("user or recipient").exception();
        }
        if(email.getSource() == null) {
            throw AppErrors.INSTANCE.missingField("source").exception();
        }
        if(email.getAction() == null) {
            throw AppErrors.INSTANCE.missingField("action").exception();
        }
        if(email.getLocale() == null && email.getUserId() == null) {
            throw AppErrors.INSTANCE.missingField("locale").exception();
        }
    }

    protected void validateProhibitedFields(Email email) {
        if(email.getStatus() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("status").exception();
        }
        if(email.getStatusReason() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("statusReason").exception();
        }
        if(email.getSentDate() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("sentDate").exception();
        }
        if(email.getRetryCount() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("retryCount").exception();
        }
        if(email.getIsResend() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("isResend").exception();
        }
    }

    protected void validateScheduleDate(Email email, boolean required) {
        if(required && email.getScheduleDate()== null) {
            throw AppErrors.INSTANCE.missingField("scheduleDate").exception();
        }
        if(email.getScheduleDate() != null && email.getScheduleDate().before(new Date())) {
            throw AppErrors.INSTANCE.invalidField("scheduleDate").exception();
        }
    }

    protected void validateAuditDate(Model model) {
        if(model.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("createTime").exception();
        }
        if(model.getModifiedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("modifiedTime").exception();
        }
    }

    protected boolean validateEmailAddress(String email) {
        //TODO:need to update later.
        return true;
    }
}
