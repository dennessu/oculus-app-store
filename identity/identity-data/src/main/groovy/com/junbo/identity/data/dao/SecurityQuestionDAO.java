/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface SecurityQuestionDAO {
    SecurityQuestionEntity save(SecurityQuestionEntity entity);

    SecurityQuestionEntity update(SecurityQuestionEntity entity);

    SecurityQuestionEntity get(@SeedParam Long id);

    void delete(@SeedParam Long id);

    // Todo:    Need to build reverse lookup table
    List<SecurityQuestionEntity> search(DomainDataGetOption getOption);

}
