/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core


import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailService.
 */
interface EmailService {
    Promise<Email> postEmail(Email request)

    Promise<Email> getEmail(Long id)

    Promise<Email> updateEmail(Long id, Email email)

    Void deleteEmail(Long id)

    Promise<Email> sendEmail(Email email)
}
