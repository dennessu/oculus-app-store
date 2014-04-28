/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core

import com.junbo.common.model.Results
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.email.spec.model.QueryParam
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailTemplateService.
 */
interface EmailTemplateService {
    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template)

    Promise<EmailTemplate> getEmailTemplate(Long id)

    Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template)

    Void deleteEmailTemplate(Long id)

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam, Pagination pagination)
}
