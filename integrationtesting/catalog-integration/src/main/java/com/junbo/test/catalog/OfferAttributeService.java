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

    OfferAttribute getOfferAttribute(Long attributeId) throws Exception;
    OfferAttribute getOfferAttribute(Long attributeId, int expectedResponseCode) throws Exception;

    Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara) throws Exception;
    Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    OfferAttribute postDefaultOfferAttribute() throws Exception;
    OfferAttribute postOfferAttribute(OfferAttribute offerAttribute) throws Exception;
    OfferAttribute postOfferAttribute(OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    OfferAttribute updateOfferAttribute(OfferAttribute offerAttribute) throws Exception;
    OfferAttribute updateOfferAttribute(OfferAttribute offerAttribute, int expectedResponseCode) throws Exception;

    void deleteOfferAttribute(Long offerAttributeId) throws Exception;
    void deleteOfferAttribute(Long offerAttributeId, int expectedResponseCode) throws Exception;

}
