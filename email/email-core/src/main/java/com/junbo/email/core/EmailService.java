/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core;


import com.junbo.email.spec.model.Email;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface of EmailService.
 */
@Transactional
public interface EmailService {
    Email send(Email email);
}
