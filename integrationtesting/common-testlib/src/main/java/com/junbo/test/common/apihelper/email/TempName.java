/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.email;

import com.junbo.email.spec.model.Email;

/**
 @author Jason
  * Time: 5/14/2014
  * The interface for email related APIs
 */
public interface TempName {
    Email postEmail(Email email) throws Exception;
    Email postEmail(Email email, int expectedResponseCode) throws Exception;
}
