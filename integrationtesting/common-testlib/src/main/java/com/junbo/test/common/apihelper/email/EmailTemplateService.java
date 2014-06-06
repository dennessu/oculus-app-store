/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.email;

import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.common.model.Results;

/**
 @author Jason
  * Time: 5/14/2014
  * The interface for email template related APIs
 */
public interface EmailTemplateService {
    Results<EmailTemplate> getEmailTemplates(String source, String action, String locale) throws Exception;
    Results<EmailTemplate> getEmailTemplates(String source, String action, String locale, int expectedResponseCode) throws Exception;
}
