/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator;

import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Paging;

/**
 * Interface of EmailTemplate validator.
 */
public interface EmailTemplateValidator {
    void validateCreate(EmailTemplate template);

    void validateUpdate(EmailTemplate template);

    void validateDelete(Long id);

    void validateGet(Paging paging);
}
