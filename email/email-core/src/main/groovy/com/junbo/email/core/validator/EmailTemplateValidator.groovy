/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator

import com.junbo.email.spec.model.EmailTemplate

/**
 * Interface of EmailTemplate validator.
 */
interface EmailTemplateValidator {
    void validateCreate(EmailTemplate template)

    void validateUpdate(EmailTemplate template)

    void validateDelete(Long id)

    //void validateGet(QueryParam queryParam)
}
