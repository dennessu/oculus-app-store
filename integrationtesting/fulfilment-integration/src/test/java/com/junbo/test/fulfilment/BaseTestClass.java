package com.junbo.test.fulfilment;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.fulfilment.utility.FulfilmentTestDataProvider;
import com.junbo.test.fulfilment.utility.FulfimentValidationHelper;

/**
 * Created by weiyu_000 on 5/14/14.
 */
public class BaseTestClass {
    protected FulfilmentTestDataProvider testDataProvider= new FulfilmentTestDataProvider();
    protected FulfimentValidationHelper validationHelper = new FulfimentValidationHelper();

    public BaseTestClass() {
        super();
        loadOffers();
    }

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;


    private void loadOffers() {
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
    }
}
