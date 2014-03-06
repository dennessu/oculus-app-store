/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemDraftRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Item service implementation.
 */
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDraftRepository itemDraftRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Override
    public Item getItem(Long itemId, EntityGetOptions options) {
        return itemDraftRepository.get(itemId);
    }

    @Override
    public List<Item> getItems(int start, int size) {
        return itemDraftRepository.getItems(start, size);
    }

    @Override
    public Item createItem(Item item) {
        // TODO: validations

        item.setRevision(Constants.INITIAL_CREATION_REVISION);
        item.setStatus(Status.DESIGN);

        Long itemId = itemDraftRepository.create(item);
        item.setId(itemId);
        itemRepository.create(item);

        return itemDraftRepository.get(itemId);
    }
}
