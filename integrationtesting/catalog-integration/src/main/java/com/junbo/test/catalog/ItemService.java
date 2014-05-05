/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Item related APIs
 */
public interface ItemService {

    Item getItem(Long itemId) throws Exception;
    Item getItem(Long itemId, int expectedResponseCode) throws Exception;

    Results<Item> getItems(HashMap<String, List<String>> httpPara) throws Exception;
    Results<Item> getItems(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    Item postDefaultItem(CatalogItemType itemType) throws Exception;
    Item prepareItemEntity(String fileName) throws Exception;
    Item postItem(Item item) throws Exception;
    Item postItem(Item item, int expectedResponseCode) throws Exception;

    Item updateItem(Item item) throws Exception;
    Item updateItem(Item item, int expectedResponseCode) throws Exception;

    void deleteItem(Long itemId) throws Exception;
    void deleteItem(Long itemId, int expectedResponseCode) throws Exception;

}
