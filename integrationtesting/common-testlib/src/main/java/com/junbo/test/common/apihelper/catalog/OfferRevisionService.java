/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.test.common.libs.EnumHelper;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 4/17/2014
 * The interface for Offer revision related APIs
 */
public interface OfferRevisionService {

    String getOfferRevision(String offerRevisionId) throws Exception;
    String getOfferRevision(String offerRevisionId, int expectedResponseCode) throws Exception;

    List<String> getOfferRevisions(HashMap<String, String> httpPara) throws Exception;
    List<String> getOfferRevisions(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultOfferRevision(EnumHelper.CatalogItemType itemType) throws Exception;
    OfferRevision prepareOfferRevisionEntity(String fileName, EnumHelper.CatalogItemType itemType) throws Exception;
    String postOfferRevision(OfferRevision offerRevision) throws Exception;
    String postOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception;

    String updateOfferRevision(OfferRevision offerRevision) throws Exception;
    String updateOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception;

    void deleteOfferRevision(String offerRevisionId) throws Exception;
    void deleteOfferRevision(String offerRevisionId, int expectedResponseCode) throws Exception;
}
