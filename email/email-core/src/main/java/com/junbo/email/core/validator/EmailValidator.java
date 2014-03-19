/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator;

import com.junbo.email.spec.model.Email;

/**
 * Interface of Email.
 */
public interface EmailValidator {
    void validateCreate(Email email);

    void validateUpdate(Email email);

    void validateDelete(Long id);
}
