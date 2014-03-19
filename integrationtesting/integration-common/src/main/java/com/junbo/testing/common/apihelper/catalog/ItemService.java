/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.common.ResultList;

import java.util.HashMap;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Item related APIs
 */
public interface ItemService {

    Item getItem(String itemId, HashMap<String, String> httpPara) throws Exception;

    ResultList<Item> getItem(HashMap<String, String> httpPara) throws Exception;

    Item postItem(Item item) throws Exception;

    Item updateItem(String itemId, Item item) throws Exception;
}
