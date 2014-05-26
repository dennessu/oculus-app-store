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
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.ItemId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                } else {
                    items.add(item);
                }
            }
        } else if (!StringUtils.isEmpty(options.getType()) && options.getGenre() == null) {
            items = super.queryView("by_type", options.getType());
        } else if (options.getGenre() != null && StringUtils.isEmpty(options.getType())) {
            items = super.queryView("by_genreId", options.getGenre().toString());
        } else if (!StringUtils.isEmpty(options.getType()) && options.getGenre() != null) {
            // TODO: search
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
        view.setMap("function(doc) {emit(doc.type, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_type", view);

        view = new CloudantViews.CloudantView();
        view.setMap(
                "function(doc) {" +
                    "if (doc.genres) {" +
                        "for (var idx in doc.genres) {" +
                            "emit(doc.genres[idx], doc._id);" +
                        "}" +
                    "}" +
                "}");
        view.setResultClass(String.class);
        viewMap.put("by_genreId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'type\', doc.type);" +
                "index(\'genreId\', doc.genres);" +
                "index(\'itemId\', doc.itemId);" +
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
