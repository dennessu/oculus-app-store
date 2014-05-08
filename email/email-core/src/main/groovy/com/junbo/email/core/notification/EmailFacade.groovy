/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.notification

import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailFacade.
 */
interface EmailFacade {
    Promise<Email> sendEmail(Email email)
}
