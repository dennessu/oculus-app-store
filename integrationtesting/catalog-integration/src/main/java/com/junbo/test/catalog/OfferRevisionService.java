/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 4/17/2014
 * The interface for Offer revision related APIs
 */
public interface OfferRevisionService {

    OfferRevision getOfferRevision(Long offerRevisionId) throws Exception;
    OfferRevision getOfferRevision(Long offerRevisionId, int expectedResponseCode) throws Exception;

    Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara) throws Exception;
    Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    OfferRevision prepareOfferRevisionEntity(String fileName) throws Exception;
    OfferRevision prepareOfferRevisionEntity(String fileName, Boolean addItemInfo) throws Exception;

    OfferRevision postDefaultOfferRevision() throws Exception;
    OfferRevision postDefaultOfferRevision(Offer offer) throws Exception;
    OfferRevision postDefaultOfferRevision(Offer offer, Item item) throws Exception;
    OfferRevision postOfferRevision(OfferRevision offerRevision) throws Exception;
    OfferRevision postOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception;

    OfferRevision updateOfferRevision(OfferRevision offerRevision) throws Exception;
    OfferRevision updateOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception;

    void deleteOfferRevision(Long offerRevisionId) throws Exception;
    void deleteOfferRevision(Long offerRevisionId, int expectedResponseCode) throws Exception;
}
