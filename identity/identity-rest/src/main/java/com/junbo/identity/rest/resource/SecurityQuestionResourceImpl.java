/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.core.service.domaindata.SecurityQuestionService;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.entity.SecurityQuestionGetOptions;
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions;
import com.junbo.identity.spec.resource.SecurityQuestionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class SecurityQuestionResourceImpl implements SecurityQuestionResource {
    @Autowired
    private SecurityQuestionService securityQuestionService;

    @Override
    public Promise<SecurityQuestion> create(SecurityQuestion securityQuestion) {
        return Promise.pure(securityQuestionService.create(securityQuestion));
    }

    @Override
    public Promise<SecurityQuestion> put(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        return Promise.pure(securityQuestionService.update(securityQuestionId, securityQuestion));
    }

    @Override
    public Promise<SecurityQuestion> patch(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        return Promise.pure(securityQuestionService.patch(securityQuestionId, securityQuestion));
    }

    @Override
    public Promise<SecurityQuestion> get(SecurityQuestionId securityQuestionId,
                                         @BeanParam SecurityQuestionGetOptions getOptions) {
        return Promise.pure(securityQuestionService.get(securityQuestionId));
    }

    @Override
    public Promise<List<SecurityQuestion>> list(@BeanParam SecurityQuestionListOptions listOptions) {
        return Promise.pure(securityQuestionService.search(listOptions));
    }
}
