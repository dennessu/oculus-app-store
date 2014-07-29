/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;

import java.util.HashSet;
import java.util.Set;

/**
 * Mocked definition facade.
 */
public class MockItemImpl implements ItemFacade {
    @Override
    public ItemRevision getItemRevision(String itemRevisionId) {
        return new ItemRevision();
    }

    @Override
    public ItemRevision getItem(String itemId) {
        return new ItemRevision();
    }

    @Override
    public Set<String> getItemIdsByHostItemId(String hostItemId) {
        return new HashSet<>();
    }
}
