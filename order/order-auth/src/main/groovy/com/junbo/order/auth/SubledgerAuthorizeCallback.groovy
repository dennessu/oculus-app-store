/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.order.spec.model.Subledger
import groovy.transform.CompileStatic

/**
 * SubledgerAuthorizeCallback.
 */
@CompileStatic
class SubledgerAuthorizeCallback extends AbstractAuthorizeCallback<Subledger> {
    @Override
    String getApiName() {
        return 'subledgers'
    }

    SubledgerAuthorizeCallback(SubledgerAuthorizeCallbackFactory factory, Subledger entity) {
        super(factory, entity)
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if (getEntity() != null) {
            if ("seller".equals(propertyPath)) {
                return getEntity().getSeller()
            }

            return super.getEntityIdByPropertyPath(propertyPath);
        }

        return null
    }
}
