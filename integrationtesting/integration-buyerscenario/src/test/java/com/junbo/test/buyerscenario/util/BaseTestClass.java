/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario.util;

import com.junbo.test.buyerscenario.helper.TestDataProvider;
import com.junbo.test.buyerscenario.helper.ValidationHelper;
import com.junbo.test.common.Utility.TestClass;


import com.junbo.test.common.libs.ConfigPropertiesHelper;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 @author Jason
  * Time: 3/7/2014
  * Base test class
 */
public class BaseTestClass extends TestClass {
    protected TestDataProvider testDataProvider = new TestDataProvider();
    protected ValidationHelper validationHelper = new ValidationHelper();

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;


    public BaseTestClass() {
        loadOffers();
    }

    private void loadOffers(){
        offer_digital_normal1 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.physical.normal2");
    }

}
