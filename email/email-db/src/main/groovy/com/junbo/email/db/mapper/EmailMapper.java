/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.mapper;

import com.junbo.common.id.EmailId;
import com.junbo.common.util.EnumRegistry;
import com.junbo.email.common.util.Utils;
import com.junbo.email.db.entity.BaseEntity;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.db.entity.EmailScheduleEntity;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.spec.error.AppErrors;
import com.junbo.email.spec.model.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
        email.setStatus(fromEmailStatus(entity.getStatus()));
        email.setStatusReason(entity.getStatusReason());
        email.setIsResend(entity.getIsResend());
        email.setCreatedTime(entity.getCreatedTime());
        email.setModifiedTime(entity.getUpdatedTime());

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
        entity.setStatus(toEmailStatus(email.getStatus()));
        entity.setRecipients(Utils.toJson(email.getRecipients()));
        entity.setStatus(toEmailStatus(email.getStatus()));
        entity.setStatusReason(email.getStatusReason());
        entity.setSentDate(email.getSentDate());
        entity.setRetryCount(email.getRetryCount());
        entity.setIsResend(email.getIsResend());

        return entity;
    }

    public EmailScheduleEntity toEmailScheduleEntity(Email email) {
        if(email == null) {
            return null;
        }
        EmailScheduleEntity entity = new EmailScheduleEntity();
        if(email.getId() != null) {
            entity.setId(email.getId().getValue());
        }
        if(email.getUserId() != null) {
            entity.setUserId(email.getUserId().getValue());
        }
        entity.setSource(email.getSource());
        entity.setAction(email.getAction());
        entity.setLocale(email.getLocale());
        entity.setPayload(Utils.toJson(email));
        entity.setRecipients(Utils.toJson(email.getRecipients()));
        entity.setPriority(email.getPriority());
        entity.setScheduleTime(email.getScheduleTime());

        return entity;
    }

    public Email toEmailSchedule(EmailScheduleEntity entity) {
        if(entity == null) {
            return null;
        }
        Email email = Utils.toObject(entity.getPayload(),Email.class);
        email.setId(new EmailId(entity.getId()));
        email.setIsResend(false);
        email.setRecipients(Utils.toObject(entity.getRecipients(), List.class));
        email.setScheduleTime(entity.getScheduleTime());
        email.setCreatedTime(entity.getCreatedTime());
        email.setModifiedTime(entity.getUpdatedTime());

        return email;
    }

    public EmailTemplate toEmailTemplate(EmailTemplateEntity entity) {
        if(entity == null) {
            return null;
        }
        EmailTemplate template = toModel(entity, new EmailTemplate());
        template.setId(new EmailId(entity.getId()));
        template.setFromAddress(entity.getFromAddress());
        template.setFromName(entity.getFromName());
        template.setProviderIndex(entity.getProviderIndex());
        template.setProviderName(entity.getProviderName());
        template.setName(entity.getName());
        template.setSubject(entity.getSubject());
        if(entity.getVars() != null) {
            template.setListOfVariables(Utils.toObject(entity.getVars(), List.class));
        }

        return template;
    }

    public List<EmailTemplate> toEmailTemplates(List<EmailTemplateEntity> entities) {
        if(entities == null) {
            return null;
        }
        List<EmailTemplate> templates = new ArrayList<>();
        for(EmailTemplateEntity entity : entities) {
            templates.add(toEmailTemplate(entity));
        }
        return templates;
    }

    public EmailTemplateEntity toEmailTemplateEntity(EmailTemplate template) {
        EmailTemplateEntity entity = new EmailTemplateEntity();
        if(template.getId()!=null) {
            entity.setId(template.getId().getValue());
        }
        entity.setName(template.getName());
        entity.setSubject(template.getSubject());
        entity.setProviderIndex(template.getProviderIndex());
        entity.setProviderName(template.getProviderName());
        entity.setFromAddress(template.getFromAddress());
        entity.setFromName(template.getFromName());
        if(template.getListOfVariables() != null) {
            entity.setVars(Utils.toJson(template.getListOfVariables()));
        }

        return entity;
    }

    private Short toEmailStatus(String emailStatus) {
        if(!StringUtils.isEmpty(emailStatus)) {
            try {
                return EmailStatus.valueOf(EmailStatus.class, emailStatus).getId();
            }
            catch (Exception e) {
                throw AppErrors.INSTANCE.invalidStatus(emailStatus).exception();
            }
        }
        return null;
    }

    private String fromEmailStatus(Short id) {
        if ( id == null) {
            return null;
        }
        return EnumRegistry.resolve(id, EmailStatus.class).toString();

    }

    private <T extends Model> T toModel(BaseEntity entity, T model) {
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setModifiedBy(entity.getUpdatedBy());
        model.setModifiedTime(entity.getUpdatedTime());

        return model;
    }
}
