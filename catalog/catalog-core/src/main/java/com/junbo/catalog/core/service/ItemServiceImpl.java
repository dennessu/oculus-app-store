/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemDraftRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item service implementation.
 */
public class ItemServiceImpl extends BaseServiceImpl<Item> implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemDraftRepository itemDraftRepository;

    @Override
    public ItemRepository getEntityRepo() {
        return itemRepository;
    }

    @Override
    public ItemDraftRepository getEntityDraftRepo() {
        return itemDraftRepository;
    }

    @Override
    protected String getEntityType() {
        return "Item";
    }
}
