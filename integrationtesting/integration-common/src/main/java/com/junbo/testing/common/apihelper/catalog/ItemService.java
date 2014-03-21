/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog;

import com.junbo.catalog.spec.model.item.Item;
import java.util.HashMap;
import java.util.List;

    /**
     @author Jason
      * Time: 3/14/2014
      * The interface for Item related APIs
     */
public interface ItemService {

    String getItem(String itemId, HashMap<String, String> httpPara) throws Exception;
    String getItem(String itemId, HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    List<String> getItem(HashMap<String, String> httpPara) throws Exception;
    List<String> getItem(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultItem(boolean isDigital) throws Exception;
    String postItem(Item item) throws Exception;
    String postItem(Item item, int expectedResponseCode) throws Exception;

    String updateItem(String itemId, Item item) throws Exception;
    String updateItem(String itemId, Item item, int expectedResponseCode) throws Exception;
}
