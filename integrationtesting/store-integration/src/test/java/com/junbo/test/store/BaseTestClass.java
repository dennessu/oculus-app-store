package com.junbo.test.store;

import com.junbo.test.catalog.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.store.apihelper.TestContext;
import com.junbo.test.store.utility.StoreTestDataProvider;
import com.junbo.test.store.utility.StoreValidationHelper;
import org.testng.annotations.AfterMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.junbo.test.catalog.impl.ItemAttributeServiceImpl.*;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public abstract class BaseTestClass {
    protected String offer_digital_oculus_free1;
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
    protected String offer_storedValue_normal;
    protected String item_digital_normal1;
    protected String item_digital_normal2;
    protected String item_digital_free;
    protected String item_digital_oculus_free1;
    protected String item_digital_oculus_free2;
    protected String cmsPagePath;

    protected String featureRootCriteria = "featureRoot";
    protected List<String> cmsSlot1Items;
    protected List<String> cmsSlot2Items;

    protected ItemService itemService;
    protected ItemRevisionService itemRevisionService;
    protected OfferService offerService;
    protected OfferRevisionService offerRevisionService;
    protected OfferAttributeService offerAttributeService;
    protected final ThreadLocal<ItemAttributeService> itemAttributeService = new ThreadLocal<>();
    protected OAuthService oAuthTokenService;
    protected int listItemPageSize = 2;

    protected boolean useCaseyEmulator = false;

    public BaseTestClass() {
        super();
        loadOffers();
    }

    private void loadOffers() {
        offer_digital_oculus_free1 = ConfigHelper.getSetting("testdata.offer.digital.oculus.free1");
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
        offer_storedValue_normal = ConfigHelper.getSetting("testdata.offer.storedvalue.normal");
        item_digital_normal1 = ConfigHelper.getSetting("testdata.item.digital.normal1");
        item_digital_normal2 = ConfigHelper.getSetting("testdata.item.digital.normal2");
        item_digital_free = ConfigHelper.getSetting("testdata.item.digital.free");
        item_digital_oculus_free1 = ConfigHelper.getSetting("testdata.item.digital.oculus.free1");
        item_digital_oculus_free2 = ConfigHelper.getSetting("testdata.item.digital.oculus.free2");

        if (ConfigHelper.getSetting("testdata.cmspage.slot1.items") != null) {
            cmsSlot1Items = Arrays.asList(ConfigHelper.getSetting("testdata.cmspage.slot1.items").split(","));
        }
        if (ConfigHelper.getSetting("testdata.cmspage.slot2.items") != null) {
            cmsSlot2Items = Arrays.asList(ConfigHelper.getSetting("testdata.cmspage.slot2.items").split(","));
        }
        if (ConfigHelper.getSetting("testdata.list.pagesize") != null) {
            listItemPageSize = Integer.parseInt(ConfigHelper.getSetting("testdata.list.pagesize"));
        }
        if (ConfigHelper.getSetting("casey.useEmulator") != null) {
            useCaseyEmulator = Boolean.valueOf(ConfigHelper.getSetting("casey.useEmulator"));
        }

        cmsPagePath = ConfigHelper.getSetting("testdata.cmspage.path");
        itemService = ItemServiceImpl.instance();
        itemRevisionService = ItemRevisionServiceImpl.instance();
        offerService = OfferServiceImpl.instance();
        offerRevisionService = OfferRevisionServiceImpl.instance();
        offerAttributeService = OfferAttributeServiceImpl.instance();
        itemAttributeService.set(instance());
        oAuthTokenService = OAuthServiceImpl.getInstance();
    }

    StoreTestDataProvider testDataProvider = new StoreTestDataProvider();
    StoreValidationHelper validationHelper = new StoreValidationHelper(testDataProvider);

    @AfterMethod
    public void clear() {
        TestContext.clear();
    }

}
