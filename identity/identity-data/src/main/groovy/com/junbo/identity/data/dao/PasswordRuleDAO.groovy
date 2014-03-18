/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.password.PasswordRuleEntity
import com.junbo.sharding.annotations.SeedParam


/**
 * Created by liangfu on 2/24/14.
 */
interface PasswordRuleDAO {
    PasswordRuleEntity get(@SeedParam Long id)
    PasswordRuleEntity save(PasswordRuleEntity passwordRule)
    void delete(@SeedParam Long id)
    PasswordRuleEntity update(PasswordRuleEntity passwordRule)
}
