/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;

/**
 * Mocked definition facade.
 */
public class MockItemImpl implements ItemFacade {
    @Override
    public ItemRevision getItem(Long itemId) {
        return new ItemRevision();
    }
}
