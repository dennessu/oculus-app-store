/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.Model
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Common Validator.
 */
@Component('emailCommonValidator')
class CommonValidator {
    @Autowired
    protected EmailTemplateRepository emailTemplateRepository

    protected void validateUser(User user) {
        if (user == null) {
            throw AppErrors.INSTANCE.invalidUserId('').exception()
        }
        if (!user.active) {
            throw AppErrors.INSTANCE.invalidUserStatus('').exception()
        }
    }

    protected void validateUserPii(UserPii userPii) {
        if ( userPii?.emails?.keySet()?.first() == null) {
            throw AppErrors.INSTANCE.emptyUserEmail().exception()
        }
    }

    protected void validateCommonField(Email email) {
        if (email == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception()
        }
        if (email.userId == null && email.recipients == null) {
            throw AppErrors.INSTANCE.missingField('user or recipients').exception()
        }
        if (StringUtils.isEmpty(email.source)) {
            throw AppErrors.INSTANCE.missingField('source').exception()
        }
        if (StringUtils.isEmpty(email.action)) {
            throw AppErrors.INSTANCE.missingField('action').exception()
        }
        if (StringUtils.isEmpty(email.locale) && email.userId == null) {
            throw AppErrors.INSTANCE.missingField('locale').exception()
        }
    }

    protected void validateProhibitedFields(Email email) {
        if (email.status != null) {
            throw AppErrors.INSTANCE.unnecessaryField('status').exception()
        }
        if (email.statusReason != null) {
            throw AppErrors.INSTANCE.unnecessaryField('statusReason').exception()
        }
        if (email.sentDate != null) {
            throw AppErrors.INSTANCE.unnecessaryField('sentDate').exception()
        }
        if (email.retryCount != null) {
            throw AppErrors.INSTANCE.unnecessaryField('retryCount').exception()
        }
        if (email.isResend != null) {
            throw AppErrors.INSTANCE.unnecessaryField('isResend').exception()
        }
    }

    protected void validateScheduleTime(Email email, boolean required) {
        if (required && email.scheduleTime == null) {
            throw AppErrors.INSTANCE.missingField('scheduleTime').exception()
        }
        if (email.scheduleTime != null && email.scheduleTime.before(new Date())) {
            throw AppErrors.INSTANCE.invalidField('scheduleTime').exception()
        }
    }

    protected void validateAuditDate(Model model) {
        if (model.createdTime != null) {
            throw AppErrors.INSTANCE.unnecessaryField('createTime').exception()
        }
        if (model.modifiedTime != null) {
            throw AppErrors.INSTANCE.unnecessaryField('modifiedTime').exception()
        }
    }

    protected boolean validateEmailAddress(String email) {
        //TODO:need to update later.
        if (email == null) {
            return false
        }
        return true
    }
}
