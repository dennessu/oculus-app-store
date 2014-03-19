/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.domaindata.impl;

import com.junbo.identity.data.repository.PasswordRuleRepository;
import com.junbo.identity.rest.service.domaindata.PasswordRuleService;
import com.junbo.identity.rest.service.validator.PasswordRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by haomin on 14-3-19.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PasswordRuleServiceImpl implements PasswordRuleService{
    @Autowired
    private PasswordRuleRepository passwordRuleRepository;

    @Autowired
    private PasswordRuleValidator passwordRuleValidator;


}
