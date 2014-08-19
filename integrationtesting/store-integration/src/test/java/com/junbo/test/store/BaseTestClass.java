package com.junbo.test.store;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.store.utility.StoreTestDataProvider;
import com.junbo.test.store.utility.StoreValidationHelper;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public abstract class BaseTestClass {
    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_digital_preOrder;
    protected String offer_physical_preOrder;
    protected String offer_inApp_consumable1;
    protected String offer_inApp_consumable2;
    protected String offer_digital_free;
    protected String offer_physical_free;
    protected String offer_iap_normal;
    protected String offer_iap_free;

    public BaseTestClass () {
        super();
        loadOffers();
    }

    private void loadOffers() {
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
        offer_digital_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.digital.normal");
        offer_physical_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.physical.normal");
        offer_inApp_consumable1 = ConfigHelper.getSetting("testdata.offer.inApp.consumable.normal1");
        offer_inApp_consumable2 = ConfigHelper.getSetting("testdata.offer.inApp.consumable.normal2");
        offer_digital_free = ConfigHelper.getSetting("testdata.offer.digital.free");
        offer_physical_free = ConfigHelper.getSetting("testdata.offer.physical.free");
        offer_iap_normal = ConfigHelper.getSetting("testdata.offer.iap.normal");
        offer_iap_free = ConfigHelper.getSetting("testdata.offer.iap.free");

    }

    StoreTestDataProvider testDataProvider = new StoreTestDataProvider();
    StoreValidationHelper validationHelper = new StoreValidationHelper();

}
