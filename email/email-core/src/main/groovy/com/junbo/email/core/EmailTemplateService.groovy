/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core

import com.junbo.common.model.Results
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailTemplateService.
 */
interface EmailTemplateService {
    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template)

    Promise<EmailTemplate> getEmailTemplate(String id)

    Promise<EmailTemplate> putEmailTemplate(String id, EmailTemplate template)

    Promise<Void> deleteEmailTemplate(String id)

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam)
}
