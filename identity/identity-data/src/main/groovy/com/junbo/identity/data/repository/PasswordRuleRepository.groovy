/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.PasswordRuleId
import com.junbo.identity.spec.model.password.PasswordRule

/**
 * Created by liangfu on 2/24/14.
 */
interface PasswordRuleRepository {
    PasswordRule get(PasswordRuleId id)
    PasswordRule save(PasswordRule passwordRule)
    void delete(PasswordRuleId id)
    PasswordRule update(PasswordRule passwordRule)
}
