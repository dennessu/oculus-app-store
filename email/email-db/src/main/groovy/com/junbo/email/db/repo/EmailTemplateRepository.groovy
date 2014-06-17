/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo

import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailTemplate Repository.
 */
public interface EmailTemplateRepository {
    public Promise<EmailTemplate> getEmailTemplate(String id)

    public Promise<EmailTemplate> saveEmailTemplate(EmailTemplate template)

    public Promise<EmailTemplate> updateEmailTemplate(EmailTemplate template)

    public Promise<EmailTemplate> getEmailTemplateByName(String name)

    public Promise<List<EmailTemplate>> getEmailTemplates(Map<String, String> queries, Pagination pagination)

    public Promise<Void> deleteEmailTemplate(String id)
}