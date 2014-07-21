/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.common.cloudant.CloudantClient;
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
        return cloudantPostSync(item);
    }

    @Override
    public Item get(String itemId) {
        if (itemId == null) {
            return null;
        }
        return cloudantGetSync(itemId);
    }

    public List<Item> getItems(ItemsGetOptions options) {
        List<Item> items = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getItemIds())) {
            for (String itemId : options.getItemIds()) {
                Item item = cloudantGetSync(itemId);
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
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Item> searchResult =
                    searchSync("search", options.getQuery(), options.getValidSize(), options.getBookmark());
            items = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else if (options.getGenre() != null
                || !StringUtils.isEmpty(options.getType())
                || options.getOwnerId() != null
                || !StringUtils.isEmpty(options.getPackageName())
                || options.getHostItemId() != null) {
            StringBuilder sb = new StringBuilder();
            if (options.getGenre() != null) {
                sb.append("genreId:'").append(options.getGenre().replace("'", "")).append("'");
            }
            if (!StringUtils.isEmpty(options.getType())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("type:'").append(options.getType().replace("'", "")).append("'");
            }
            if (!StringUtils.isEmpty(options.getOwnerId())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("ownerId:'").append(options.getOwnerId().getValue()).append("'");
            }
            if (!StringUtils.isEmpty(options.getPackageName())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("packageName:'").append(options.getPackageName().replace("'", "")).append("'");
            }
            if (options.getHostItemId() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("hostItemId:'").append(options.getHostItemId().replace("'", "")).append("'");
            }
            CloudantSearchResult<Item> searchResult =
                    searchSync("search", sb.toString(), options.getValidSize(), options.getBookmark());
            items = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else {
            items = queryViewSync("by_itemId", null, options.getValidSize(), options.getValidStart(), false);
            options.setNextBookmark(null);
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
        return cloudantPutSync(item, oldItem);
    }

    @Override
    public void delete(String itemId) {
        cloudantDeleteSync(itemId);
    }

}
