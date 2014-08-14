/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.email;

import com.junbo.common.id.EmailId;
import com.junbo.email.spec.model.Email;

/**
 * Created by Wei on 8/12/14.
 */
public interface EmailService {
    Email postEmail(Email email) throws Exception;
    Email postEmail(Email email, int expectedResponseCode) throws Exception;

    Email getEmail(EmailId id) throws Exception;
    Email getEmail(EmailId id, int expectedResponseCode) throws Exception;

    Email putEmail(EmailId id, Email email) throws Exception;
    Email putEmail(EmailId id, Email email, int expectedResponseCode) throws Exception;

    void deleteEmail(EmailId id) throws Exception;
    void deleteEmail(EmailId id, int expectedResponseCode) throws Exception;
}
