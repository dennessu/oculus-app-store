/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/21/2014
  * The interface for Attribute related APIs
 */
public interface OfferAttributeService {

    OfferAttribute getOfferAttribute(String attributeId) throws Exception;
    OfferAttribute getOfferAttribute(String attributeId, String locale) throws Exception;
    OfferAttribute getOfferAttribute(String attributeId, String locale, int expectedResponseCode) throws Exception;
    OfferAttribute getOfferAttribute(String attributeId, int expectedResponseCode, boolean isServiceScope) throws Exception;

    Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara) throws Exception;
    Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    OfferAttribute postDefaultOfferAttribute() throws Exception;
    OfferAttribute postOfferAttribute(OfferAttribute offerAttribute) throws Exception;
    OfferAttribute postOfferAttribute(OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    OfferAttribute updateOfferAttribute(String offerAttributeId, OfferAttribute offerAttribute) throws Exception;
    OfferAttribute updateOfferAttribute(String offerAttributeId, OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    void deleteOfferAttribute(String offerAttributeId) throws Exception;
    void deleteOfferAttribute(String offerAttributeId, int expectedResponseCode) throws Exception;

}
