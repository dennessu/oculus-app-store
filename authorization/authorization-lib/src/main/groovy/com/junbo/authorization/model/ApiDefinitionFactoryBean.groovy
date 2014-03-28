/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.model

import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean

/**
 * !!!HACK
 * This is just a temporal solution for api definition.
 * TODO: add api endpoint at oauth. dynamically get the api definition from OAuth.
 * ApiDefinitionFactoryBean.
 */
@CompileStatic
class ApiDefinitionFactoryBean implements FactoryBean<ApiDefinition> {
    @Override
    ApiDefinition getObject() throws Exception {
        Scope scope = new Scope(claims: ['owner': ['read', 'owner'].toSet(),
                                         'guest': ['read'].toSet(),
                                         'admin': ['read', 'admin'].toSet()])

        return new ApiDefinition(availableScopes: ['identity': scope])
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
