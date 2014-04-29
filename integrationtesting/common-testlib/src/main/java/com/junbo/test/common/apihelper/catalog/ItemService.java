/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.EnumHelper;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Item related APIs
 */
public interface ItemService {

    String getItem(String itemId) throws Exception;
    String getItem(String itemId, int expectedResponseCode) throws Exception;

    List<String> getItems(HashMap<String, String> httpPara) throws Exception;
    List<String> getItems(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultItem(EnumHelper.CatalogItemType itemType) throws Exception;
    Item prepareItemEntity(String fileName) throws Exception;
    String postItem(Item item) throws Exception;
    String postItem(Item item, int expectedResponseCode) throws Exception;

    String updateItem(Item item) throws Exception;
    String updateItem(Item item, int expectedResponseCode) throws Exception;
}
