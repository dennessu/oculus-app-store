/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import com.junbo.identity.spec.resource.SecurityQuestionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class SecurityQuestionResourceImpl implements SecurityQuestionResource {
    @Override
    public Promise<SecurityQuestion> create(SecurityQuestion securityQuestion) {
        return null;
    }

    @Override
    public Promise<SecurityQuestion> update(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        return null;
    }

    @Override
    public Promise<SecurityQuestion> patch(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        return null;
    }

    @Override
    public Promise<SecurityQuestion> get(SecurityQuestionId securityQuestionId) {
        return null;
    }

    @Override
    public Promise<ResultList<SecurityQuestion>> list(@BeanParam DomainDataGetOption option) {
        return null;
    }
}
