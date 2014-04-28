/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.EnumHelper;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Offer related APIs
 */
public interface OfferService {

    String getOffer(String offerId) throws Exception;
    String getOffer(String offerId, int expectedResponseCode) throws Exception;

    List<String> getOffers(HashMap<String, String> httpPara) throws Exception;
    List<String> getOffers(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultOffer(EnumHelper.CatalogItemType itemType) throws Exception;
    Offer prepareOfferEntity(String fileName, EnumHelper.CatalogItemType itemType) throws Exception;
    String postOffer(Offer offer) throws Exception;
    String postOffer(Offer offer, int expectedResponseCode) throws Exception;

    String updateOffer(Offer offer) throws Exception;
    String updateOffer(Offer offer, int expectedResponseCode) throws Exception;

    String getOfferIdByName(String offerName) throws  Exception;
}
