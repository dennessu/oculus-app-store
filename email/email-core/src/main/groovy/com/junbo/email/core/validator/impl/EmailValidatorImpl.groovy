/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.common.id.EmailId
import com.junbo.email.common.util.IdUtils
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.v1.model.User
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
    void validateDelete(Long id) {
        if (id == null) {
            throw AppErrors.INSTANCE.missingField('id').exception()
        }
        if (emailScheduleRepository.getEmailSchedule(id) == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(IdUtils.encodeEmailId(id)).exception()
        }
    }

    private void validateEmailSchedule(Email email) {
        Email schedule = emailScheduleRepository.getEmailSchedule(email.id.value)
        if (schedule == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(IdUtils.encodeId(email.id)).exception()
        }
    }

    private void validateEmailId(Email email) {
        if (email.id != null) {
            throw AppErrors.INSTANCE.unnecessaryField('self').exception()
        }
    }

    private void validateCommonField(Email email) {
        if (email == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception()
        }
        if (email.userId == null && email.recipients == null) {
            throw AppErrors.INSTANCE.missingField('recipients').exception()
        }
        if (email.recipients != null) {
            for (String recipient : email.recipients) {
                if (!super.validateEmailAddress(recipient)) {
                   throw AppErrors.INSTANCE.invalidField('recipients').exception()
                }
            }
        }
        if (email.templateId == null) {
            throw AppErrors.INSTANCE.missingField('template').exception()
        }
    }

    private void validateProhibitedFields(Email email) {
        if (!StringUtils.isEmpty(email.status)) {
            throw AppErrors.INSTANCE.unnecessaryField('status').exception()
        }
        if (!StringUtils.isEmpty(email.statusReason)) {
            throw AppErrors.INSTANCE.unnecessaryField('statusReason').exception()
        }
        if (email.sentTime != null) {
            throw AppErrors.INSTANCE.unnecessaryField('sentTime').exception()
        }
        if (email.retryCount != null) {
            throw AppErrors.INSTANCE.unnecessaryField('retryCount').exception()
        }
        if (email.isResend != null) {
            throw AppErrors.INSTANCE.unnecessaryField('isResend').exception()
        }
    }
}
