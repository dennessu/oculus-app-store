/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario.util;

import com.junbo.test.buyerscenario.BuyerTestDataProvider;
import com.junbo.test.buyerscenario.BuyerValidationHelper;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Utility.TestClass;

/**
 @author Jason
  * Time: 3/7/2014
  * Base test class
 */
public class BaseTestClass extends TestClass {
    protected BuyerTestDataProvider testDataProvider = new BuyerTestDataProvider();
    protected BuyerValidationHelper validationHelper = new BuyerValidationHelper();

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_storedValue_normal;


    public BaseTestClass() {
        loadOffers();
    }

    private void loadOffers(){
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
        offer_storedValue_normal = ConfigHelper.getSetting("testdata.offer.storedvalue.normal");
    }

}
