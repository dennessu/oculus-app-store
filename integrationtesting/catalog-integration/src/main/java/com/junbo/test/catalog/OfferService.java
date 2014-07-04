/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for Offer related APIs
 */
public interface OfferService {

    Offer getOffer(String offerId) throws Exception;
    Offer getOffer(String offerId, int expectedResponseCode) throws Exception;

    Results<Offer> getOffers(HashMap<String, List<String>> httpPara) throws Exception;
    Results<Offer> getOffers(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    Offer postDefaultOffer() throws Exception;
    Offer postDefaultOffer(OrganizationId organizationId) throws Exception;
    Offer prepareOfferEntity(String fileName) throws Exception;
    Offer prepareOfferEntity(String fileName, OrganizationId organizationId) throws Exception;
    Offer postOffer(Offer offer) throws Exception;
    Offer postOffer(Offer offer, int expectedResponseCode) throws Exception;

    Offer updateOffer(String offerId, Offer offer) throws Exception;
    Offer updateOffer(String offerId, Offer offer, int expectedResponseCode) throws Exception;

    String getOfferIdByName(String offerName) throws  Exception;

    void deleteOffer(String offerId) throws Exception;
    void deleteOffer(String offerId, int expectedResponseCode) throws Exception;

}
