/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.cache.CacheFacade;
import com.junbo.catalog.common.util.Callable;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Item repository.
 */
public class ItemRepositoryImpl extends CloudantClient<Item> implements ItemRepository {

    @Override
    public Item create(Item item) {
        Item createdItem = cloudantPostSync(item);
        CacheFacade.ITEM.put(createdItem.getItemId(), createdItem);
        return createdItem;
    }

    @Override
    public Item get(final String itemId) {
        if (itemId == null) {
            return null;
        }
        return CacheFacade.ITEM.get(itemId, new Callable<Item>() {
            @Override
            public Item execute() {
                return cloudantGetSync(itemId);
            }
        });
    }

    public List<Item> getItems(ItemsGetOptions options) {
        List<Item> items = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getItemIds())) {
            for (String itemId : options.getItemIds()) {
                Item item = get(itemId);
                if (item == null) {
                    continue;
                }else if (!StringUtils.isEmpty(options.getType()) && !options.getType().equals(item.getType())) {
                    continue;
                } else if (!StringUtils.isEmpty(options.getGenre())
                        &&(item.getGenres()==null || !item.getGenres().contains(options.getGenre()))) {
                    continue;
                } else if (options.getOwnerId() != null
                        && !options.getOwnerId().equals(item.getOwnerId())) {
                    continue;
                } else {
                    items.add(item);
                }
            }
            options.setTotal(Long.valueOf(items.size()));
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Item> searchResult =
                    searchSync("search", options.getQuery(), options.getValidSize(), options.getCursor());
            items = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else if (options.getGenre() != null
                || !StringUtils.isEmpty(options.getType())
                || options.getOwnerId() != null
                || !StringUtils.isEmpty(options.getPackageName())
                || options.getHostItemId() != null) {
            StringBuilder sb = new StringBuilder();
            if (options.getGenre() != null) {
                sb.append("genreId:\"").append(Utils.escaple(options.getGenre())).append("\"");
            }
            if (!StringUtils.isEmpty(options.getType())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("type:\"").append(Utils.escaple(options.getType())).append("\"");
            }
            if (!StringUtils.isEmpty(options.getOwnerId())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("ownerId:\"").append(options.getOwnerId().getValue()).append("\"");
            }
            if (!StringUtils.isEmpty(options.getPackageName())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("packageName:\"").append(Utils.escaple(options.getPackageName())).append("\"");
            }
            if (options.getHostItemId() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("hostItemId:\"").append(Utils.escaple(options.getHostItemId())).append("\"");
            }
            CloudantSearchResult<Item> searchResult =
                    searchSync("search", sb.toString(), options.getValidSize(), options.getCursor());
            items = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else {
            CloudantQueryResult queryResult = queryViewSync("by_itemId", null, options.getValidSize(), options.getValidStart(), false, true);
            items = Utils.getDocs(queryResult.getRows());
            options.setNextCursor(null);
            options.setTotal(queryResult.getTotalRows());
        }

        return items;
    }

    public List<Item> getItems(Collection<String> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.emptyList();
        }

        List<Item> items = new ArrayList<>();
        for (String itemId : itemIds) {
            Item item = cloudantGetSync(itemId);
            if (item != null) {
                items.add(item);
            }
        }

        return items;
    }

    @Override
    public Item update(Item item, Item oldItem) {
        Item updatedItem = cloudantPutSync(item, oldItem);
        CacheFacade.ITEM.put(item.getItemId(), updatedItem);

        return updatedItem;
    }

    @Override
    public void delete(String itemId) {
        cloudantDeleteSync(itemId);
        CacheFacade.ITEM.evict(itemId);
    }

}
