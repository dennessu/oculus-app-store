/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.password;

import com.junbo.identity.spec.model.password.PasswordRule;

/**
 * Interface for password Service.
 * Please refer to this page:
 * http://howtodoinjava.com/2013/07/22/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 * We will use much more security methods to protect our code.
 */
public interface PasswordService {
    void validatePassword(String password);

    PasswordRule save(PasswordRule passwordRule);
    void delete(Long id);
    PasswordRule get(Long id);
    PasswordRule update(PasswordRule passwordRule);
}

