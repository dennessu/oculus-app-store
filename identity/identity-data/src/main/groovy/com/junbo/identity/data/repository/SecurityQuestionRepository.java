/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface SecurityQuestionRepository {

    SecurityQuestion save(SecurityQuestion entity);

    SecurityQuestion update(SecurityQuestion entity);

    SecurityQuestion get(SecurityQuestionId id);

    void delete(SecurityQuestionId id);

    List<SecurityQuestion> search(SecurityQuestionListOptions getOption);
}
