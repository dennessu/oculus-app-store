/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.SecurityQuestionResource;
import com.junbo.identity.spec.v1.model.domainData.SecurityQuestion;
import com.junbo.identity.spec.v1.model.options.DomainDataGetOption;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
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
