/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo

import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise

/**
 * Interface of EmailScheduleRepository.
 */
interface EmailScheduleRepository {
    public Promise<Email> getEmailSchedule(String id)

    public Promise<Email> saveEmailSchedule(Email email)

    public Promise<Email> updateEmailSchedule(Email email)

    public Promise<Void> deleteEmailSchedule(String id)
}
