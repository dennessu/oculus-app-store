/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.enums.EventActionType;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.enums.EventType;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.model.Results;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl extends HttpClientBase implements OfferService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "offers";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";
    private final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";
    private final String defaultPreOrderOfferRevisionFileName = "defaultPreOrderOfferRevision";
    private final String defaultFreeOfferRevisionFileName = "defaultFreeOfferRevision";
    private final String preOrderDigital = "testOffer_PreOrder_Digital1";
    private final String preOrderPhysical = "testOffer_PreOrder_Physical1";
    private final String freeOfferDigital = "testOffer_Free_Digital";
    private final String freeOfferPhysical = "testOffer_Free_Physical";
    private final String defaultOfferFileName = "defaultOffer";
    private final String defaultItemFileName = "defaultItem";
    private final String defaultLocale = "en_US";

    private LogHelper logger = new LogHelper(OfferServiceImpl.class);
    private static OfferService instance;

    private UserService userService = UserServiceImpl.instance();
    private OrganizationService organizationService = OrganizationServiceImpl.instance();

    private final Integer defaultPagingSize = 10000;
    private final Integer start = 0;
    private Boolean offerLoaded = false;
    private String catalogDB;

    public static synchronized OfferService instance() {
        if (instance == null) {
            instance = new OfferServiceImpl();
        }
        return instance;
    }

    private OfferServiceImpl() {
        componentType = ComponentType.CATALOG;
    }

    public Offer getOffer(String offerId) throws Exception {
        return getOffer(offerId, 200);
    }

    public Offer getOffer(String offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + offerId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        Offer offerGet = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, responseBody);

        Master.getInstance().addOffer(offerGet.getOfferId(), offerGet);
        return offerGet;
    }

    public Results<Offer> getOffers(HashMap<String, List<String>> httpPara) throws Exception {
        return getOffers(httpPara, 200);
    }

    public Results<Offer> getOffers(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<Offer> offerGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Offer>>() {},
                responseBody);
        for (Offer offer : offerGet.getItems()){
            Master.getInstance().addOffer(offer.getOfferId(), offer);
        }

        return offerGet;
    }

    public Offer postDefaultOffer() throws Exception {
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName);
        return postOffer(offerForPost);
    }

    public Offer postDefaultOffer(OrganizationId organizationId) throws Exception {
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName, organizationId);
        return postOffer(offerForPost);
    }

    public Offer prepareOfferEntity(String fileName) throws Exception {
        return prepareOfferEntity(fileName, getOrganizationId());
    }

    public Offer prepareOfferEntity(String fileName, OrganizationId organizationId) throws Exception {
        String strOfferContent = readFileContent(String.format("testOffers/%s.json", fileName));
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        offerForPost.setOwnerId(organizationId);
        return offerForPost;
    }

    public Offer postOffer(Offer offer) throws Exception {
        return postOffer(offer, 200);
    }

    public Offer postOffer(Offer offer, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offer, expectedResponseCode);
        Offer offerPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);

        Master.getInstance().addOffer(offerPost.getOfferId(), offerPost);
        return offerPost;
    }

    public Offer updateOffer(String offerId, Offer offer) throws Exception {
        return updateOffer(offerId, offer, 200);
    }

    public Offer updateOffer(String offerId, Offer offer, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + offerId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offer, expectedResponseCode);
        Offer offerPut = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);

        Master.getInstance().addOffer(offerPut.getOfferId(), offerPut);
        return offerPut;
    }

    public void deleteOffer(String offerId) throws Exception {
        this.deleteOffer(offerId, 204);
    }

    public void deleteOffer(String offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + offerId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeOffer(offerId);
    }

    public String getOfferIdByName(String offerName) throws Exception {
        catalogDB = ConfigHelper.getSetting("catalogDB");

        if (catalogDB != null && catalogDB.equalsIgnoreCase("cloudant")) {
            Results<Offer> offerRtn = this.searchOfferByName(offerName);

            if (offerRtn.getItems().size() <= 0) {
                this.postPredefinedOffer();
                offerRtn = this.searchOfferByName(offerName);
            }
            if (offerRtn.getItems().size() <= 0) {
                return "No such predefined offer";
            }
            else {
                OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
                ItemService itemService = ItemServiceImpl.instance();
                ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

                OfferRevision offerRevision;
                Item item;

                offerRevision = offerRevisionService.getOfferRevision(offerRtn.getItems().get(0).getCurrentRevisionId());
                if (offerRevision != null) {
                    item = itemService.getItem(offerRevision.getItems().get(0).getItemId());
                    itemRevisionService.getItemRevision(item.getCurrentRevisionId());
                }

                return offerRtn.getItems().get(0).getOfferId();
            }
        }
        else {
            if (!offerLoaded){
                this.loadAllOffers();
                this.loadAllOfferRevisions();
                this.loadAllItems();
                this.loadAllItemRevisions();
                this.postPredefinedOffer();
                offerLoaded = true;
            }

            return Master.getInstance().getOfferIdByName(offerName);
        }
    }

    private void loadAllOffers() throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("start", listStart);
        paraMap.put("size", listSize);
        this.getOffers(paraMap);
    }

    private void loadAllOfferRevisions() throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStatus = new ArrayList<>();
        listStatus.add(CatalogEntityStatus.APPROVED.getEntityStatus());
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("status", listStatus);
        paraMap.put("start", listStart);
        paraMap.put("size", listSize);
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
        offerRevisionService.getOfferRevisions(paraMap);
    }

    private void loadAllItems() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("start", listStart);
        paraMap.put("size", listSize);
        itemService.getItems(paraMap);
    }

    private void loadAllItemRevisions() throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStatus = new ArrayList<>();
        listStatus.add(CatalogEntityStatus.APPROVED.getEntityStatus());
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("status", listStatus);
        paraMap.put("start", listStart);
        paraMap.put("size", listSize);

        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        itemRevisionService.getItemRevisions(paraMap);
    }

    private Results<Offer> searchOfferByName(String offerName) throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add("name:" + offerName);
        paraMap.put("q", query);

        return this.getOffers(paraMap);
    }

    private Results<Item> searchItemByName(String itemName) throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add("name:" + itemName);
        paraMap.put("q", query);

        return itemService.getItems(paraMap);
    }

    private void postPredefinedOffer() throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream("testOffers/predefinedofferlist.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sCurrentLine;
            Results<Offer> offerRtn;
            while ((sCurrentLine = br.readLine()) != null) {
                logger.logInfo(sCurrentLine);
                String[] strLine = sCurrentLine.split(",");
                if (catalogDB != null && catalogDB.equalsIgnoreCase("cloudant")) {
                    offerRtn = this.searchOfferByName(strLine[0]);
                    if (offerRtn.getItems().size() <= 0) {
                        preparePredefinedOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
                    }
                }
                else {
                    if (Master.getInstance().getOfferIdByName(strLine[0]) == null) {
                        preparePredefinedOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    private void preparePredefinedOffer(String offerName, String itemName, String userName, String offerType) throws  Exception {

        List<String> userIdList = userService.GetUserByUserName(userName);
        String userId;

        if (userIdList == null || userIdList.isEmpty()) {
            userId = getUserId();
        }
        else {
            userId = userIdList.get(0);
        }

        OrganizationId organizationId = getOrganizationId(userId);

        Item item;
        if (catalogDB != null && catalogDB.equalsIgnoreCase("cloudant")) {
            Results<Item> itemRtn = this.searchItemByName(itemName);
            if (itemRtn.getItems().size() <= 0) {
                item = prepareItem(organizationId, itemName, offerType);
            }
            else {
                item = itemRtn.getItems().get(0);
            }
        }
        else {
            String itemId = Master.getInstance().getItemIdByName(itemName);
            if (itemId == null) {
                item = prepareItem(organizationId, itemName, offerType);
            }
            else {
                item = Master.getInstance().getItem(itemId);
            }
        }

        //Post offer
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName, organizationId);
        offerForPost.setOwnerId(organizationId);
        Offer offer = this.postOffer(offerForPost);

        //Post offer revision
        String strOfferRevisionContent;
        if (offerName.equalsIgnoreCase(preOrderDigital) ||
                offerName.equalsIgnoreCase(preOrderPhysical)){
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultPreOrderOfferRevisionFileName));
        }
        else if (offerName.equalsIgnoreCase(freeOfferDigital) ||
                offerName.equalsIgnoreCase(freeOfferPhysical)){
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultFreeOfferRevisionFileName));
        }
        else if (offerType.equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultStoredValueOfferRevisionFileName));
        }
        else {
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultOfferRevisionFileName));
        }


        OfferRevision offerRevisionForPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, strOfferRevisionContent);

        if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            offerRevisionForPost.getEventActions().get(EventType.PURCHASE.name()).get(0).setItemId(item.getItemId());
        } else if (item.getType().equalsIgnoreCase(CatalogItemType.CONSUMABLE_UNLOCK.getItemType())) {
            List<Action> purchaseActions = new ArrayList<>();
            Map<String, List<Action>> consumableEvent = new HashMap<>();
            Action action = new Action();
            action.setType(EventActionType.GRANT_ENTITLEMENT.name());
            action.setItemId(item.getItemId());
            action.setUseCount(10);
            purchaseActions.add(action);
            consumableEvent.put(EventType.PURCHASE.name(), purchaseActions);
            offerRevisionForPost.setEventActions(consumableEvent);
        }

        //set locales
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevisionForPost.getLocales().get(defaultLocale);
        if (offerRevisionLocaleProperties == null) {
            offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        }

        offerRevisionLocaleProperties.setName(offerName);

        HashMap<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        locales.put(defaultLocale, offerRevisionLocaleProperties);
        offerRevisionForPost.setLocales(locales);

        //Add item related info
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerRevisionForPost.setItems(itemEntities);
        offerRevisionForPost.setOwnerId(organizationId);

        //Add offer related info
        offerRevisionForPost.setOfferId(offer.getOfferId());

        //Post offer revision and update its status to 'Approved'
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
        OfferRevision offerRevision = offerRevisionService.postOfferRevision(offerRevisionForPost);
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);
        this.getOffer(offer.getOfferId());
    }

    private OrganizationId getOrganizationId() throws Exception {
        return organizationService.postDefaultOrganization().getId();
    }

    private OrganizationId getOrganizationId(String userId) throws Exception {
        return organizationService.postDefaultOrganization(userId).getId();
    }

    private String getUserId() throws Exception {
        return userService.PostUser();
    }

    private Item prepareItem(OrganizationId ownerId, String fileName, String itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item = itemService.prepareItemEntity(defaultItemFileName, ownerId);
        item.setType(itemType);
        item.setOwnerId(ownerId);
        Item itemPost = itemService.postItem(item);

        //Attach item revision to the item
        ItemRevision itemRevision;
        if (itemType.equalsIgnoreCase(CatalogItemType.APP.getItemType()) ||
                itemType.equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.getItemType())) {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        }
        else if (itemType.equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultStoredValueItemRevisionFileName);
        }
        else {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }

        //set packagename
        itemRevision.setPackageName("PackageName_" + RandomFactory.getRandomStringOfAlphabet(10));

        itemRevision.setItemId(itemPost.getItemId());
        itemRevision.setOwnerId(itemPost.getOwnerId());

        //prepare IapHostItemIds
        if (itemRevision.getDistributionChannels().contains("INAPP")) {
            Item iapHostItem = ItemServiceImpl.instance().postDefaultItem(CatalogItemType.APP, ownerId);
            List<String> iapHostItemIds = new ArrayList<>();
            iapHostItemIds.add(iapHostItem.getItemId());
            itemRevision.setIapHostItemIds(iapHostItemIds);
        }

        //set locales
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = itemRevision.getLocales().get(defaultLocale);
        if (itemRevisionLocaleProperties == null) {
            itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        }

        itemRevisionLocaleProperties.setName(fileName);

        HashMap<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        itemRevision.setLocales(locales);

        //Post and then approve the item revision
        ItemRevision itemRevisionPost = itemRevisionService.postItemRevision(itemRevision);
        itemRevisionPost.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevisionPost.getRevisionId(), itemRevisionPost);
        return itemService.getItem(itemPost.getItemId());
    }

}
