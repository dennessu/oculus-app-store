/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.model.ApiDefinition
import com.junbo.authorization.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean

/**
 * ApiDefinitionFactoryBean.
 */
@CompileStatic
class ApiDefinitionFactoryBean implements FactoryBean<ApiDefinition> {
    @Override
    ApiDefinition getObject() throws Exception {
        Scope scope = new Scope(claims: ['owner': ['read', 'owner'].toSet(),
                                         'guest': ['read'].toSet(),
                                         'admin': ['read', 'admin'].toSet()])

        return new ApiDefinition(availableScopes: ['entity': scope])
    }

    @Override
    Class<?> getObjectType() {
        return ApiDefinition
    }

    @Override
    boolean isSingleton() {
        return false
    }
}
