/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.item.ItemRevision;
import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 4/17/2014
  * The interface for Item revision related APIs
 */
public interface ItemRevisionService {

    String getItemRevision(String revisionId) throws Exception;
    String getItemRevision(String revisionId, int expectedResponseCode) throws Exception;

    List<String> getItemRevisions(HashMap<String, List<String>> httpPara) throws Exception;
    List<String> getItemRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    ItemRevision prepareItemRevisionEntity(String fileName) throws Exception;

    String postDefaultItemRevision() throws Exception;
    String postDefaultItemRevision(String itemId) throws Exception;
    String postItemRevision(ItemRevision itemRevision) throws Exception;
    String postItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception;

    String updateItemRevision(ItemRevision itemRevision) throws Exception;
    String updateItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception;

    void deleteItemRevision(String itemRevisionId) throws Exception;
    void deleteItemRevision(String itemRevisionId, int expectedResponseCode) throws Exception;
}
