/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailSearchOption
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Impl of EmailValidator.
 */
@CompileStatic
@Component
class EmailValidatorImpl extends CommonValidator implements EmailValidator {

    private EmailScheduleRepository emailScheduleRepository

    void setEmailScheduleRepository(EmailScheduleRepository emailScheduleRepository) {
        this.emailScheduleRepository = emailScheduleRepository
    }

    @Override
    void validateCreate(Email email) {
        this.validateEmailId(email)
        this.validateCommonField(email)
        this.validateProhibitedFields(email)
        super.validateScheduleTime(email, false)
        super.validateEmailTemplate(email)
    }

    @Override
    void validateUpdate(Email email) {
        this.validateCommonField(email)
        this.validateProhibitedFields(email)
        super.validateScheduleTime(email, true)
        super.validateAuditDate(email)
        super.validateEmailTemplate(email)
        this.validateEmailSchedule(email)
    }

    @Override
    void validateDelete(String id) {
        if (id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }
        if (emailScheduleRepository.getEmailSchedule(id).get() == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(id).exception()
        }
    }

    @Override
    void validateSearch(EmailSearchOption option) {
        if (option == null || option.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }
    }

    private void validateEmailSchedule(Email email) {
        Email schedule = emailScheduleRepository.getEmailSchedule(email.getId().value).get()
        if (schedule == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(email.getId().getValue()).exception()
        }
    }

    private void validateEmailId(Email email) {
        if (email.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('self').exception()
        }
    }

    private void validateCommonField(Email email) {
        if (email == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        if (email.userId == null && email.recipients == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('recipients').exception()
        }
        if (email.recipients != null) {
            for (String recipient : email.recipients) {
                if (!super.validateEmailAddress(recipient)) {
                   throw AppCommonErrors.INSTANCE.fieldInvalid('recipients').exception()
                }
            }
        }
        if (email.templateId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('template').exception()
        }
    }

    private void validateProhibitedFields(Email email) {
        if (!StringUtils.isEmpty(email.status)) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('status').exception()
        }
        if (!StringUtils.isEmpty(email.statusReason)) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('statusReason').exception()
        }
        if (email.sentTime != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('sentTime').exception()
        }
        if (email.retryCount != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('retryCount').exception()
        }
        if (email.isResend != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('isResend').exception()
        }
    }
}
