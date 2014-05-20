/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.common.util;

import com.junbo.common.id.EmailId;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.id.Id;
import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;

/**
 * IdUtils.
 */
public class IdUtils {
    private IdUtils() {}

    public static String encodeId(Id id) {
        return IdFormatter.encodeId(id);
    }

    public static String encodeEmailId(Long id) {
        EmailId emailId = new EmailId(id);
        return IdFormatter.encodeId(emailId);
    }

    public static String encodeEmailTemplateId(Long id) {
        EmailTemplateId templateId = new EmailTemplateId(id);
        return IdFormatter.encodeId(templateId);
    }

    public static String encodeUserId(Long id) {
        UserId userId = new UserId(id);
        return IdFormatter.encodeId(userId);
    }
}
