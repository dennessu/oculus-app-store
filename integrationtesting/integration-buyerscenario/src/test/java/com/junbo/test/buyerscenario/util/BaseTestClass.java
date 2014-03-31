/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario.util;

import com.junbo.test.common.Utility.TestDataProvider;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.libs.ConfigPropertiesHelper;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 @author Jason
  * Time: 3/7/2014
  * Base test class
 */
public class BaseTestClass {
    protected TestDataProvider testDataProvider = new TestDataProvider();
    protected ValidationHelper validationHelper = new ValidationHelper();

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;


    public BaseTestClass() {
        //set loggging info
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
        System.setProperty("logback.configurationFile", "logback-test.xml");

        loadOffers();
    }

    private void loadOffers(){
        offer_digital_normal1 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigPropertiesHelper.instance().getProperty("testdata.offer.physical.normal2");
    }

}
