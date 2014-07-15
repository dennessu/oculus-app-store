/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.OwnerCallback;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.*;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by Zhanxin on 5/23/2014.
 */
public class ItemOwnerCallback implements OwnerCallback {
    private ItemResource itemResource;

    @Required
    public void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource;
    }

    @Override
    public UserId getUserOwnerId(UniversalId resourceId) {
        return null;
    }

    @Override
    public OrganizationId getOrganizationOwnerId(UniversalId resourceId) {
        assert resourceId instanceof ItemId : "resourceId is not an ItemId";
        Item item = itemResource.getItem(((ItemId) resourceId).getValue()).syncGet();
        return item.getOwnerId();
    }
}
