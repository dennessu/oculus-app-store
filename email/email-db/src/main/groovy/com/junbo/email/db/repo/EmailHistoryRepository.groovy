/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo

import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailHistory Repository.
 */

interface EmailHistoryRepository {
    public Promise<Email> getEmailHistory(Long id)

    public Promise<Email> createEmailHistory(Email email)

    public Promise<Email> updateEmailHistory(Email email)
}
