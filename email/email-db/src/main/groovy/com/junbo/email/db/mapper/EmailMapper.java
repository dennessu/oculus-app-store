/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.mapper;

import com.google.common.base.Joiner;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.EmailId;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.util.EnumRegistry;
import com.junbo.email.common.util.Utils;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.db.entity.EmailScheduleEntity;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailStatus;
import com.junbo.email.spec.model.EmailTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailMapper.
 */
@Component
public class EmailMapper {
    public Email toEmail(EmailHistoryEntity entity) throws IOException {
        if(entity == null) {
            return null;
        }
        Email email = Utils.toObject(entity.getPayload(),Email.class);
        email.setSentTime(entity.getSentTime());
        email.setId(new EmailId(entity.getId().toString()));
        email.setPriority(entity.getPriority());
        email.setRetryCount(entity.getRetryCount());
        email.setStatus(fromEmailStatus(entity.getStatus()));
        email.setStatusReason(entity.getStatusReason());
        email.setIsResend(entity.getIsResend());
        email.setCreatedTime(entity.getCreatedTime());
        email.setUpdatedTime(entity.getUpdatedTime());

        return  email;
    }

    public EmailHistoryEntity toEmailHistoryEntity(Email email) throws IOException {
        if(email == null) {
            return null;
        }
        EmailHistoryEntity entity = new EmailHistoryEntity();
        if(email.getUserId() != null) {
            entity.setUserId(email.getUserId().getValue());
        }
        if(email.getId() != null) {
            entity.setId(email.getId().asLong());
        }
        entity.setTemplateId(email.getTemplateId().asLong());
        entity.setPayload(Utils.toJson(email));
        entity.setPriority(email.getPriority());
        entity.setStatus(toEmailStatus(email.getStatus()));
        entity.setRecipients(Utils.toJson(email.getRecipients()));
        entity.setStatusReason(email.getStatusReason());
        entity.setSentTime(email.getSentTime());
        entity.setRetryCount(email.getRetryCount());
        entity.setIsResend(email.getIsResend());

        return entity;
    }

    public EmailScheduleEntity toEmailScheduleEntity(Email email) throws IOException {
        if(email == null) {
            return null;
        }
        EmailScheduleEntity entity = new EmailScheduleEntity();
        if(email.getId() != null) {
            entity.setId(email.getId().asLong());
        }
        if(email.getUserId() != null) {
            entity.setUserId(email.getUserId().getValue());
        }
        entity.setTemplateId(email.getTemplateId().asLong());
        entity.setPayload(Utils.toJson(email));
        entity.setRecipients(Utils.toJson(email.getRecipients()));
        entity.setPriority(email.getPriority());
        entity.setScheduleTime(email.getScheduleTime());

        return entity;
    }

    public Email toEmailSchedule(EmailScheduleEntity entity) throws IOException {
        if(entity == null) {
            return null;
        }
        Email email = Utils.toObject(entity.getPayload(), Email.class);
        email.setId(new EmailId(entity.getId().toString()));
        email.setIsResend(false);
        email.setTemplateId(new EmailTemplateId(entity.getTemplateId().toString()));
        email.setStatus(EmailStatus.PENDING.toString());
        email.setRecipients(Utils.toObject(entity.getRecipients(), List.class));
        email.setScheduleTime(entity.getScheduleTime());
        email.setCreatedTime(entity.getCreatedTime());
        email.setUpdatedTime(entity.getUpdatedTime());

        return email;
    }

    public EmailTemplate toEmailTemplate(EmailTemplateEntity entity) throws IOException {
        if(entity == null) {
            return null;
        }
        EmailTemplate template = new EmailTemplate();
        template.setSource(entity.getSource());
        template.setAction(entity.getAction());
        template.setLocale(entity.getLocale());
        template.setId(new EmailTemplateId(entity.getId().toString()));
        template.setFromAddress(entity.getFromAddress());
        template.setFromName(entity.getFromName());
        template.setProviderIndex(entity.getProviderIndex());
        template.setProviderName(entity.getProviderName());
        template.setName(entity.getName());
        template.setSubject(entity.getSubject());
        if(!StringUtils.isEmpty(entity.getPlaceholderNames())) {
            template.setPlaceholderNames(Utils.toObject(entity.getPlaceholderNames(), List.class));
        }

        return template;
    }

    public List<EmailTemplate> toEmailTemplates(List<EmailTemplateEntity> entities) throws IOException {
        if(entities == null) {
            return null;
        }
        List<EmailTemplate> templates = new ArrayList<>();
        for(EmailTemplateEntity entity : entities) {
            templates.add(toEmailTemplate(entity));
        }
        return templates;
    }

    public EmailTemplateEntity toEmailTemplateEntity(EmailTemplate template) throws IOException {
        EmailTemplateEntity entity = new EmailTemplateEntity();
        if(template.getId()!=null) {
            entity.setId(template.getId().asLong());
        }
        entity.setName(template.getName());
        entity.setSource(template.getSource());
        entity.setAction(template.getAction());
        entity.setLocale(template.getLocale());
        entity.setSubject(template.getSubject());
        entity.setProviderIndex(template.getProviderIndex());
        entity.setProviderName(template.getProviderName());
        entity.setFromAddress(template.getFromAddress());
        entity.setFromName(template.getFromName());
        if(template.getPlaceholderNames() != null) {
            entity.setPlaceholderNames(Utils.toJson(template.getPlaceholderNames()));
        }

        return entity;
    }

    private Short toEmailStatus(String emailStatus) {
        if(!StringUtils.isEmpty(emailStatus)) {
            try {
                return EmailStatus.valueOf(EmailStatus.class, emailStatus).getId();
            }
            catch (Exception e) {
                List<String> validStatus = new ArrayList<>();
                for (EmailStatus status : EmailStatus.values()) {
                    validStatus.add(status.toString());
                }
                throw AppCommonErrors.INSTANCE.fieldInvalid("status", Joiner.on(", ").join(validStatus)).exception();
            }
        }
        return null;
    }

    private String fromEmailStatus(Short id) {
        if (id == null) {
            return null;
        }
        return EnumRegistry.resolve(id, EmailStatus.class).toString();

    }
}
