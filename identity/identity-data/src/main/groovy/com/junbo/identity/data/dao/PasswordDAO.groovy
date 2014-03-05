/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.password.PasswordRule

/**
 * Created by liangfu on 2/24/14.
 */
interface PasswordDAO {
    PasswordRule get(Long id)
    PasswordRule save(PasswordRule passwordRule)
    void delete(Long id)
    PasswordRule update(PasswordRule passwordRule)
}
