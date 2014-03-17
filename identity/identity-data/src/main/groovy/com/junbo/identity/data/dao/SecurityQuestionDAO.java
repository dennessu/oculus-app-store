/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.spec.model.options.DomainDataGetOption;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface SecurityQuestionDAO {
    SecurityQuestionEntity save(SecurityQuestionEntity entity);

    SecurityQuestionEntity update(SecurityQuestionEntity entity);

    SecurityQuestionEntity get(Long id);

    List<SecurityQuestionEntity> search(DomainDataGetOption getOption);

    void delete(Long id);
}
