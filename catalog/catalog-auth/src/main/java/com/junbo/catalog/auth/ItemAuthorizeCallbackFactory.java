/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.resource.ItemResource;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Required;

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
public class ItemAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Item> {
    private ItemResource itemResource;

    @Required
    public void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource;
    }

    @Override
    public AuthorizeCallback<Item> create(Item entity) {
        return new ItemAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<Item> create(String itemId) {
        Item item = itemResource.getItem(itemId).get();
        return create(item);
    }
}
