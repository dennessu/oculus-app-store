/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 4/17/2014
  * The interface for Item revision related APIs
 */
public interface ItemRevisionService {

    ItemRevision getItemRevision(Long revisionId) throws Exception;
    ItemRevision getItemRevision(Long revisionId, int expectedResponseCode) throws Exception;

    Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara) throws Exception;
    Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    ItemRevision prepareItemRevisionEntity(String fileName) throws Exception;

    ItemRevision postDefaultItemRevision() throws Exception;
    ItemRevision postDefaultItemRevision(Item item) throws Exception;
    ItemRevision postItemRevision(ItemRevision itemRevision) throws Exception;
    ItemRevision postItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception;

    ItemRevision updateItemRevision(Long itemRevisionId, ItemRevision itemRevision) throws Exception;
    ItemRevision updateItemRevision(Long itemRevisionId, ItemRevision itemRevision, int expectedResponseCode) throws Exception;

    void deleteItemRevision(Long itemRevisionId) throws Exception;
    void deleteItemRevision(Long itemRevisionId, int expectedResponseCode) throws Exception;
}
