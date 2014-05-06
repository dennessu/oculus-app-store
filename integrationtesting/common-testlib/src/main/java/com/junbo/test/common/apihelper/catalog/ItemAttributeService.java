/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/21/2014
  * The interface for Item Attribute related APIs
 */
public interface ItemAttributeService {

    String getItemAttribute(String itemAttributeId) throws Exception;
    String getItemAttribute(String itemAttributeId, int expectedResponseCode) throws Exception;

    List<String> getItemAttributes(HashMap<String, List<String>> httpPara) throws Exception;
    List<String> getItemAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    String postItemAttribute(ItemAttribute itemAttribute) throws Exception;
    String postItemAttribute(ItemAttribute itemAttribute, int expectedResponseCode) throws Exception;

    String updateItemAttribute(ItemAttribute itemAttribute) throws Exception;
    String updateItemAttribute(ItemAttribute itemAttribute, int expectedResponseCode) throws Exception;

    void deleteItemAttribute(String itemAttributeId) throws Exception;
    void deleteItemAttribute(String itemAttributeId, int expectedResponseCode) throws Exception;
}
