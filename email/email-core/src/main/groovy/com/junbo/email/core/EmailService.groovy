/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core

import com.junbo.common.model.Results
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailSearchOption
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailService.
 */
interface EmailService {
    Promise<Email> postEmail(Email request)

    Promise<Email> getEmail(String id)

    Promise<Email> updateEmail(String id, Email email)

    Promise<Void> deleteEmail(String id)

    Promise<Results<Email>> searchEmail(EmailSearchOption option)
}
