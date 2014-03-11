/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.mapper;

import com.junbo.common.id.EmailId;
import com.junbo.common.util.EnumRegistry;
import com.junbo.email.common.exception.AppExceptions;
import com.junbo.email.common.util.Utils;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.db.entity.EmailScheduleEntity;
import com.junbo.email.db.entity.EmailStatus;
import com.junbo.email.db.entity.EmailType;
import com.junbo.email.spec.model.Email;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * EmailMapper.
 */
@Component
public class EmailMapper {
    public Email toEmail(EmailHistoryEntity entity) {
        if(entity == null) {
            return null;
        }
        Email email =Utils.toObject(entity.getPayload(),Email.class);
        email.setSentDate(entity.getSentDate());
        email.setId(new EmailId(entity.getId()));
        email.setPriority(entity.getPriority());
        email.setRetryCount(entity.getRetryCount());

        email.setType(fromEmailType(entity.getType()));
        email.setStatus(fromEmailStatus(entity.getStatus()));
        email.setStatusReason(entity.getStatusReason());
        email.setIsResend(entity.getIsResend());

        return  email;
    }

    public EmailHistoryEntity toEmailHistoryEntity(Email email) {
        if(email == null) {
            return null;
        }
        EmailHistoryEntity entity = new EmailHistoryEntity();
        if(email.getUserId() != null) {
            entity.setUserId(email.getUserId().getValue());
        }
        if(email.getId() != null) {
            entity.setId(email.getId().getValue());
        }
        entity.setAction(email.getAction());
        entity.setSource(email.getSource());
        entity.setLocale(email.getLocale());
        entity.setPayload(Utils.toJson(email));
        entity.setPriority(email.getPriority());
        entity.setRecipient(Utils.toJson(email.getRecipients()));
        entity.setType(StringUtils.isEmpty(email.getType()) ? EmailType.COMMERCE.getId():toEmailType(email.getType()));
        entity.setStatus(toEmailStatus(email.getStatus()));

        return entity;
    }

    public EmailScheduleEntity toEmailScheduleEntity(Email email) {
        if(email == null) {
            return null;
        }
        return null;
    }

    public Email toEmail(EmailScheduleEntity entity) {
        if(entity == null) {
            return null;
        }
        Email email = Utils.toObject(entity.getPayload(),Email.class);
        email.setId(new EmailId(entity.getId()));
        email.setIsResend(false);
        email.setScheduleDate(entity.getScheduleDate());

        return email;
    }

    private Short toEmailType(String emailType) {
        if(!StringUtils.isEmpty(emailType)) {
            try {
                return EmailType.valueOf(EmailType.class,emailType).getId();
            }
            catch (Exception e) {
                throw AppExceptions.INSTANCE.invalidType().exception();
            }
        }
        return null;
    }

    private Short toEmailStatus(String emailStatus) {
        if(!StringUtils.isEmpty(emailStatus)) {
            try {
                return EmailStatus.valueOf(EmailStatus.class,emailStatus).getId();
            }
            catch (Exception e) {
                throw AppExceptions.INSTANCE.invalidStatus().exception();
            }
        }
        return null;
    }

    private String fromEmailType(Short id) {
        return EnumRegistry.resolve(id, EmailType.class).toString();
    }

    private String fromEmailStatus(Short id) {
        return EnumRegistry.resolve(id, EmailStatus.class).toString();

    }
}
