package com.junbo.test.store;

import com.junbo.test.catalog.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.store.apihelper.TestContext;
import com.junbo.test.store.utility.StoreTestDataProvider;
import com.junbo.test.store.utility.StoreValidationHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.List;

import static com.junbo.test.catalog.impl.ItemAttributeServiceImpl.instance;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public abstract class BaseTestClass {
    protected String offer_digital_free_same_item_offer1;
    protected String offer_digital_free_same_item_offer2;
    protected String offer_digital_oculus_free1;
    protected String offer_digital_oculus_free2;
    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_digital_preOrder;
    protected String offer_physical_preOrder;
    protected String offer_inApp_consumable;
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
    protected String item_digital_free_same_item;
    protected String cmsPageName;

    protected String cmsSlot1 = "slot1";
    protected String cmsSlot2 = "slot2";
    protected String cmsSlotName1 = "Featured All";
    protected String cmsSlotName2 = "Featured SS";
    protected List<String> cmsSlot1Items;
    protected List<String> cmsSlot2Items;

    protected String initialAppsCmsPage = "android-initial-app";
    protected String initialAppsCmsSlot = "offers";

    protected String initialDownloadItemCmsPage = "android-initial-items";
    protected String initialDownloadItemSlot = "android-initial-items";
    protected String initialDownloadItemContentName = "android-initial-items";

    protected ItemService itemService;
    protected ItemRevisionService itemRevisionService;
    protected OfferService offerService;
    protected OfferRevisionService offerRevisionService;
    protected OfferAttributeService offerAttributeService;
    protected final ThreadLocal<ItemAttributeService> itemAttributeService = new ThreadLocal<>();
    protected OAuthService oAuthTokenService;
    protected boolean tosDisabled = false;
    protected int listItemPageSize = 2;

    protected boolean serviceClientEnabled = false;

    protected boolean useCaseyEmulator = false;

    LogHelper logger = new LogHelper(BaseTestClass.class);

    public BaseTestClass() {
        super();
        loadOffers();
        loadEndPointUrl();
    }

    private void loadEndPointUrl() {
        Master.getInstance().setPrimaryCommerceEndPointUrl(ConfigHelper.getSetting("defaultCommerceEndpoint"));
        String secondaryUrl = ConfigHelper.getSetting("secondaryDcEndpoint") != null ? ConfigHelper.getSetting("secondaryDcEndpoint") : Master.getInstance().getPrimaryCommerceEndPointUrl();
        Master.getInstance().setSecondaryCommerceEndPointUrl(secondaryUrl);
    }

    private void loadOffers() {
        offer_digital_oculus_free1 = ConfigHelper.getSetting("testdata.offer.digital.oculus.free1");
        offer_digital_oculus_free2 = ConfigHelper.getSetting("testdata.offer.digital.oculus.free2");
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
        offer_digital_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.digital.normal");
        offer_physical_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.physical.normal");
        offer_inApp_consumable = ConfigHelper.getSetting("testdata.offer.inApp.consumable.normal");
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
        offer_digital_free_same_item_offer1 = ConfigHelper.getSetting("testdata.offer.digital.free.sameItem.offer1");
        offer_digital_free_same_item_offer2 = ConfigHelper.getSetting("testdata.offer.digital.free.sameItem.offer2");
        item_digital_free_same_item = ConfigHelper.getSetting("testdata.offer.digital.free.sameItem");

        if (ConfigHelper.getSetting("test.tos.verify.disabled") != null) {
            tosDisabled = Boolean.valueOf(ConfigHelper.getSetting("test.tos.verify.disabled"));
        }
        if (ConfigHelper.getSetting("test.oauth.service.client.enabled") != null) {
            serviceClientEnabled = Boolean.valueOf(ConfigHelper.getSetting("test.oauth.service.client.enabled"));
        }
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

        cmsPageName = ConfigHelper.getSetting("testdata.cmspage.name");
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

    @BeforeMethod(alwaysRun = true)
    public void initialEnv() {
        if (ConfigHelper.getSetting("endpoint.random") == null) {
            return;
        } else if (ConfigHelper.getSetting("endpoint.random") != null && Boolean.valueOf(ConfigHelper.getSetting("endpoint.random"))) {
            logger.logInfo(String.format("Set endpoint type = Random"));
            Master.getInstance().setEndPointType(Master.EndPointType.Random);
            logger.logInfo(String.format("Current endpoint type = %s", Master.getInstance().getCurrentEndPointType()));
        } else {
            logger.logInfo(String.format("Set endpoint type = Primary"));
            Master.getInstance().setEndPointType(Master.EndPointType.Primary);
            logger.logInfo(String.format("Current endpoint type = %s", Master.getInstance().getCurrentEndPointType()));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void clear() {
        TestContext.clear();
    }

}
