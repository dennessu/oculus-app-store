/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.SecurityQuestionId
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
/**
 * Created by liangfu on 3/16/14.
 */
interface SecurityQuestionRepository extends IdentityBaseRepository<SecurityQuestion, SecurityQuestionId> {
    Promise<List<SecurityQuestion>> search(SecurityQuestionListOptions getOption)
}
