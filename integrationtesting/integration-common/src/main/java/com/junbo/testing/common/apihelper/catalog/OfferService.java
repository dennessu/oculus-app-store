/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog;

import com.junbo.catalog.spec.model.offer.Offer;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Offer related APIs
 */
public interface OfferService {

    String getOffer(String offerId, HashMap<String, String> httpPara) throws Exception;
    String getOffer(String offerId, HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    List<String> getOffer(HashMap<String, String> httpPara) throws Exception;
    List<String> getOffer(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultOffer(boolean isPhysical) throws Exception;
    Offer prepareOfferEntity(String fileName, boolean isPhysical) throws Exception;
    String postOffer(Offer offer) throws Exception;
    String postOffer(Offer offer, int expectedResponseCode) throws Exception;

    String updateOffer(Offer offer) throws Exception;
    String updateOffer(Offer offer, int expectedResponseCode) throws Exception;

    String getOfferIdByName(String offerName) throws  Exception;
}
