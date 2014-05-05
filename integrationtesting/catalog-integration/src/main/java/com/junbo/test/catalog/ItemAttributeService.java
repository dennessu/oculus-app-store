/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/21/2014
  * The interface for Item Attribute related APIs
 */
public interface ItemAttributeService {

    ItemAttribute getItemAttribute(Long itemAttributeId) throws Exception;
    ItemAttribute getItemAttribute(Long itemAttributeId, int expectedResponseCode) throws Exception;

    Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara) throws Exception;
    Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    ItemAttribute postItemAttribute(ItemAttribute itemAttribute) throws Exception;
    ItemAttribute postItemAttribute(ItemAttribute itemAttribute, int expectedResponseCode) throws Exception;

    ItemAttribute updateItemAttribute(ItemAttribute itemAttribute) throws Exception;
    ItemAttribute updateItemAttribute(ItemAttribute itemAttribute, int expectedResponseCode) throws Exception;

    void deleteItemAttribute(Long itemAttributeId) throws Exception;
    void deleteItemAttribute(Long itemAttributeId, int expectedResponseCode) throws Exception;
}
