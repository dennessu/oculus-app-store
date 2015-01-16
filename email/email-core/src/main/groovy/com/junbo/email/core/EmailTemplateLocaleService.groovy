/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise

/**
 * Created by dell on 1/16/2015.
 */
public interface EmailTemplateLocaleService {
    // It will first get user's preferredLocale, then it will fetch current application's locale, then it will fetch input locale.
    Promise<String> getEmailTemplateLocale(String locale, UserId userId)
}
