/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl extends HttpClientBase implements OfferService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "/offers";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";
    private final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";
    private final String defaultPreOrderOfferRevisionFileName = "defaultPreOrderOfferRevision";
    private final String preOrderDigital = "testOffer_PreOrder_Digital1";
    private final String preOrderPhysical = "testOffer_PreOrder_Physical1";
    private final String defaultOfferFileName = "defaultOffer";
    private final String defaultItemFileName = "defaultItem";
    private final Integer defaultPagingSize = 10000;
    private final Integer start = 0;

    private LogHelper logger = new LogHelper(OfferServiceImpl.class);
    private static OfferService instance;
    private Boolean offerLoaded = false;

    private ItemService itemService = ItemServiceImpl.instance();
    private UserService userService = UserServiceImpl.instance();
    private OrganizationService organizationService = OrganizationServiceImpl.instance();

    public static synchronized OfferService instance() {
        if (instance == null) {
            instance = new OfferServiceImpl();
        }
        return instance;
    }

    private OfferServiceImpl() {
    }

    public Offer getOffer(String offerId) throws Exception {
        return getOffer(offerId, 200);
    }

    public Offer getOffer(String offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idToUrlString(OfferId.class, offerId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        Offer offerGet = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, responseBody);
        String offerRtnId = IdConverter.idToUrlString(OfferId.class, offerGet.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerGet);
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
            String offerRtnId = IdConverter.idToUrlString(OfferId.class, offer.getOfferId());
            Master.getInstance().addOffer(offerRtnId, offer);
        }

        return offerGet;
    }

    public Offer postDefaultOffer() throws Exception {
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName);
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
        String offerRtnId = IdConverter.idToUrlString(OfferId.class, offerPost.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerPost);
        return offerPost;
    }

    public Offer updateOffer(String offerId, Offer offer) throws Exception {
        return updateOffer(offerId, offer, 200);
    }

    public Offer updateOffer(String offerId, Offer offer, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idToUrlString(OfferId.class, offerId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offer, expectedResponseCode);
        Offer offerPut = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);
        String offerRtnId = IdConverter.idToUrlString(OfferId.class, offerPut.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerPut);
        return offerPut;
    }

    public void deleteOffer(String offerId) throws Exception {
        this.deleteOffer(offerId, 204);
    }

    public void deleteOffer(String offerId, int expectedResponseCode) throws Exception {
        String strOfferId = IdConverter.idToUrlString(OfferId.class, offerId);
        String url = catalogServerURL + "/" + strOfferId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeOffer(strOfferId);
    }

    public String getOfferIdByName(String offerName) throws  Exception {

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

    private void postPredefinedOffer() throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream("testOffers/predefinedofferlist.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                logger.logInfo(sCurrentLine);
                String[] strLine = sCurrentLine.split(",");
                if (Master.getInstance().getOfferIdByName(strLine[0]) == null) {
                    preparePredefinedOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
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

        String itemId = Master.getInstance().getItemIdByName(itemName);
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
        if (itemId == null) {
            item = prepareItem(userId, itemName, offerType);
        }
        else {
            item = Master.getInstance().getItem(itemId);
        }

        //Post offer
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName);
        offerForPost.setOwnerId(organizationId);
        Offer offer = this.postOffer(offerForPost);

        //Post offer revision
        String strOfferRevisionContent;
        if (offerName.equalsIgnoreCase(preOrderDigital) ||
                offerName.equalsIgnoreCase(preOrderPhysical)){
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultPreOrderOfferRevisionFileName));
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

        //set locales
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        offerRevisionLocaleProperties.setName(offerName);
        HashMap<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", offerRevisionLocaleProperties);
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

    private Item prepareItem(String ownerId, String fileName, String itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item = itemService.prepareItemEntity(defaultItemFileName);
        item.setType(itemType);
        item.setOwnerId(getOrganizationId(ownerId));
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

        itemRevision.setItemId(itemPost.getItemId());
        itemRevision.setOwnerId(itemPost.getOwnerId());

        //set locales
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        itemRevisionLocaleProperties.setName(fileName);
        HashMap<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", itemRevisionLocaleProperties);
        itemRevision.setLocales(locales);

        //Post and then approve the item revision
        ItemRevision itemRevisionPost = itemRevisionService.postItemRevision(itemRevision);
        itemRevisionPost.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevisionPost.getRevisionId(), itemRevisionPost);
        return itemService.getItem(itemPost.getItemId());
    }

}
