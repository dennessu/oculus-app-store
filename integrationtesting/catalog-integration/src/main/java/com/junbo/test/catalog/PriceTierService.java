/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The interface for PriceTier related APIs
 */
public interface PriceTierService {

    PriceTier getPriceTier(String priceTierId) throws Exception;
    PriceTier getPriceTier(String priceTierId, int expectedResponseCode) throws Exception;

    Results<PriceTier> getPriceTiers(HashMap<String, List<String>> httpPara) throws Exception;
    Results<PriceTier> getPriceTiers(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    PriceTier postDefaultPriceTier() throws Exception;
    PriceTier postPriceTier(PriceTier priceTier) throws Exception;
    PriceTier postPriceTier(PriceTier priceTier, int expectedResponseCode) throws Exception;

    PriceTier updatePriceTier(String priceTierId, PriceTier priceTier) throws Exception;
    PriceTier updatePriceTier(String priceTierId, PriceTier priceTier, int expectedResponseCode) throws Exception;

    void deletePriceTier(String priceTierId) throws Exception;
    void deletePriceTier(String priceTierId, int expectedResponseCode) throws Exception;

}
