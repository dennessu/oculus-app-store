/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email;

import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailTemplate;

/**
 * Created by Wei on 8/12/14.
 */
public interface EmailTemplateService {
    Results<EmailTemplate> getEmailTemplates(com.junbo.email.spec.model.QueryParam queryParam,boolean isAdminScope) throws Exception;
    Results<EmailTemplate> getEmailTemplates(com.junbo.email.spec.model.QueryParam queryParam, int expectedResponseCode,boolean isAdminScope) throws Exception;

    EmailTemplate postEmailTemplate(EmailTemplate template,boolean isAdminScope) throws Exception;
    EmailTemplate postEmailTemplate(EmailTemplate template, int expectedResponseCode,boolean isAdminScope) throws Exception;

    EmailTemplate getEmailTemplate(EmailTemplateId id,boolean isAdminScope) throws Exception;
    EmailTemplate getEmailTemplate(EmailTemplateId id, int expectedResponseCode,boolean isAdminScope) throws Exception;

    EmailTemplate putEmailTemplate(EmailTemplateId id, EmailTemplate template,boolean isAdminScope) throws Exception;
    EmailTemplate putEmailTemplate(EmailTemplateId id, EmailTemplate template, int expectedResponseCode,boolean isAdminScope) throws Exception;

    void deleteEmailTemplate(EmailTemplateId id,boolean isAdminScope) throws Exception;
    void deleteEmailTemplate(EmailTemplateId id, int expectedResponseCode,boolean isAdminScope) throws Exception;
}
