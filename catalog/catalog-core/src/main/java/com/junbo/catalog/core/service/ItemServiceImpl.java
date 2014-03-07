/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.exception.CatalogException;
import com.junbo.catalog.common.exception.NotFoundException;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.db.repo.ItemDraftRepository;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    public Item createItem(Item item) {
        if (item == null) {
            throw new CatalogException("TODO");
        }

        item.setRevision(Constants.INITIAL_CREATION_REVISION);
        item.setStatus(Status.DESIGN);

        Long itemId = itemDraftRepository.create(item);

        return itemDraftRepository.get(itemId);
    }

    @Override
    public List<Item> getItems(EntitiesGetOptions options) {
        if (CollectionUtils.isEmpty(options.getEntityIds())) {
            List<Item> items = new ArrayList<>();

            for (Long itemId : options.getEntityIds()) {
                Item item;
                if (Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                    item = itemRepository.get(itemId, options.getTimestamp());
                } else {
                    item = itemDraftRepository.get(itemId);
                }

                if (item != null) {
                    items.add(item);
                }
            }
            return items;
        } else {
            options.ensurePagingValid();
            List<Item> draftOffers = itemDraftRepository.getItems(options.getStart(), options.getSize());
            if (!Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                return draftOffers;
            }

            List<Item> items = new ArrayList<>();
            for (Item draftOffer : draftOffers) {
                Item item = itemRepository.get(draftOffer.getId(), options.getTimestamp());
                if (item != null) {
                    items.add(item);
                }
            }

            return items;
        }
    }

    @Override
    public Item updateItem(Item item) {
        if (item == null) {
            throw new CatalogException("TODO");
        }

        itemDraftRepository.update(item);
        return itemDraftRepository.get(item.getId());
    }

    @Override
    public Item reviewItem(Long itemId) {
        Item item = itemDraftRepository.get(itemId);
        checkItemNotNull(itemId, item);
        item.setStatus(Status.PENDING_REVIEW);
        itemDraftRepository.update(item);
        return itemRepository.get(itemId, null);
    }

    @Override
    public Item releaseItem(Long itemId) {
        Item item = itemDraftRepository.get(itemId);
        checkItemNotNull(itemId, item);
        item.setStatus(Status.RELEASED);
        itemRepository.create(item);
        itemDraftRepository.update(item);
        return itemRepository.get(itemId, null);
    }

    @Override
    public Item rejectItem(Long itemId) {
        Item item = itemDraftRepository.get(itemId);
        checkItemNotNull(itemId, item);
        item.setStatus(Status.REJECTED);
        itemDraftRepository.update(item);
        return itemRepository.get(itemId, null);
    }

    @Override
    public Long removeItem(Long itemId) {
        Item item = itemRepository.get(itemId, null);
        checkItemNotNull(itemId, item);
        item.setStatus(Status.DELETED);
        itemRepository.create(item);
        return itemId;
    }

    @Override
    public Long deleteItem(Long itemId) {
        Item item = itemRepository.get(itemId, null);
        checkItemNotNull(itemId, item);
        item.setStatus(Status.DELETED);
        itemRepository.create(item);
        itemDraftRepository.update(item);
        return itemId;
    }

    private void checkItemNotNull(Long itemId, Item item) {
        if (item == null) {
            throw new NotFoundException("offer", itemId);
        }
    }
}
