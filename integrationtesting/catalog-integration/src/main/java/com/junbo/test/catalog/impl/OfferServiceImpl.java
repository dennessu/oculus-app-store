/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl extends HttpClientBase implements OfferService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offers";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";
    private final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";
    private final String defaultOfferFileName = "defaultOffer";
    private final Integer defaultPagingSize = 10000;
    private final Integer start = 0;

    private LogHelper logger = new LogHelper(OfferServiceImpl.class);
    private static OfferService instance;
    private Boolean offerLoaded = false;

    private ItemService itemService = ItemServiceImpl.instance();
    private UserService userService = UserServiceImpl.instance();

    private Map<String, Item> items;
    private Map<String, Offer> offers;
    private Map<String, ItemRevision> itemRevisions;
    private Map<String, OfferRevision> offerRevisions;

    public static synchronized OfferService instance() {
        if (instance == null) {
            instance = new OfferServiceImpl();
        }
        return instance;
    }

    private OfferServiceImpl() {
        items = new HashMap<>();
        offers = new HashMap<>();
        itemRevisions = new HashMap<>();
        offerRevisions = new HashMap<>();
    }

    public Offer getOffer(Long offerId) throws Exception {
        return getOffer(offerId, 200);
    }

    public Offer getOffer(Long offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferId.class, offerId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Offer>() {
        }, responseBody);
    }

    public Results<Offer> getOffers(HashMap<String, List<String>> httpPara) throws Exception {
        return getOffers(httpPara, 200);
    }

    public Results<Offer> getOffers(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Offer>>() {
        }, responseBody);
    }

    public Offer postDefaultOffer() throws Exception {
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName);
        return postOffer(offerForPost);
    }

    public Offer prepareOfferEntity(String fileName) throws Exception {
        String strOfferContent = readFileContent(String.format("testOffers/%s.json", fileName));
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        offerForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, getUserId()));
        return offerForPost;
    }

    public Offer postOffer(Offer offer) throws Exception {
        return postOffer(offer, 200);
    }

    public Offer postOffer(Offer offer, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offer, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Offer>() {
        }, responseBody);
    }

    public Offer updateOffer(Offer offer) throws Exception {
        return updateOffer(offer, 200);
    }

    public Offer updateOffer(Offer offer, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferId.class, offer.getOfferId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offer, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Offer>() {
        }, responseBody);
    }

    public void deleteOffer(Long offerId) throws Exception {
        this.deleteOffer(offerId, 204);
    }

    public void deleteOffer(Long offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferId.class, offerId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
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

        return getOfferByName(offerName);
    }

    private void loadAllOffers() throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("start", listStart);
        paraMap.put("size", listSize);
        Results<Offer> offerResults = this.getOffers(paraMap);

        for (Offer offer : offerResults.getItems()){
            String offerId = IdConverter.idLongToHexString(OfferId.class, offer.getOfferId());
            offers.put(offerId, offer);
        }
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
        Results<OfferRevision> offerRevisionResults = offerRevisionService.getOfferRevisions(paraMap);

        for (OfferRevision offerRevision : offerRevisionResults.getItems()){
            String offerRevisionId = IdConverter.idLongToHexString(OfferRevisionId.class, offerRevision.getRevisionId());
            offerRevisions.put(offerRevisionId, offerRevision);
        }
    }

    private void loadAllItems() throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listStart = new ArrayList<>();
        listStart.add(start.toString());
        List<String> listSize = new ArrayList<>();
        listSize.add(defaultPagingSize.toString());

        paraMap.put("start", listStart);
        paraMap.put("size", listSize);
        Results<Item> itemResults = itemService.getItems(paraMap);

        for (Item item : itemResults.getItems()){
            String itemId = IdConverter.idLongToHexString(ItemId.class, item.getItemId());
            items.put(itemId, item);
        }
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
        Results<ItemRevision> itemRevisonResults = itemRevisionService.getItemRevisions(paraMap);

        for (ItemRevision itemRevision : itemRevisonResults.getItems()){
            String itemRevisionId = IdConverter.idLongToHexString(ItemRevisionId.class, itemRevision.getRevisionId());
            itemRevisions.put(itemRevisionId, itemRevision);
        }
    }

    private void postPredefinedOffer() throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream("testOffers/predefinedofferlist.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                logger.logInfo(sCurrentLine);
                String[] strLine = sCurrentLine.split(",");
                if (getOfferByName(strLine[0]) == null) {
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

    private String getOfferByName(String offerName) {

        for (Map.Entry<String, Offer> entry : offers.entrySet()) {
            String key = entry.getKey();
            Offer offer = entry.getValue();
            if (offer.getCurrentRevisionId() != null) {
                String offerRevisionId = IdConverter.idLongToHexString(OfferRevisionId.class, offer.getCurrentRevisionId());
                OfferRevision offerRevision = this.offerRevisions.get(offerRevisionId);

                if (offerRevision != null && offerRevision.getLocales().get("en_US").getName().equalsIgnoreCase(offerName)) {
                    return key;
                }
            }
        }
        return null;
    }

    private String getItemIdByName(String itemName) {

        for (Map.Entry<String, Item> entry : items.entrySet()) {
            String key = entry.getKey();
            Item item = entry.getValue();
            if (item.getCurrentRevisionId() != null) {
                String itemRevisionId = IdConverter.idLongToHexString(ItemRevisionId.class, item.getCurrentRevisionId());
                ItemRevision itemRevision = this.itemRevisions.get(itemRevisionId);

                if (itemRevision.getLocales().get("en_US").getName().equalsIgnoreCase(itemName)) {
                    return key;
                }
            }
        }
        return null;
    }

    private void preparePredefinedOffer(String offerName, String itemName, String userName, String offerType) throws  Exception {

        String itemId = this.getItemIdByName(itemName);
        List<String> userIdList = userService.GetUserByUserName(userName);
        String userId;

        if (userIdList == null || userIdList.isEmpty()) {
            userId = getUserId();
        }
        else {
            userId = userIdList.get(0);
        }

        Item item;
        if (itemId == null) {
            item = prepareItem(userId, itemName, offerType);
        }
        else {
            item = itemService.getItem(IdConverter.hexStringToId(ItemId.class, itemId));
        }

        //Post offer
        String strOfferContent = readFileContent(String.format("testOffers/%s.json", offerName));
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        offerForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));
        Offer offer = this.postOffer(offerForPost);

        //Post offer revision
        String strOfferRevisionContent;
        if (offerType.equalsIgnoreCase(EnumHelper.CatalogItemType.STORED_VALUE.getItemType())) {
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
        offerRevisionForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));

        //Add offer related info
        offerRevisionForPost.setOfferId(offer.getOfferId());

        //Post offer revision and update its status to 'Approved'
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
        OfferRevision offerRevision = offerRevisionService.postOfferRevision(offerRevisionForPost);
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision);

        //Add to offerRevisions
        offerRevisions.put(IdConverter.idLongToHexString(OfferRevisionId.class, offerRevision.getRevisionId()), offerRevision);

        offer = this.getOffer(offer.getOfferId());
        offers.put(IdConverter.idLongToHexString(OfferId.class, offer.getOfferId()), offer);
    }

    private String getUserId() throws Exception {
        UserService us = UserServiceImpl.instance();
        return us.PostUser();
    }

    private Item prepareItem(String ownerId, String fileName, String itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item = itemService.prepareItemEntity(fileName);
        item.setOwnerId(IdConverter.hexStringToId(UserId.class, ownerId));
        Item itemPost = itemService.postItem(item);

        //Attach item revision to the item
        ItemRevision itemRevision;
        if (itemType.equalsIgnoreCase(CatalogItemType.DIGITAL.getItemType())) {
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
        itemRevisionPost = itemRevisionService.updateItemRevision(itemRevisionPost);

        //Add to itemRevisions
        itemRevisions.put(IdConverter.idLongToHexString(ItemRevisionId.class, itemRevisionPost.getRevisionId()), itemRevisionPost);

        itemPost = itemService.getItem(itemPost.getItemId());
        //Add to items
        items.put(IdConverter.idLongToHexString(ItemId.class, itemPost.getItemId()), itemPost);

        return itemPost;
    }

}
