/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.catalog.spec.model.item.Item;
import groovy.transform.CompileStatic;

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class ItemAuthorizeCallback extends AbstractAuthorizeCallback<Item> {

    ItemAuthorizeCallback(AbstractAuthorizeCallbackFactory<Item> factory, Item entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "items";
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if ("owner".equals(propertyPath)) {
            return getEntity().getOwnerId();
        }

        return super.getEntityIdByPropertyPath(propertyPath);
    }
}
