/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core;

import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Paging;
import com.junbo.email.spec.model.Results;
import com.junbo.langur.core.promise.Promise;

/**
 * Interface of EmailTemplateService.
 */
public interface EmailTemplateService {
    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template);

    Promise<EmailTemplate> getEmailTemplate(Long id);

    Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template);

    Void deleteEmailTemplate(Long id);

    Promise<Results<EmailTemplate>> getEmailTemplates(Paging paging);
}
