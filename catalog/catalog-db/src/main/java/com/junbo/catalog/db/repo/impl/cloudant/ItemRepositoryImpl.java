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
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.ItemId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Item repository.
 */
public class ItemRepositoryImpl extends CloudantClient<Item> implements ItemRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Item create(Item item) {
        if (item.getItemId() == null) {
            item.setItemId(idGenerator.nextId());
        }
        return super.cloudantPost(item);
    }

    @Override
    public Item get(Long itemId) {
        return super.cloudantGet(itemId.toString());
    }

    public List<Item> getItems(ItemsGetOptions options) {
        List<Item> items = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getItemIds())) {
            for (ItemId itemId : options.getItemIds()) {
                Item item = super.cloudantGet(itemId.toString());
                if (item == null) {
                    continue;
                }else if (!StringUtils.isEmpty(options.getType()) && !options.getType().equals(item.getType())) {
                    continue;
                } else if (options.getGenre() != null
                        &&(item.getGenres()==null || !item.getGenres().contains(options.getGenre().getValue()))) {
                    continue;
                } else if (options.getOwnerId() != null
                        && !options.getOwnerId().getValue().equals(item.getOwnerId())) {
                    continue;
                } else {
                    items.add(item);
                }
            }
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Item> searchResult =
                    super.search("search", options.getQuery(), options.getValidSize(), options.getBookmark());
            items = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else if (options.getGenre() != null
                || !StringUtils.isEmpty(options.getType())
                || options.getOwnerId() != null
                || options.getHostItemId() != null) {
            StringBuilder sb = new StringBuilder();
            if (options.getGenre() != null) {
                sb.append("genreId:'").append(options.getGenre().getValue()).append("'");
            }
            if (!StringUtils.isEmpty(options.getType())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("type:'").append(options.getType()).append("'");
            }
            if (!StringUtils.isEmpty(options.getOwnerId())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("ownerId:'").append(options.getOwnerId().getValue()).append("'");
            }
            if (options.getHostItemId() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("hostItemId:'").append(options.getHostItemId()).append("'");
            }
            CloudantSearchResult<Item> searchResult =
                    super.search("search", sb.toString(), options.getValidSize(), options.getBookmark());
            items = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else {
            items = super.queryView("by_itemId", null, options.getValidSize(), options.getValidStart(), false);
            options.setNextBookmark(null);
        }

        return items;
    }

    public List<Item> getItems(Collection<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.emptyList();
        }

        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            Item item = super.cloudantGet(itemId.toString());
            if (item != null) {
                items.add(item);
            }
        }

        return items;
    }

    @Override
    public Item update(Item item) {
        return super.cloudantPut(item);
    }

    @Override
    public void delete(Long itemId) {
        super.cloudantDelete(itemId.toString());
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.itemId, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_itemId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'type\', doc.type);" +
                "index(\'default\', doc.type);" +
                "if (doc.genres) {" +
                    "for (var genreIdx in doc.genres) {" +
                        "index(\'genreId\', doc.genres[genreIdx]);" +
                        "index(\'default\', doc.genres[genreIdx]);" +
                    "}" +
                "}" +
                "index(\'ownerId\', doc.ownerId);" +
                "index(\'default\', doc.ownerId);" +
                "index(\'itemId\', doc.itemId);" +
                "index(\'default\', doc.itemId);" +
                "if (doc.activeRevision) {" +
                    "index(\'revisionId\', doc.activeRevision.revisionId);" +
                    "index(\'default\', doc.activeRevision.revisionId);" +
                    "if (doc.activeRevision.sku) {" +
                        "index(\'sku\', doc.activeRevision.sku);" +
                        "index(\'default\', doc.activeRevision.sku);" +
                    "}" +
                    "if (doc.activeRevision.iapHostItemIds) {" +
                        "for (var iapIdx in doc.activeRevision.iapHostItemIds) {" +
                            "index(\'hostItemId\', doc.activeRevision.iapHostItemIds[iapIdx]);" +
                            "index(\'default\', doc.activeRevision.iapHostItemIds[iapIdx]);" +
                        "}" +
                     "}" +
                    "if (doc.activeRevision.gameModes) {" +
                        "for (var modeIdx in doc.activeRevision.gameModes) {" +
                            "index(\'gameMode\', doc.activeRevision.gameModes[modeIdx]);" +
                            "index(\'default\', doc.activeRevision.gameModes[modeIdx]);" +
                        "}" +
                    "}" +
                    "if (doc.activeRevision.platforms) {" +
                        "for (var platformIdx in doc.activeRevision.platforms) {" +
                            "index(\'platform\', doc.activeRevision.platforms[platformIdx]);" +
                            "index(\'default\', doc.activeRevision.platforms[platformIdx]);" +
                        "}" +
                    "}" +
                    "if (doc.activeRevision.locales) {" +
                        "for (var localeIdx in doc.activeRevision.locales) {" +
                            "var locale = doc.activeRevision.locales[localeIdx];" +
                            "if (locale.name) {" +
                                "index(\'name\', locale.name);" +
                                "index(\'default\', locale.name);" +
                            "}" +
                            "if (locale.revisionNotes) {" +
                                "index(\'revisionNotes\', locale.revisionNotes);" +
                                "index(\'default\', locale.revisionNotes);" +
                            "}" +
                            "if (locale.longDescription) {" +
                                "index(\'longDescription\', locale.longDescription);" +
                                "index(\'default\', locale.longDescription);" +
                            "}" +
                            "if (locale.shortDescription) {" +
                                "index(\'shortDescription\', locale.shortDescription);" +
                                "index(\'default\', locale.shortDescription);" +
                            "}" +
                        "}" +
                    "}" +
                "}" +
            "}");
        indexMap.put("search", index);

        setIndexes(indexMap);
        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
