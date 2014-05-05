/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/21/2014
  * The interface for Attribute related APIs
 */
public interface OfferAttributeService {

    String getOfferAttribute(String attributeId) throws Exception;
    String getOfferAttribute(String attributeId, int expectedResponseCode) throws Exception;

    List<String> getOfferAttributes(HashMap<String, List<String>> httpPara) throws Exception;
    List<String> getOfferAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    String postOfferAttribute(OfferAttribute offerAttribute) throws Exception;
    String postOfferAttribute(OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    String updateOfferAttribute(OfferAttribute offerAttribute) throws Exception;
    String updateOfferAttribute(OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    void deleteOfferAttribute(String offerAttributeId) throws Exception;
    void deleteOfferAttribute(String offerAttributeId, int expectedResponseCode) throws Exception;

}
