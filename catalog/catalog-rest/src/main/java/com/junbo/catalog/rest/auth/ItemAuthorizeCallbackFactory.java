/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.catalog.spec.model.item.Item;
import groovy.transform.CompileStatic;

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
public class ItemAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Item> {

    @Override
    public AuthorizeCallback<Item> create(String apiName, Item entity) {

        return new ItemAuthorizeCallback(this, apiName, entity);
    }
}
