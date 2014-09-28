/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Item related APIs
 */
public interface ItemService {

    Item getItem(String itemId) throws Exception;
    Item getItem(String itemId, int expectedResponseCode) throws Exception;

    Results<Item> getItems(HashMap<String, List<String>> httpPara) throws Exception;
    Results<Item> getItems(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    Item postDefaultItem(CatalogItemType itemType) throws Exception;
    Item postDefaultItem(CatalogItemType itemType, OrganizationId organizationId) throws Exception;
    Item prepareItemEntity(String fileName) throws Exception;
    Item prepareItemEntity(String fileName, OrganizationId organizationId) throws Exception;
    Item postItem(Item item) throws Exception;
    Item postItem(Item item, int expectedResponseCode) throws Exception;
    Item postItem(Item item, int expectedResponseCode, boolean isAdmin) throws Exception;

    Item updateItem(String itemId, Item item) throws Exception;
    Item updateItem(String itemId, Item item, int expectedResponseCode) throws Exception;

    void deleteItem(String itemId) throws Exception;
    void deleteItem(String itemId, int expectedResponseCode) throws Exception;

}
